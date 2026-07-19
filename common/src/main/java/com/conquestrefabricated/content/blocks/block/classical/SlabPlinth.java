package com.conquestrefabricated.content.blocks.block.classical;

import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.WaterloggedShape;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

//only used for variant name
@Assets(
        state = @State(name = "%s_slab", template = "parent_doric_base_vertical_slab"),
        item = @Model(name = "item/%s_vertical_slab", parent = "block/%s_vertical_slab", template = "item/parent_vertical_slab"),
        block = {
                @Model(name = "block/%s_vertical_slab", template = "block/parent_doric_base_vertical_slab"),
        }
)
public class SlabPlinth extends WaterloggedShape {

    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST)));
    private static final VoxelShape BOTTOM_SHAPE = Block.box(0.1D, 0.1D, 0.1D, 15.9D, 8.0D, 15.9D);
    private static final VoxelShape TOP_SHAPE = Block.box(0.1D, 8.0D, 0.1D, 15.9D, 15.9D, 15.9D);

    public SlabPlinth(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(TYPE_UPDOWN, Half.BOTTOM).setValue(WATERLOGGED, false));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        if (state.getValue(TYPE_UPDOWN) == Half.TOP) {
            return TOP_SHAPE;
        } else {
            return BOTTOM_SHAPE;
        }
    }

    public boolean func_220111_a(BlockState state, BlockState stateOriginal) {
        Block block = state.getBlock();
        boolean flag1 = block instanceof CubeCapital || (block instanceof SlabPlinth && state.getValue(TYPE_UPDOWN) == stateOriginal.getValue(TYPE_UPDOWN));
        return flag1;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return directionToNeighbour.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? stateIn.setValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour), this.func_220111_a(neighbourState, stateIn)) : super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.east();
        BlockPos blockpos3 = blockpos.south();
        BlockPos blockpos4 = blockpos.west();
        BlockState blockstate = iblockreader.getBlockState(blockpos1);
        BlockState blockstate1 = iblockreader.getBlockState(blockpos2);
        BlockState blockstate2 = iblockreader.getBlockState(blockpos3);
        BlockState blockstate3 = iblockreader.getBlockState(blockpos4);

        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state2 = this.defaultBlockState().setValue(TYPE_UPDOWN, Half.BOTTOM).setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
        Direction facing = context.getClickedFace();
        BlockState halfState = facing != Direction.DOWN && (facing == Direction.UP || !(context.getClickLocation().y - (double) blockpos.getY() > 0.5D)) ? state2 : state2.setValue(TYPE_UPDOWN, Half.TOP);
        BlockState finalState = halfState.setValue(NORTH, this.func_220111_a(blockstate, halfState)).setValue(EAST, this.func_220111_a(blockstate1, halfState)).setValue(SOUTH, this.func_220111_a(blockstate2, halfState)).setValue(WEST, this.func_220111_a(blockstate3, halfState));
        return finalState;
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> container) {
        container.add(TYPE_UPDOWN).add(NORTH).add(WEST).add(SOUTH).add(EAST);
    }
}
