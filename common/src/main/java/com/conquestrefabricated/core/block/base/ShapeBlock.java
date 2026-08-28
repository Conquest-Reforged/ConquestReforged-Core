package com.conquestrefabricated.core.block.base;

import com.conquestrefabricated.core.block.builder.Props;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class ShapeBlock extends Shape {

    private final List<VoxelShape> hitBox;

    public ShapeBlock(Props props) {
        super(props.toSettings());
        this.hitBox = props.get("hitBox", List.class);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return getShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (this.hasCollision == false) {
            return Shapes.empty();
        } else {
            return getShape(state);
        }
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        if (this.hasCollision == false) {
            return Shapes.empty();
        } else {
            return getShape(state);
        }
    }

    public VoxelShape getShape(BlockState state) {
        return hitBox.get(0);
    }
}
