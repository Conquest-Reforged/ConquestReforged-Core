package com.conquestrefabricated.content.blocks.block.glass;

import com.conquestrefabricated.content.blocks.block.*;
import com.conquestrefabricated.content.blocks.block.arch.Arch;
import com.conquestrefabricated.content.blocks.block.arch.ArchHalf;
import com.conquestrefabricated.content.blocks.block.arch.ArchSmall;
import com.conquestrefabricated.content.blocks.block.arch.ArchTwoMeter;
import com.conquestrefabricated.content.blocks.block.decor.FenceLayered;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@Assets(
        state = @State(name = "%s_pane", template = "parent_pane"),
        item = @Model(name = "item/%s_pane", parent = "block/%s_pane_ns", template = "item/parent_pane"),
        render = @Render(RenderLayer.CUTOUT),
        block = {
                @Model(name = "block/%s_pane_n", template = "block/parent_flatpane_n"),
                @Model(name = "block/%s_pane_ne", template = "block/parent_flatpane_ne"),
                @Model(name = "block/%s_pane_ns", template = "block/parent_flatpane_ns"),
                @Model(name = "block/%s_pane_nse", template = "block/parent_flatpane_nse"),
                @Model(name = "block/%s_pane_nsew", template = "block/parent_flatpane_nsew"),
                @Model(name = "block/%s_pane_post", template = "block/parent_flatpane_post"),
                @Model(name = "block/%s_pane_n_up", template = "block/parent_flatpane_n_up"),
                @Model(name = "block/%s_pane_ne_up", template = "block/parent_flatpane_ne_up"),
                @Model(name = "block/%s_pane_ns_up", template = "block/parent_flatpane_ns_up"),
                @Model(name = "block/%s_pane_nse_up", template = "block/parent_flatpane_nse_up"),
                @Model(name = "block/%s_pane_nsew_up", template = "block/parent_flatpane_nsew_up"),
                @Model(name = "block/%s_pane_post_up", template = "block/parent_flatpane_post_up"),
                @Model(name = "block/%s_pane_n_updown", template = "block/parent_flatpane_n_updown"),
                @Model(name = "block/%s_pane_ne_updown", template = "block/parent_flatpane_ne_updown"),
                @Model(name = "block/%s_pane_ns_updown", template = "block/parent_flatpane_ns_updown"),
                @Model(name = "block/%s_pane_nse_updown", template = "block/parent_flatpane_nse_updown"),
                @Model(name = "block/%s_pane_nsew_updown", template = "block/parent_flatpane_nsew_updown"),
                @Model(name = "block/%s_pane_post_updown", template = "block/parent_flatpane_post_updown"),
                @Model(name = "block/%s_pane_n_down", template = "block/parent_flatpane_n_down"),
                @Model(name = "block/%s_pane_ne_down", template = "block/parent_flatpane_ne_down"),
                @Model(name = "block/%s_pane_ns_down", template = "block/parent_flatpane_ns_down"),
                @Model(name = "block/%s_pane_nse_down", template = "block/parent_flatpane_nse_down"),
                @Model(name = "block/%s_pane_nsew_down", template = "block/parent_flatpane_nsew_down"),
                @Model(name = "block/%s_pane_post_down", template = "block/parent_flatpane_post_down")
        }
)
public class Pane extends IronBarsBlock {

    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public Pane(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (stateIn.getValue(WATERLOGGED)) {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return directionToNeighbour.getAxis().isHorizontal() ? stateIn.setValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour),
                this.canAttach(neighbourState, neighbourState.isFaceSturdy(level, neighbourPos, directionToNeighbour.getOpposite()), directionToNeighbour.getOpposite()))
                .setValue(UP, canAttachAbove(level, currentPos.above(), stateIn))
                .setValue(DOWN, canAttachBelow(level, currentPos.below(), stateIn)) :

