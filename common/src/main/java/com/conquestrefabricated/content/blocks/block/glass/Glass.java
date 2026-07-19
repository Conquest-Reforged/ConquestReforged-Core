package com.conquestrefabricated.content.blocks.block.glass;

import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.util.RenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

import static com.conquestrefabricated.core.block.base.WaterloggedHorizontalDirectionalShape.DIRECTION;
import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@Assets(
        state = @State(name = "%s", template = "parent_cube", plural = true),
        item = @Model(name = "item/%s", parent = "block/%s", template = "item/parent_cube", plural = true),
        render = @Render(RenderLayer.CUTOUT),
        block = @Model(name = "block/%s", template = "block/parent_cube", plural = true)
)
public class Glass extends TransparentBlock {

    public Glass(Properties properties) {
        super(properties);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (adjacentBlockState.getBlock() instanceof TransparentBlock) {
            return true;
        } else if (side == Direction.DOWN && adjacentBlockState.getBlock() instanceof SlabCutout && adjacentBlockState.getValue(TYPE_UPDOWN) == Half.TOP) {
            return true;
        } else if (side == Direction.UP && adjacentBlockState.getBlock() instanceof SlabCutout && adjacentBlockState.getValue(TYPE_UPDOWN) == Half.BOTTOM) {
            return true;
        } else if ((adjacentBlockState.getBlock() instanceof StairsCutout) && (adjacentBlockState.getValue(DIRECTION) == side.getOpposite()) && adjacentBlockState.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT) {
            return true;
        } else if ((adjacentBlockState.getBlock() instanceof VerticalSlabCutout) && (adjacentBlockState.getValue(DIRECTION) == side)) {
            return true;
        } else if ((adjacentBlockState.getBlock() instanceof VerticalCornerCutout) && (adjacentBlockState.getValue(DIRECTION) == side || adjacentBlockState.getValue(DIRECTION).getCounterClockWise() == side)) {
            return true;
        }
        return super.skipRendering(state, adjacentBlockState, side);
    }
}
