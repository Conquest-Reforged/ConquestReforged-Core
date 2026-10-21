package com.conquestrefabricated.content.treefelling;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * Cutting a tree down with an axe brings the whole tree down: chop the foot of a trunk and the tree, with
 * its branches and canopy, topples away from you and lands with everything it yields.
 *
 * <p>Cut anywhere up the trunk and what hangs above the cut comes down; the lower trunk stays. Bigger trees take longer to cut. Sneak to chop a single block as usual. The option {@code tree_felling} in the Conquest config turns the
 * whole thing off. See {@link TreeScan} for what counts as a tree and {@link FallingTree} for the fall.</p>
 */
public final class TreeFelling {

    /** Scoreboard tag on the display entities, so any left behind by a crash can be recognised and removed. */
    public static final String TAG = "conquest_felling";
    /** Top byte of a felled block's display glow colour, which the client reads to tint it as it was in the world. */
    public static final int DISPLAY_MARKER = 0xC5 << 24;

    private static final List<FallingTree> ACTIVE = new ArrayList<>();
    private static final Set<Display.BlockDisplay> OWNED = Collections.newSetFromMap(new IdentityHashMap<>());

    private TreeFelling() {
    }

    public static boolean enabled() {
        return ConquestConfig.INSTANCE.treeFelling.get();
    }

    public static void register() {
        BlockEvent.BREAK.register((level, pos, state, player, xp) ->
                level instanceof ServerLevel serverLevel && fell(serverLevel, pos, state, player)
                        ? EventResult.interruptFalse() : EventResult.pass());
        TickEvent.SERVER_LEVEL_POST.register(TreeFelling::tick);
        LifecycleEvent.SERVER_STOPPING.register(server -> {
            for (FallingTree tree : new ArrayList<>(ACTIVE)) {
                tree.finishNow();
            }
            ACTIVE.clear();
            OWNED.clear();
        });
        // A tree that was mid-fall when the world was saved would otherwise hang about for ever.
        EntityEvent.ADD.register((entity, level) ->
                entity instanceof Display.BlockDisplay && entity.entityTags().contains(TAG) && !OWNED.contains(entity)
                        ? EventResult.interruptFalse() : EventResult.pass());
    }

    private static boolean fellsWith(Player player) {
        return enabled() && !player.isShiftKeyDown() && !player.isCreative() && player.getMainHandItem().is(ItemTags.AXES);
    }

    /** A tree this big takes this many times as long to cut through (the slowest being {@link #MAX_SLOWDOWN}). */
    private static final float MAX_SLOWDOWN = 6.0F;
    /** Wood blocks, with each leaf counting for a tenth, that add one to the slowdown. */
    private static final float SIZE_PER_STEP = 5.0F;

    private static BlockPos slowPos;
    private static int slowTick = Integer.MIN_VALUE;
    private static float slowFactor = 1.0F;

    /**
     * How much slower than normal to break the block at {@code pos}: 1 unless it is the cut that would fell a
     * tree, otherwise more the bigger the tree is, up to a cap. Called every tick of mining on both sides,
     * so the answer is kept for a second at a time.
     */
    public static float slowdown(Player player, BlockGetter level, BlockPos pos) {
        if (!fellsWith(player) || !TreeScan.isWood(level.getBlockState(pos))) {
            return 1.0F;
        }
        if (!pos.equals(slowPos) || Math.abs(player.tickCount - slowTick) > 20) {
            slowPos = pos.immutable();
            slowTick = player.tickCount;
            TreeScan.Result tree = TreeScan.scan(level, pos);
            slowFactor = tree == null ? 1.0F
                    : 1.0F + Math.min(MAX_SLOWDOWN - 1.0F, (tree.wood().size() + tree.leaves().size() / 10.0F) / SIZE_PER_STEP);
        }
        return slowFactor;
    }

    private static void tick(ServerLevel level) {
        if (ACTIVE.isEmpty()) {
            return;
        }
        for (FallingTree tree : new ArrayList<>(ACTIVE)) {
            if (tree.level() == level) {
                tree.tick();
                if (tree.finished()) {
                    ACTIVE.remove(tree);
                }
            }
        }
        if (ACTIVE.isEmpty()) {
            OWNED.clear();
        }
    }

