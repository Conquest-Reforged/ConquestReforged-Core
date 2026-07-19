package com.conquestrefabricated.content.blocks.block.pipes;

import com.conquestrefabricated.content.blocks.BlockVoxelShapes;
import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MountedPipe extends HorizontalDirectional.Waterlogged.Toggle4 {

    public MountedPipe(Props props) {
        super(props);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        if (state.getValue(TOGGLE) == 1) {
            return BlockVoxelShapes.pipeShape;
        } else {
            switch(state.getValue(DIRECTION)) {
                default:
                case NORTH:
                case SOUTH:
                    return BlockVoxelShapes.pipeShapeHorizontal_NS;
                case EAST:
                case WEST:
                    return BlockVoxelShapes.pipeShapeHorizontal_EW;
            }
        }

    }
}
