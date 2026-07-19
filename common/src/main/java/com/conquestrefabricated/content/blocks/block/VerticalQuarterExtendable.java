package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

@Assets(
        state = @State(name = "%s_vertical_quarter", template = "parent_vertical_quarter"),
        item = @Model(name = "item/%s_vertical_quarter", parent = "block/%s_vertical_quarter_4", template = "item/parent_vertical_quarter"),
        block = {
                @Model(name = "block/%s_vertical_quarter_1", template = "block/parent_vertical_quarter_1"),
                @Model(name = "block/%s_vertical_quarter_2", template = "block/parent_vertical_quarter_2"),
                @Model(name = "block/%s_vertical_quarter_4", template = "block/parent_vertical_quarter_4"),
                @Model(name = "block/%s_vertical_quarter_6", template = "block/parent_vertical_quarter_6"),
        }
)
@SpecialOffset(offsetType = SpecialOffsetType.DUPLICATE_DOWN)
public class VerticalQuarterExtendable extends VerticalQuarter {
    public static final BooleanProperty EXTENSION_TOGGLE = ModBlockProperties.EXTENSION_TOGGLE;

    public VerticalQuarterExtendable(Props props) {
        super(props);
        this.registerDefaultState((this.stateDefinition.any()).setValue(DIRECTION, Direction.NORTH).setValue(WATERLOGGED, false).setValue(EXTENSION_TOGGLE, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction sideFacing = context.getClickedFace().getOpposite();
        BlockGetter blockreader = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = blockreader.getBlockState(pos.relative(sideFacing));
        Block block = state.getBlock();
        boolean isSlab = false;
        if (sideFacing == Direction.DOWN) {
            isSlab = block instanceof Slab ||
                    block instanceof SlabBlock ||
                    block instanceof Layer ||
                    block instanceof SnowLayerBlock ||
                    block instanceof SlabLessLayers ||
                    block instanceof LeavesBlock ||
                    block instanceof BushBlock;
        }
        Direction facing = PlacementHelper.getHitVecHorizontalAxisDirection(context.getHorizontalDirection().getOpposite(), pos, context);
        BlockState blockstate = context.getLevel().getBlockState(pos);
        if (blockstate.getBlock() == this) {
            int i = blockstate.getValue(LAYERS);
            if (i == 4) {
                return fullBlock.defaultBlockState();
            }
            return blockstate.setValue(LAYERS, Math.min(4, i + 1));
        } else {
            return super.getStateForPlacement(context).setValue(DIRECTION, facing).setValue(EXTENSION_TOGGLE, isSlab);
        }
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS, EXTENSION_TOGGLE);
    }
}
