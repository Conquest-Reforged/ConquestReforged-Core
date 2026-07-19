package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.content.blocks.BlockVoxelShapes;
import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.VerticalQuarter;
import com.conquestrefabricated.content.blocks.block.plants.Bush;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.base.WaterloggedHorizontalDirectionalShape;
import com.conquestrefabricated.core.block.base.WaterloggedShape;
import com.conquestrefabricated.core.block.builder.Props;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

@Render(RenderLayer.CUTOUT)
public class YoungTreeDirectional extends WaterloggedHorizontalDirectionalShape {

    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public YoungTreeDirectional(Props props) {
        super(props.toSettings());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(WATERLOGGED, false));

    }

    @Override
    public VoxelShape getShape(BlockState state) {
        switch (state.getValue(DIRECTION)) {
            case NORTH:
            default:
                return VerticalQuarter.NORTH_SHAPE[1];
            case SOUTH:
                return VerticalQuarter.SOUTH_SHAPE[1];
            case WEST:
                return VerticalQuarter.WEST_SHAPE[1];
            case EAST:
                return VerticalQuarter.EAST_SHAPE[1];
        }
    }


    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockGetter reader = context.getLevel();

        BlockState up = reader.getBlockState(pos.above());
        BlockState down = reader.getBlockState(pos.below());

        return super.getStateForPlacement(context)
                .setValue(UP, attachesTo(up))
                .setValue(DOWN, attachesToDown(down));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (stateIn.getValue(WATERLOGGED)) {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        boolean flag = this.canConnectTo(level, currentPos.above());
        boolean flag1 = this.canConnectToDown(level, currentPos.below());
        return stateIn.setValue(UP, flag).setValue(DOWN, flag1);
    }

    private boolean attachesTo(BlockState blockstate) {
        Block block = blockstate.getBlock();
        return !Block.isExceptionForConnection(blockstate) && (!(block != this && !(block instanceof YoungTreeDirectional)));
    }

    private boolean attachesToDown(BlockState blockstate) {
        Block block = blockstate.getBlock();
        return block instanceof Layer;
    }

    private boolean canConnectTo(LevelReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        return !Block.isExceptionForConnection(blockState) && (!(block != this && !(block instanceof YoungTreeDirectional)));
    }

    private boolean canConnectToDown(LevelReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block instanceof Layer;
    }
}
