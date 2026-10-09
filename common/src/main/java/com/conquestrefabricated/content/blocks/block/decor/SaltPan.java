package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.salt.Salt;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A shallow tray of sea water left to the sun, and the salt it leaves behind.
 *
 * <p>The pan holds up to three levels of {@link #BRINE} and, once that has dried, up to three of
 * {@link #SALT} crust. It works on random ticks, so a great many can be laid out as flats at no cost:</p>
 * <ul>
 *   <li><b>Filling.</b> A pan within two blocks of sea water (sideways, or up to two above it), or beside a full pan, slowly takes
 *       a level of brine. A bucket of sea water fills it at once, a bottle gives one level.</li>
 *   <li><b>Drying.</b> In daylight, under open sky and out of the rain, brine dries a level at a time
 *       and leaves a level of crust. The hotter the biome the faster; below freezing it does not dry.
 *       A pan with a full crust stops until it is harvested.</li>
 *   <li><b>Rain.</b> Falls into an open pan and washes a level of crust away.</li>
 *   <li><b>Harvest.</b> An empty hand, or a shovel, takes the crust as the salt item named by the
 *       block's {@code salt} property, {@code yield} to a level (default 2).</li>
 * </ul>
 *
 * <p>Everything stops when {@link Salt#enabled()} is off.</p>
 */
@Render(RenderLayer.CUTOUT)
public class SaltPan extends Block {

    public static final IntegerProperty BRINE = IntegerProperty.create("brine", 0, Salt.MAX_BRINE);
    public static final IntegerProperty SALT = IntegerProperty.create("salt", 0, Salt.MAX_BRINE);

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 5, 16);

    /** Looked up when used, not when built: the item is registered after the blocks that name it. */
    private final Identifier salt;
    private final int yield;

    public SaltPan(Props props) {
        super(props.toSettings());
        this.salt = Identifier.parse(props.get("salt", String.class));
        this.yield = props.getOrDefault("yield", Integer.class, 2);
        this.registerDefaultState(this.stateDefinition.any().setValue(BRINE, 0).setValue(SALT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BRINE, SALT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    // ---------------------------------------------------------------------------------- working

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!Salt.enabled()) {
            return;
        }
        BlockPos above = pos.above();
        boolean open = level.canSeeSky(above);
        int brine = state.getValue(BRINE);
        int salt = state.getValue(SALT);

        if (open && level.isRainingAt(above)) {
            if (salt > 0) {
                level.setBlock(pos, state.setValue(SALT, salt - 1), Block.UPDATE_CLIENTS);
            }
            return;
        }

        BlockState next = state;
        if (brine < Salt.MAX_BRINE && salt < Salt.MAX_BRINE && random.nextBoolean() && fed(level, pos)) {
            next = next.setValue(BRINE, ++brine);
        }
        if (brine > 0 && salt < Salt.MAX_BRINE && open && level.isBrightOutside()
                && random.nextFloat() < dryingChance(level, pos)) {
            next = next.setValue(BRINE, brine - 1).setValue(SALT, salt + 1);
        }
        if (next != state) {
            level.setBlock(pos, next, Block.UPDATE_CLIENTS);
        }
    }

    /** How likely a random tick in sunlight is to dry a level: nothing below freezing, near certain in a desert. */
    private static float dryingChance(Level level, BlockPos pos) {
        float temperature = level.getBiome(pos).value().getBaseTemperature();
        if (temperature <= 0.0F) {
            return 0.0F;
        }
        return Math.min(0.9F, 0.15F + 0.35F * temperature);
    }

    /** Sea within a couple of blocks, or a full pan beside it, to draw from. */
    private static boolean fed(Level level, BlockPos pos) {
        if (Salt.seaNear(level, pos)) {
            return true;
        }
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos beside = pos.relative(side);
            BlockState other = level.getBlockState(beside);
            if (other.getBlock() instanceof SaltPan && other.getValue(BRINE) == Salt.MAX_BRINE) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------------- interaction

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit) {
        if (!Salt.enabled()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        int brine = state.getValue(BRINE);
        boolean bucket = Salt.isSeaBucket(stack);
        if (bucket || Salt.isSeaBottle(stack)) {
            if (brine >= Salt.MAX_BRINE || state.getValue(SALT) >= Salt.MAX_BRINE) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            if (!level.isClientSide()) {
                int filled = bucket ? Salt.MAX_BRINE : brine + 1;
                level.setBlockAndUpdate(pos, state.setValue(BRINE, filled));
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, Salt.emptied(stack)));
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.is(ItemTags.SHOVELS) && state.getValue(SALT) > 0) {
            return harvest(state, level, pos, player);
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!Salt.enabled() || !player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        if (state.getValue(SALT) > 0) {
            return harvest(state, level, pos, player);
        }
        if (state.getValue(BRINE) > 0 && !level.isClientSide()) {
            player.sendOverlayMessage(Component.translatable("message.conquest.salt_pan.drying"));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private InteractionResult harvest(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide()) {
            int count = state.getValue(SALT) * this.yield;
            level.setBlockAndUpdate(pos, state.setValue(SALT, 0));
            Salt.give(player, new ItemStack(BuiltInRegistries.ITEM.getValue(this.salt), count));
            level.playSound(null, pos, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 0.8F, 1.2F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        // A pan broken with crust in it spills the salt; the brine just soaks away.
        int crust = state.getValue(SALT);
        if (crust > 0 && !movedByPiston) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    new ItemStack(BuiltInRegistries.ITEM.getValue(this.salt), crust * this.yield));
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        // 0-3 for brine, 4-15 once there is crust: a comparator can tell wet, drying and ready apart.
        int crust = state.getValue(SALT);
        return crust > 0 ? 3 + crust * 4 : state.getValue(BRINE);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
}
