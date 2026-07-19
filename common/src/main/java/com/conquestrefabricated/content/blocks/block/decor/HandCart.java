package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Render(RenderLayer.CUTOUT)
@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class HandCart extends ParallelConnecting {

    protected static final VoxelShape SHAPE = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape COLLISION_SHAPE_N = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 32.0D);
    protected static final VoxelShape COLLISION_SHAPE_E = Block.box(-16.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape COLLISION_SHAPE_S = Block.box(0.0D, 8.0D, -16.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape COLLISION_SHAPE_W = Block.box(0.0D, 8.0D, 0.0D, 32.0D, 16.0D, 16.0D);

    public HandCart(Props properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(OFFSET_TOGGLE)) {
            return Shapes.empty();
        }
        return switch (state.getValue(DIRECTION)) {
            case EAST -> COLLISION_SHAPE_E;
            case SOUTH -> COLLISION_SHAPE_S;
            case WEST -> COLLISION_SHAPE_W;
            default -> COLLISION_SHAPE_N;
        };
    }

    @Override
    protected boolean attachesTo(BlockState state, Direction facing) {
        Block block = state.getBlock();
        return block instanceof HandCart && state.getValue(DIRECTION) == facing;
    }
}
