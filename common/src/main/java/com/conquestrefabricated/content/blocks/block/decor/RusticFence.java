package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

public class RusticFence extends IronBarsBlock {

    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty TOGGLE = BooleanProperty.create("toggle");

    public RusticFence(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (stateIn.getValue(WATERLOGGED)) {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return directionToNeighbour.getAxis().isHorizontal() ? stateIn.setValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour),
                        this.canAttach(neighbourState, neighbourState.isFaceSturdy(level, neighbourPos, directionToNeighbour.getOpposite()), directionToNeighbour.getOpposite()))
                .setValue(DOWN, canAttachBelow(level, currentPos.below(), stateIn)) :

                super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);

    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        BlockPos blockposNorth = blockpos.north();
        BlockPos blockposSouth = blockpos.south();
        BlockPos blockposWest = blockpos.west();
        BlockPos blockposEast = blockpos.east();
        BlockState blockstateNorth = iblockreader.getBlockState(blockposNorth);
        BlockState blockstateSouth = iblockreader.getBlockState(blockposSouth);
        BlockState blockstateWest = iblockreader.getBlockState(blockposWest);
        BlockState blockstateEast = iblockreader.getBlockState(blockposEast);

        BlockPos blockposDown = blockpos.below();
        BlockState blockstateDown = iblockreader.getBlockState(blockposDown);

        boolean attachedNorth = this.canAttach(blockstateNorth, blockstateNorth.isFaceSturdy(iblockreader, blockposNorth, Direction.SOUTH), Direction.SOUTH);
        boolean attachedEast = this.canAttach(blockstateEast, blockstateEast.isFaceSturdy(iblockreader, blockposEast, Direction.WEST), Direction.WEST);
        boolean attachedSouth = this.canAttach(blockstateSouth, blockstateSouth.isFaceSturdy(iblockreader, blockposSouth, Direction.NORTH), Direction.NORTH);
        boolean attachedWest = this.canAttach(blockstateWest, blockstateWest.isFaceSturdy(iblockreader, blockposWest, Direction.EAST), Direction.EAST);

        return this.defaultBlockState()
                .setValue(NORTH, attachedNorth)
                .setValue(EAST, attachedEast)
                .setValue(SOUTH, attachedSouth)
                .setValue(WEST, attachedWest)
                .setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER)
                .setValue(DOWN, this.canAttachBelow(blockstateDown))
                .setValue(TOGGLE, false);
    }

    public final boolean canAttach(BlockState state, boolean bool, Direction direction) {
        Block block = state.getBlock();
        boolean bl4 = block instanceof SmallDoor && SmallDoor.canWallConnect(state, direction);

        boolean bl3 = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, direction);
        return !isExceptionForConnection(state) && bool ||
                bl3 ||
                bl4 ||
                block instanceof RusticFence ||
                (block instanceof VerticalCorner && state.getValue(VerticalCorner.LAYERS) >= 3) ||
                (block instanceof VerticalCornerLessLayers && state.getValue(VerticalCornerLessLayers.LAYERS) >= 2);
    }

    public final boolean canAttachBelow(BlockState state) {
        Block block = state.getBlock();
        return ((block instanceof StairBlock && state.getValue(StairBlock.HALF) == Half.BOTTOM) ||
                (block instanceof SlabLessLayers && state.getValue(TYPE_UPDOWN) == Half.BOTTOM) ||
                (block instanceof Slab && state.getValue(TYPE_UPDOWN) == Half.BOTTOM) ||
                (block instanceof SlabBlock && state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) ||
                block instanceof Layer ||
                block instanceof SnowLayerBlock);
    }

    public final boolean canAttachBelow(LevelReader world, BlockPos pos, BlockState stateIn) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return ((block instanceof StairBlock && state.getValue(StairBlock.HALF) == Half.BOTTOM) ||
                (block instanceof SlabLessLayers && state.getValue(TYPE_UPDOWN) == Half.BOTTOM) ||
                (block instanceof Slab && state.getValue(TYPE_UPDOWN) == Half.BOTTOM) ||
                (block instanceof SlabBlock && state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) ||
                block instanceof Layer ||
                block instanceof SnowLayerBlock);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.FAIL;
        } else {
            state = state.cycle(TOGGLE);
            world.setBlock(pos, state, 3);
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE, NORTH, EAST, WEST, SOUTH, DOWN, WATERLOGGED);
    }
}
