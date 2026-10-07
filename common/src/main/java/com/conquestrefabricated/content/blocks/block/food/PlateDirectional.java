package com.conquestrefabricated.content.blocks.block.food;

import com.conquestrefabricated.content.blocks.block.food.Plate;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class PlateDirectional extends Plate {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PlateDirectional(Properties properties) {super(properties);
    }

    @NotNull
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        boolean isSlab = PlacementHelper.isFacingSlab(context);
        return super.getStateForPlacement(context)
                .setValue(FACING, facing)
                .setValue(OFFSET_TOGGLE, isSlab);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OFFSET_TOGGLE, BITES);
    }
}