    /** Fells the tree {@code player} has just cut at {@code pos}, if there is one to fell. Returns whether it did. */
    private static boolean fell(ServerLevel level, BlockPos pos, BlockState cutState, ServerPlayer player) {
        ItemStack tool = player.getMainHandItem();
        if (!fellsWith(player)) {
            return false;
        }
        TreeScan.Result tree = TreeScan.scan(level, pos);
        if (tree == null) {
            return false;
        }
        for (BlockPos wood : tree.wood()) {
            if (!level.mayInteract(player, wood) || player.blockActionRestricted(level, wood, player.gameMode.getGameModeForPlayer())) {
                return false;
            }
        }

        // Note each block, what it gives and the light it had, before any of it is moved.
        List<FallingTree.Piece> pieces = new ArrayList<>();
        List<BlockPos> everything = new ArrayList<>(tree.wood());
        everything.addAll(tree.leaves());
        LongOpenHashSet fallingSet = new LongOpenHashSet();
        for (BlockPos p : everything) {
            fallingSet.add(p.asLong());
        }
        int woodCount = tree.wood().size();
        for (int i = 0; i < everything.size(); i++) {
            BlockPos p = everything.get(i);
            BlockState state = level.getBlockState(p);
            boolean wood = i < woodCount;
            List<ItemStack> drops = new ArrayList<>();
            for (ItemStack drop : Block.getDrops(state, level, p, null, player, tool)) {
                merge(drops, drop);
            }
            boolean shown = state.getRenderShape() != RenderShape.INVISIBLE && !buried(level, p, state, fallingSet);
            int[] light = lightOf(level, p, state);
            pieces.add(new FallingTree.Piece(p, state, wood, wood && !TreeScan.isBranch(state), shown, light[0], light[1], drops));
        }

        // Fall away from the player, hinged on the stump's edge on that side.
        Vec3 centre = Vec3.atCenterOf(pos);
        double dx = centre.x - player.getX();
        double dz = centre.z - player.getZ();
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < 1.0E-3) {
            Vec3 look = player.getLookAngle();
            dx = look.x;
            dz = look.z;
            length = Math.max(Math.sqrt(dx * dx + dz * dz), 1.0E-3);
        }
        Vec3 fall = new Vec3(dx / length, 0, dz / length);
        double reach = 0;
        for (BlockPos p : tree.wood()) {
            if (p.getY() == pos.getY()) {
                reach = Math.max(reach, (p.getX() - pos.getX()) * fall.x + (p.getZ() - pos.getZ()) * fall.z);
            }
        }
        double half = 0.5 * (Math.abs(fall.x) + Math.abs(fall.z));
        Vec3 pivot = new Vec3(centre.x + fall.x * (reach + half), hingeHeight(level, pos, cutState), centre.z + fall.z * (reach + half));

        int height = 0;
        for (BlockPos p : tree.wood()) {
            height = Math.max(height, p.getY() - pos.getY());
        }
        FallingTree felled = FallingTree.plan(level, pieces, pivot, fall, height, cutState.getSoundType().getBreakSound());
        if (felled == null) {
            return false;
        }

        // Remove: the supports go with the leaves, taking nothing.
        for (BlockPos p : everything) {
            level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
        for (BlockPos p : tree.supports()) {
            level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        level.levelEvent(2001, pos, Block.getId(cutState));
        level.playSound(null, centre.x, centre.y, centre.z, cutState.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.5F, 0.6F);
        player.causeFoodExhaustion(0.005F);
        int damage = Math.min(woodCount, Math.max(0, tool.getMaxDamage() - tool.getDamageValue() - 1));
        if (tool.isDamageableItem() && damage > 0) {
            tool.hurtAndBreak(damage, player, EquipmentSlot.MAINHAND);
        }

        felled.spawn(OWNED::add);
        ACTIVE.add(felled);
        return true;
    }

    /**
     * Where the cut block's foot is. A trunk shape with {@code down} set reaches down into the layer (or slab)
     * beneath it, so the tree hinges at the top of that layer, or a whole block down if what is below has no
     * layers.
     */
    private static double hingeHeight(ServerLevel level, BlockPos pos, BlockState cut) {
        if (!cut.hasProperty(BlockStateProperties.DOWN) || !cut.getValue(BlockStateProperties.DOWN)) {
            return pos.getY();
        }
        BlockState below = level.getBlockState(pos.below());
        if (below.hasProperty(BlockStateProperties.LAYERS)) {
            return pos.getY() - 1 + below.getValue(BlockStateProperties.LAYERS) / 8.0;
        }
        return pos.getY() - 1;
    }

    /**
     * The light to draw a block with once it has left its place: {block, sky}. In the world each face is lit by
     * the space in front of it, so this is the average over the open faces (and, for leaves, which let light
     * in, the block's own). Averaging rather than taking the brightest keeps the inside of a canopy as dim as
     * it looked.
     */
    private static int[] lightOf(ServerLevel level, BlockPos p, BlockState state) {
        int sky = 0;
        int block = 0;
        int n = 0;
        if (!state.canOcclude()) {
            sky += level.getBrightness(LightLayer.SKY, p);
            block += level.getBrightness(LightLayer.BLOCK, p);
            n++;
        }
        for (Direction dir : Direction.values()) {
            BlockPos at = p.relative(dir);
            if (level.getBlockState(at).canOcclude()) {
                continue;
            }
            sky += level.getBrightness(LightLayer.SKY, at);
            block += level.getBrightness(LightLayer.BLOCK, at);
            n++;
        }
        if (n == 0) {
            return new int[] {level.getBrightness(LightLayer.BLOCK, p.above()), level.getBrightness(LightLayer.SKY, p.above())};
        }
        return new int[] {block / n, sky / n};
    }

    /** A full block with a full block on every side shows nothing, so needs no display. */
    private static boolean buried(ServerLevel level, BlockPos p, BlockState state, LongOpenHashSet falling) {
        if (!full(level, p, state)) {
            return false;
        }
        for (Direction dir : Direction.values()) {
            BlockPos n = p.relative(dir);
            if (!falling.contains(n.asLong()) || !full(level, n, level.getBlockState(n))) {
                return false;
            }
        }
        return true;
    }

    private static boolean full(ServerLevel level, BlockPos p, BlockState state) {
        return state.isCollisionShapeFullBlock(level, p);
    }

    static void merge(List<ItemStack> into, ItemStack drop) {
        if (drop.isEmpty()) {
            return;
        }
        for (ItemStack have : into) {
            if (ItemStack.isSameItemSameComponents(have, drop) && have.getCount() < have.getMaxStackSize()) {
                int moved = Math.min(drop.getCount(), have.getMaxStackSize() - have.getCount());
                have.grow(moved);
                drop.shrink(moved);
                if (drop.isEmpty()) {
                    return;
                }
            }
        }
        into.add(drop.copy());
    }
}
