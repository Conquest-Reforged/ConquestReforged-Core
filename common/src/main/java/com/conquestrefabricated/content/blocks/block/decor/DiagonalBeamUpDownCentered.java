package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.block.directional.Half;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DiagonalBeamUpDownCentered extends Half.DirectionalWaterlogged {

    public DiagonalBeamUpDownCentered(Props properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        if (state.getValue(DIRECTION) == Direction.NORTH || state.getValue(DIRECTION) == Direction.SOUTH) {
            return DiagonalBeamCentered.NORTH_SOUTH_SHAPE;
        } else {
            return DiagonalBeamCentered.EAST_WEST_SHAPE;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = DiagonalBeamCentered.getDirection(context.getHorizontalDirection().getOpposite(), context.getClickedPos(), context);
        return super.getStateForPlacement(context).setValue(DIRECTION, facing);
    }
}
