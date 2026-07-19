package com.conquestrefabricated.content.blocks.block.glass;

import com.conquestrefabricated.content.blocks.block.VerticalCorner;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

@Assets(
        state = @State(name = "%s_vertical_corner", template = "parent_vertical_corner_transparent"),
        item = @Model(name = "item/%s_vertical_corner", parent = "block/%s_vertical_corner_4", template = "item/parent_vertical_corner"),
        render = @Render(RenderLayer.CUTOUT),
        block = {
                @Model(name = "block/%s_vertical_corner_1", template = "block/parent_vertical_corner_transparent_1"),
                @Model(name = "block/%s_vertical_corner_2", template = "block/parent_vertical_corner_transparent_2"),
                @Model(name = "block/%s_vertical_corner_4", template = "block/parent_vertical_corner_transparent_4"),
                @Model(name = "block/%s_vertical_corner_6", template = "block/parent_vertical_corner_transparent_6"),
        }
)
public class VerticalCornerCutout extends VerticalCorner {

    public VerticalCornerCutout(Props properties) {
        super(properties);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (side == Direction.UP || side == Direction.DOWN) {
            if (adjacentBlockState.getBlock() instanceof TransparentBlock || (adjacentBlockState.getBlock() instanceof VerticalCornerCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && state.getValue(DIRECTION) == adjacentBlockState.getValue(DIRECTION))) {
                return true;
            }
        }

        if (state.getValue(DIRECTION) == Direction.EAST) {
            if (side == Direction.NORTH || side == Direction.EAST) {
                return false;
            } else if (adjacentBlockState.getBlock() instanceof TransparentBlock
                    || (((adjacentBlockState.getBlock() instanceof VerticalCornerCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.SOUTH && (adjacentBlockState.getValue(DIRECTION) == Direction.WEST || adjacentBlockState.getValue(DIRECTION) == Direction.SOUTH))))) {
                return true;
            } else if (adjacentBlockState.getBlock() instanceof TransparentBlock
                    || (((adjacentBlockState.getBlock() instanceof VerticalCornerCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.WEST && (adjacentBlockState.getValue(DIRECTION) == Direction.WEST || adjacentBlockState.getValue(DIRECTION) == Direction.SOUTH))))) {
                return true;
            } else if (((adjacentBlockState.getBlock() instanceof VerticalSlabCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.SOUTH && adjacentBlockState.getValue(DIRECTION) == Direction.SOUTH))) {
                return true;
            } else if (((adjacentBlockState.getBlock() instanceof VerticalSlabCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.WEST && adjacentBlockState.getValue(DIRECTION) == Direction.WEST))) {
                return true;
            } else {
                super.skipRendering(state, adjacentBlockState, side);
            }
        }

        if (state.getValue(DIRECTION) == Direction.WEST) {
            if (side == Direction.SOUTH || side == Direction.WEST) {
                return false;
            } else if (adjacentBlockState.getBlock() instanceof TransparentBlock
                    || (((adjacentBlockState.getBlock() instanceof VerticalCornerCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.NORTH && (adjacentBlockState.getValue(DIRECTION) == Direction.EAST || adjacentBlockState.getValue(DIRECTION) == Direction.NORTH))))) {
                return true;
            } else if (adjacentBlockState.getBlock() instanceof TransparentBlock
                    || (((adjacentBlockState.getBlock() instanceof VerticalCornerCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.EAST && (adjacentBlockState.getValue(DIRECTION) == Direction.EAST || adjacentBlockState.getValue(DIRECTION) == Direction.SOUTH))))) {
                return true;
            } else if (((adjacentBlockState.getBlock() instanceof VerticalSlabCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.NORTH && adjacentBlockState.getValue(DIRECTION) == Direction.NORTH))) {
                return true;
            } else if (((adjacentBlockState.getBlock() instanceof VerticalSlabCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.EAST && adjacentBlockState.getValue(DIRECTION) == Direction.EAST))) {
                return true;
            } else {
                super.skipRendering(state, adjacentBlockState, side);
            }
        }

        if (state.getValue(DIRECTION) == Direction.SOUTH) {
            if (side == Direction.SOUTH || side == Direction.EAST) {
                return false;
            } else if (adjacentBlockState.getBlock() instanceof TransparentBlock
                    || (((adjacentBlockState.getBlock() instanceof VerticalCornerCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.NORTH && (adjacentBlockState.getValue(DIRECTION) == Direction.EAST || adjacentBlockState.getValue(DIRECTION) == Direction.NORTH))))) {
                return true;
            } else if (adjacentBlockState.getBlock() instanceof TransparentBlock
                    || (((adjacentBlockState.getBlock() instanceof VerticalCornerCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.WEST && (adjacentBlockState.getValue(DIRECTION) == Direction.WEST || adjacentBlockState.getValue(DIRECTION) == Direction.NORTH))))) {
                return true;
            } else if (((adjacentBlockState.getBlock() instanceof VerticalSlabCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.NORTH && adjacentBlockState.getValue(DIRECTION) == Direction.NORTH))) {
                return true;
            } else if (((adjacentBlockState.getBlock() instanceof VerticalSlabCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.WEST && adjacentBlockState.getValue(DIRECTION) == Direction.WEST))) {
                return true;
            } else {
                super.skipRendering(state, adjacentBlockState, side);
            }
        }

        if (state.getValue(DIRECTION) == Direction.NORTH) {
            if (side == Direction.NORTH || side == Direction.WEST) {
                return false;
            } else if (adjacentBlockState.getBlock() instanceof TransparentBlock
                    || ((adjacentBlockState.getBlock() instanceof VerticalCornerCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.SOUTH && (adjacentBlockState.getValue(DIRECTION) == Direction.WEST || adjacentBlockState.getValue(DIRECTION) == Direction.SOUTH)))) {
                return true;
            } else if (adjacentBlockState.getBlock() instanceof TransparentBlock
                    || ((adjacentBlockState.getBlock() instanceof VerticalCornerCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.EAST && (adjacentBlockState.getValue(DIRECTION) == Direction.EAST || adjacentBlockState.getValue(DIRECTION) == Direction.SOUTH)))) {
                return true;
            } else if ((adjacentBlockState.getBlock() instanceof VerticalSlabCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.SOUTH && adjacentBlockState.getValue(DIRECTION) == Direction.SOUTH)) {
                return true;
            } else if ((adjacentBlockState.getBlock() instanceof VerticalSlabCutout && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && side == Direction.EAST && adjacentBlockState.getValue(DIRECTION) == Direction.EAST)) {
                return true;
            } else {
                super.skipRendering(state, adjacentBlockState, side);
            }
        }
        return super.skipRendering(state, adjacentBlockState, side);
    }
}