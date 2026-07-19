package com.conquestrefabricated.content.blocks.block.classical;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.decor.Lintels;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmallCorniceModel extends Lintels {

    public SmallCorniceModel(Properties properties) {
        super(((BlockSettingsAccessor) properties)
                .setCustomOffsetter(CustomOffsetType.LAYER_XYZ)
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .dynamicShape()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(OFFSET_TOGGLE, false));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(OFFSET_TOGGLE)) {
            return Shapes.empty();
        } else {
            return super.getShape(state);
        }
    }
}