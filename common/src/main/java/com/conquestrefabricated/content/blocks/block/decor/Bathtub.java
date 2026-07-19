package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.BlockVoxelShapes;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.CauldronBehavior;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class Bathtub extends HorizontalDirectionalBlock {
    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;

    public static final MapCodec<Bathtub> CODEC = simpleCodec(Bathtub::new);

    public static final List<VoxelShape> tubShapes = BlockVoxelShapes.makeNSEWShapes(Stream.of(
            Block.box(0, 0, 0, 16, 5, 16),
            Block.box(0, 5, 0, 2, 15, 16),
            Block.box(14, 5, 0, 16, 15, 16),
            Block.box(2, 5, 14, 14, 15, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());

    private final CauldronBehavior behavior;

    protected final CauldronInteraction.Dispatcher interactions = CauldronInteractions.EMPTY;

    public Bathtub(Props props) {
        super(props
                .customOffsetType(CustomOffsetType.LAYER_XYZ)
                .offset(BlockBehaviour.OffsetType.NONE)
                .dynamicBounds(true)
                .toSettings());
        this.behavior = new CauldronBehavior(tubShapes);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PART, BedPart.FOOT)
                .setValue(OFFSET_TOGGLE, false));
    }

    // Secondary constructor for codec reconstruction
    public Bathtub(BlockBehaviour.Properties settings) {
        super(settings
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .dynamicShape());
        this.behavior = new CauldronBehavior(tubShapes);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PART, BedPart.FOOT)
                .setValue(OFFSET_TOGGLE, false));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(PART) == BedPart.FOOT) {
            switch (state.getValue(FACING)) {
                case NORTH:
                    return tubShapes.get(0);
                case EAST:
                    return tubShapes.get(1);
                case SOUTH:
                    tubShapes.get(2);
                case WEST:
                    tubShapes.get(3);
            }
        } else {
            switch (state.getValue(FACING)) {
                case NORTH:
                    return tubShapes.get(2);
                case EAST:
                    return tubShapes.get(3);
                case SOUTH:
                    tubShapes.get(0);
                case WEST:
                    tubShapes.get(1);
            }
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(PART) == BedPart.FOOT) {
            switch (state.getValue(FACING)) {
                case NORTH:
                    return tubShapes.get(0);
                case EAST:
                    return tubShapes.get(1);
                case SOUTH:
                    tubShapes.get(2);
                case WEST:
                    tubShapes.get(3);
            }
        } else {
            switch (state.getValue(FACING)) {
                case NORTH:
                    return tubShapes.get(2);
                case EAST:
                    return tubShapes.get(3);
                case SOUTH:
                    tubShapes.get(0);
                case WEST:
                    tubShapes.get(1);
            }
        }
        return super.getShape(state, world, pos, context);
    }

    // -------------------------------------------------------------------------
    // Sync helper
    // -------------------------------------------------------------------------

    /**
     * Returns the BlockPos of the other half of this bathtub, or null if the
     * neighbouring block is not the same bathtub block.
     */
    @Nullable
    private BlockPos getOtherHalfPos(BlockState state, BlockPos pos) {
        Direction dir = getNeighbourDirection(state.getValue(PART), state.getValue(FACING));
        return pos.relative(dir);
    }

    /**
     * Copies the current LEVEL value from {@code state} to the partner half,
     * but only when the partner is actually the other half of this bathtub.
     * Safe to call on both client and server; does nothing on the client.
     */
    private void syncLevelToOtherHalf(Level world, BlockPos pos, BlockState state) {
        if (world.isClientSide()) return;

        BlockPos otherPos = getOtherHalfPos(state, pos);
        if (otherPos == null) return;

        BlockState otherState = world.getBlockState(otherPos);
        if (!otherState.is(this)) return;
        if (otherState.getValue(PART) == state.getValue(PART)) return; // sanity guard

        int currentLevel = state.getValue(CauldronBehavior.LEVEL);
        if (otherState.getValue(CauldronBehavior.LEVEL) != currentLevel) {
            world.setBlock(otherPos, otherState.setValue(CauldronBehavior.LEVEL, currentLevel), 3);
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        net.minecraft.core.cauldron.CauldronInteraction cauldronBehavior = this.interactions.get(stack);
        syncLevelToOtherHalf(world, pos, world.getBlockState(pos));
        return cauldronBehavior.interact(state, world, pos, player, hand, stack);
    }

    @Override
    public void handlePrecipitation(BlockState state, Level world, BlockPos pos,
                                  Biome.Precipitation precipitation) {
        behavior.precipitationTick(state, world, pos, precipitation);
        syncLevelToOtherHalf(world, pos, world.getBlockState(pos));
    }

    // -------------------------------------------------------------------------
    // Passive sync via neighbor updates (covers anything CauldronBehavior
    // does that we might miss, and external changes)
    // -------------------------------------------------------------------------

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        // First run the existing bed-part breakage logic
        BlockState updated = super_getStateForNeighborUpdate(
                stateIn, directionToNeighbour, neighbourState, level, currentPos, neighbourPos, ticks, random);

        // If the update came from our partner half and it still exists, mirror LEVEL
        Direction partnerDir = getNeighbourDirection(stateIn.getValue(PART), stateIn.getValue(FACING));
        if (directionToNeighbour == partnerDir && neighbourState.is(this)
                && neighbourState.getValue(PART) != stateIn.getValue(PART)) {
            int partnerLevel = neighbourState.getValue(CauldronBehavior.LEVEL);
            if (updated.getValue(CauldronBehavior.LEVEL) != partnerLevel) {
                updated = updated.setValue(CauldronBehavior.LEVEL, partnerLevel);
            }
        }

        return updated;
    }

    /**
     * Extracted copy of the original getStateForNeighborUpdate logic so that
     * the override above can call it cleanly without duplicating code.
     */
    private BlockState super_getStateForNeighborUpdate(BlockState blockState, Direction direction,
                                                       BlockState facingState,
                                                       LevelReader levelAccessor,
                                                       BlockPos currentPos, BlockPos facingPos, ScheduledTickAccess ticks, RandomSource random) {
        if (direction == getNeighbourDirection(blockState.getValue(PART), blockState.getValue(FACING))) {
            return facingState.is(this) && facingState.getValue(PART) != blockState.getValue(PART)
                    ? super.updateShape(blockState, levelAccessor, ticks, currentPos, direction, facingPos, facingState, random)
                    : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(blockState, levelAccessor, ticks, currentPos, direction, facingPos, facingState, random);
        }
    }


    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType type) {
        return false;
    }

    private static Direction getNeighbourDirection(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative()) {
            BedPart bedpart = state.getValue(PART);
            if (bedpart == BedPart.FOOT) {
                BlockPos blockpos = pos.relative(getNeighbourDirection(bedpart, state.getValue(FACING)));
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, entity, itemStack);
        if (!level.isClientSide()) {
            BlockPos blockpos = pos.relative(state.getValue(FACING));
            level.setBlockAndUpdate(blockpos, state.setValue(PART, BedPart.HEAD));
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    public long getSeed(BlockState state, BlockPos pos) {
        BlockPos blockpos = pos.relative(state.getValue(FACING), state.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed(blockpos.getX(), pos.getY(), blockpos.getZ());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OFFSET_TOGGLE, CauldronBehavior.LEVEL);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return behavior.hasComparatorOutput(state);
    }

    @Override
    protected MapCodec<? extends Bathtub> codec() {
        return CODEC;
    }
}
