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
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
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
 * <p>Sneak to chop a single block as usual. The option {@code tree_felling} in the Conquest config turns the
 * whole thing off. See {@link TreeScan} for what counts as a tree and {@link FallingTree} for the fall.</p>
 */
public final class TreeFelling {

    /** Scoreboard tag on the display entities, so any left behind by a crash can be recognised and removed. */
    public static final String TAG = "conquest_felling";

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
        if (!enabled() || player.isShiftKeyDown() || player.isCreative() || !tool.is(ItemTags.AXES)) {
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

        // Pull the tree out of the world, noting what each block was and what it gives.
        List<FallingTree.Piece> pieces = new ArrayList<>();
        List<ItemStack> drops = new ArrayList<>();
        List<BlockPos> shown = new ArrayList<>();
        List<BlockPos> everything = new ArrayList<>(tree.wood());
        everything.addAll(tree.leaves());
        LongOpenHashSet fallingSet = new LongOpenHashSet();
        for (BlockPos p : everything) {
            fallingSet.add(p.asLong());
        }
        for (BlockPos p : everything) {
            BlockState state = level.getBlockState(p);
            for (ItemStack drop : Block.getDrops(state, level, p, null, player, tool)) {
                merge(drops, drop);
            }
            if (state.getRenderShape() != RenderShape.INVISIBLE && !buried(level, p, state, fallingSet)) {
                pieces.add(new FallingTree.Piece(p, state));
                shown.add(p);
            }
        }
        int woodCount = tree.wood().size();

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
        Vec3 pivot = new Vec3(centre.x + fall.x * (reach + half), pos.getY(), centre.z + fall.z * (reach + half));
        Vec3 axis = new Vec3(fall.z, 0, -fall.x);

        // How far it gets before the ground stops it (it lies flat on level ground).
        double step = Math.toRadians(2);
        double limit = Math.toRadians(90);
        double angle = 0;
        while (angle + step <= limit && !FallingTree.blocked(level, shown, pivot, axis, angle + step)) {
            angle += step;
        }
        if (angle < Math.toRadians(8)) {
            return false;
        }

        int height = 0;
        for (BlockPos p : tree.wood()) {
            height = Math.max(height, p.getY() - pos.getY());
        }
        int duration = Math.max(70, Math.min(180, 50 + 4 * height));

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

        FallingTree felled = new FallingTree(level, pieces, drops, pivot, fall, angle, duration, cutState.getSoundType().getBreakSound());
        felled.spawn(OWNED::add);
        ACTIVE.add(felled);
        return true;
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

    private static void merge(List<ItemStack> into, ItemStack drop) {
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
