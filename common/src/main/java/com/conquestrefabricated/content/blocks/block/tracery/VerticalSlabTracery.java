package com.conquestrefabricated.content.blocks.block.tracery;

import com.conquestrefabricated.content.blocks.block.VerticalSlab;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@Assets(
        state = @State(name = "%s_vertical_slab", template = "parent_vertical_slab"),
        item = @Model(name = "item/%s_vertical_slab", parent = "block/%s_vertical_slab_4", template = "item/parent_vertical_slab"),
        render = @Render(RenderLayer.CUTOUT),
        block = {
                @Model(name = "block/%s_vertical_slab_1", template = "block/parent_vertical_slab_1"),
                @Model(name = "block/%s_vertical_slab_2", template = "block/parent_vertical_slab_2"),
                @Model(name = "block/%s_vertical_slab_4", template = "block/parent_vertical_slab_4"),
                @Model(name = "block/%s_vertical_slab_6", template = "block/parent_vertical_slab_6"),
        }
)
public class VerticalSlabTracery extends VerticalSlab {

    public VerticalSlabTracery(Props properties) {
        super(properties);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        {
            if (side == Direction.UP || side == Direction.DOWN) {
                if (adjacentBlockState.getBlock() instanceof GlassTracery) {
                    return true;
                } else if (adjacentBlockState.getBlock() instanceof VerticalSlabTracery && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && state.getValue(DIRECTION) == adjacentBlockState.getValue(DIRECTION)) {
                    return true;
                } else if (adjacentBlockState.getBlock() instanceof VerticalCornerTracery && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && (state.getValue(DIRECTION) == adjacentBlockState.getValue(DIRECTION) || state.getValue(DIRECTION) == adjacentBlockState.getValue(DIRECTION).getCounterClockWise())) {
                    return true;
                } else {
                    return false;
                }
            }
            if (side == state.getValue(DIRECTION)) {
                return false;
            } else if (adjacentBlockState.getBlock() instanceof GlassTracery) {
                return true;
            } else if ((adjacentBlockState.getBlock() instanceof VerticalSlabTracery) && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && ((state.getValue(DIRECTION) == adjacentBlockState.getValue(DIRECTION).getOpposite() && state.getValue(DIRECTION).getOpposite() == side))) {
                return true;
            } else if (adjacentBlockState.getBlock() instanceof VerticalSlabTracery && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && ((state.getValue(DIRECTION) == adjacentBlockState.getValue(DIRECTION) && (state.getValue(DIRECTION).getClockWise() == side || state.getValue(DIRECTION).getCounterClockWise() == side))
                    || ((state.getValue(DIRECTION).getClockWise() == side || state.getValue(DIRECTION).getCounterClockWise() == side) && adjacentBlockState.getValue(DIRECTION) == side)
                    || (state.getValue(DIRECTION) == adjacentBlockState.getValue(DIRECTION).getOpposite() && state.getValue(DIRECTION).getOpposite() == side))) {
                return true;
            } else if ((adjacentBlockState.getBlock() instanceof VerticalCornerTracery && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS)) && (adjacentBlockState.getValue(DIRECTION) == state.getValue(DIRECTION).getOpposite() || adjacentBlockState.getValue(DIRECTION) == state.getValue(DIRECTION).getCounterClockWise()) && side == state.getValue(DIRECTION).getOpposite()) {
                return true;
            } else if (adjacentBlockState.getBlock() instanceof VerticalCornerTracery && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && (adjacentBlockState.getValue(DIRECTION) != state.getValue(DIRECTION).getCounterClockWise() && side == state.getValue(DIRECTION).getClockWise()
                    || (adjacentBlockState.getValue(DIRECTION) == state.getValue(DIRECTION).getOpposite() || adjacentBlockState.getValue(DIRECTION) == state.getValue(DIRECTION).getCounterClockWise()) && side == state.getValue(DIRECTION).getOpposite()
                    || adjacentBlockState.getValue(DIRECTION) != state.getValue(DIRECTION).getOpposite() && side == state.getValue(DIRECTION).getCounterClockWise())) {
                return true;
            } else if (adjacentBlockState.getBlock() instanceof VerticalQuarterTracery && adjacentBlockState.getValue(LAYERS) >= state.getValue(LAYERS) && ((adjacentBlockState.getValue(DIRECTION).getCounterClockWise() == side && adjacentBlockState.getValue(DIRECTION) == state.getValue(DIRECTION)) || (adjacentBlockState.getValue(DIRECTION) == side && adjacentBlockState.getValue(DIRECTION) == state.getValue(DIRECTION).getClockWise()))) {
                return true;
            }
        }


        return super.skipRendering(state, adjacentBlockState, side);
    }
}
