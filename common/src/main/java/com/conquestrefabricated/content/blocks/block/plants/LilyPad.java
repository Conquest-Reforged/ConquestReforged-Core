package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LilyPadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Render(RenderLayer.CUTOUT)
public class LilyPad extends LilyPadBlock {

    public LilyPad(BlockBehaviour.Properties properties) {
        super(properties);
    }

    /**
     * Disable boats breaking the lilypad/duckweed block
     */
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {}

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        // Check if the context represents a player in creative mode
        return Shapes.empty();
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockView, BlockPos blockPos) {
        return true;
    }
}