                super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random)
                        .setValue(UP, canAttachAbove(level, currentPos.above(), stateIn))
                        .setValue(DOWN, canAttachBelow(level, currentPos.below(), stateIn));

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

        BlockPos blockposUp = blockpos.above();
        BlockState blockstateUp = iblockreader.getBlockState(blockposUp);
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
                .setValue(UP, this.canAttachAbove(blockstateUp, attachedNorth, attachedEast, attachedSouth, attachedWest))
                .setValue(DOWN, this.canAttachBelow(blockstateDown, attachedNorth, attachedEast, attachedSouth, attachedWest));
    }

    public final boolean canAttachAbove(BlockState state, boolean attachedNorth, boolean attachedEast, boolean attachedSouth, boolean attachedWest) {
        Block block = state.getBlock();
        return ((block instanceof StairBlock && state.getValue(StairBlock.HALF) == Half.TOP && state.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT && (canAttachToStair(state, attachedNorth, attachedEast, attachedSouth, attachedWest))) ||
                (block instanceof SlabLessLayers && state.getValue(TYPE_UPDOWN) == Half.TOP) ||
                (block instanceof Slab && state.getValue(TYPE_UPDOWN) == Half.TOP) ||
                (block instanceof SlabBlock && state.getValue(SlabBlock.TYPE) == SlabType.TOP) ||
                block instanceof Arch ||
                block instanceof ArchSmall ||
                block instanceof ArchTwoMeter ||
                block instanceof ArchHalf);
    }

    public final boolean canAttachBelow(BlockState state, boolean attachedNorth, boolean attachedEast, boolean attachedSouth, boolean attachedWest) {
        Block block = state.getBlock();
        return ((block instanceof StairBlock && state.getValue(StairBlock.HALF) == Half.BOTTOM && state.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT && (canAttachToStair(state, attachedNorth, attachedEast, attachedSouth, attachedWest))) ||
                (block instanceof SlabLessLayers && state.getValue(TYPE_UPDOWN) == Half.BOTTOM) ||
                (block instanceof Slab && state.getValue(TYPE_UPDOWN) == Half.BOTTOM) ||
                (block instanceof SlabBlock && state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) ||
                block instanceof Arch ||
                block instanceof ArchSmall ||
                block instanceof ArchTwoMeter ||
                block instanceof ArchHalf);
    }

    private boolean canAttachToStair(BlockState state, boolean attachedNorth, boolean attachedEast, boolean attachedSouth, boolean attachedWest) {
        switch (state.getValue(StairBlock.FACING)) {
            default:
            case NORTH:
            case SOUTH: {
                return !attachedEast && !attachedWest;
            }
            case EAST:
            case WEST:
                return !attachedNorth && !attachedSouth;
        }
    }

    private boolean canAttachToStair(BlockState state, BlockState stateIn) {
        switch (state.getValue(StairBlock.FACING)) {
            default:
            case NORTH:
            case SOUTH: {
                return !stateIn.getValue(IronBarsBlock.EAST) && !stateIn.getValue(IronBarsBlock.WEST);
            }
            case EAST:
            case WEST:
                return !stateIn.getValue(IronBarsBlock.NORTH) && !stateIn.getValue(IronBarsBlock.SOUTH);
        }
    }

    public final boolean canAttachAbove(LevelReader world, BlockPos pos, BlockState stateIn) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return ((block instanceof StairBlock && state.getValue(StairBlock.HALF) == Half.TOP && state.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT && canAttachToStair(state, stateIn)) ||
                (block instanceof SlabLessLayers && state.getValue(TYPE_UPDOWN) == Half.TOP) ||
                (block instanceof Slab && state.getValue(TYPE_UPDOWN) == Half.TOP) ||
                (block instanceof SlabBlock && state.getValue(SlabBlock.TYPE) == SlabType.TOP) ||
                block instanceof Arch ||
                block instanceof ArchSmall ||
                block instanceof ArchTwoMeter ||
                block instanceof ArchHalf);
    }

    public final boolean canAttachBelow(LevelReader world, BlockPos pos, BlockState stateIn) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return ((block instanceof StairBlock && state.getValue(StairBlock.HALF) == Half.BOTTOM && state.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT && canAttachToStair(state, stateIn)) ||
                (block instanceof SlabLessLayers && state.getValue(TYPE_UPDOWN) == Half.BOTTOM) ||
                (block instanceof Slab && state.getValue(TYPE_UPDOWN) == Half.BOTTOM) ||
                (block instanceof SlabBlock && state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) ||
                block instanceof Arch ||
                block instanceof ArchSmall ||
                block instanceof ArchTwoMeter ||
                block instanceof ArchHalf);
    }

    public final boolean canAttach(BlockState state, boolean bool, Direction direction) {
        Block block = state.getBlock();
        return !isExceptionForConnection(state) && bool ||
                block instanceof IronBarsBlock || state.getBlock() instanceof FenceLayered || state.getBlock() instanceof FenceLayered.Half ||
                (block instanceof VerticalCorner && state.getValue(VerticalCorner.LAYERS) >= 3) ||
                (block instanceof VerticalCornerLessLayers && state.getValue(VerticalCornerLessLayers.LAYERS) >= 2) ||
                (block instanceof VerticalSlab && state.getValue(VerticalSlab.LAYERS) >= 3 && canAttachToVertSlab(state, direction)) ||
                (block instanceof VerticalSlabLessLayers && state.getValue(VerticalSlabLessLayers.LAYERS) >= 2 && canAttachToVertSlab(state, direction)) ||
                (block instanceof VerticalQuarter && state.getValue(VerticalQuarter.LAYERS) >= 3 && canAttachToVertQuarter(state, direction)) ||
                (block instanceof VerticalQuarterLessLayers && state.getValue(VerticalQuarterLessLayers.LAYERS) >= 2 && canAttachToVertQuarter(state, direction)) ||
                block instanceof StairBlock ||
                block instanceof Slab ||
                block instanceof SlabBlock ||
                block instanceof Arch ||
                block instanceof ArchSmall ||
                block instanceof ArchTwoMeter ||
                block instanceof ArchHalf;
    }

    private boolean canAttachToVertQuarter(BlockState state, Direction direction) {
        return (state.getValue(VerticalQuarter.DIRECTION) != direction && state.getValue(VerticalQuarter.DIRECTION).getCounterClockWise() != direction);
    }

    public final boolean canAttachToVertSlab(BlockState state, Direction direction) {
        return state.getValue(VerticalSlab.DIRECTION) != direction;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN, WATERLOGGED);
    }
}
