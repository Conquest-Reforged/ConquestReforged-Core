package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.VerticalSlab;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.conquestrefabricated.core.block.properties.Waterloggable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.Map;
import java.util.Objects;

@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class LichenMoss extends AbstractBush implements Waterloggable {

    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST)));

    public LichenMoss(Properties properties) {
        super(((BlockSettingsAccessor)properties).setCustomOffsetter(CustomOffsetType.LAYER_XYZ_LICHEN).offsetType(OffsetType.NONE).dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(OFFSET_TOGGLE, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter blockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        boolean isSlab = PlacementHelper.isFacingSlab(context);

        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.east();
        BlockPos blockpos3 = blockpos.south();
        BlockPos blockpos4 = blockpos.west();
        BlockPos blockposDown = blockpos.below();
        BlockState blockstateNorth = blockreader.getBlockState(blockpos1);
        BlockState blockstateEast = blockreader.getBlockState(blockpos2);
        BlockState blockstateSouth = blockreader.getBlockState(blockpos3);
        BlockState blockstateWest = blockreader.getBlockState(blockpos4);
        BlockState blockstateDown = blockreader.getBlockState(blockposDown);
        return Objects.requireNonNull(super.getStateForPlacement(context))
                .setValue(NORTH, blockstateNorth.isFaceSturdy(blockreader, blockpos1, Direction.SOUTH) || (blockstateNorth.getBlock() instanceof VerticalSlab && blockstateNorth.getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.SOUTH))
                .setValue(EAST, blockstateEast.isFaceSturdy(blockreader, blockpos1, Direction.WEST) || (blockstateEast.getBlock() instanceof VerticalSlab && blockstateEast.getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.WEST))
                .setValue(SOUTH, blockstateSouth.isFaceSturdy(blockreader, blockpos3, Direction.NORTH) || (blockstateSouth.getBlock() instanceof VerticalSlab && blockstateSouth.getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.NORTH))
                .setValue(WEST, blockstateWest.isFaceSturdy(blockreader, blockpos4, Direction.EAST) || (blockstateWest.getBlock() instanceof VerticalSlab && blockstateWest.getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.EAST))
                .setValue(DOWN, blockstateDown.isFaceSturdy(blockreader, blockposDown, Direction.DOWN))
                .setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER)
                .setValue(OFFSET_TOGGLE, isSlab);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (stateIn.getValue(WATERLOGGED)) {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return directionToNeighbour.getAxis().getPlane() == Direction.Plane.HORIZONTAL ?
                stateIn.setValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour), neighbourState.isFaceSturdy(level, neighbourPos, directionToNeighbour.getOpposite()) || (neighbourState.getBlock() instanceof VerticalSlab && neighbourState.getValue(BlockStateProperties.HORIZONTAL_FACING) == directionToNeighbour))
                : super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, DOWN, WATERLOGGED, OFFSET_TOGGLE);
    }

    @Override
    public float getMaxHorizontalOffset() {
        return 0.0F;
    }

    @Override
    public float getMaxVerticalOffset() {
        return 0.0F;
    }


}
