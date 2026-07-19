package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.WaterloggedHorizontalDirectionalShape;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

@Assets(
        state = @State(name = "%s_vertical_slab", template = "parent_vertical_slab"),
        item = @Model(name = "item/%s_vertical_slab", parent = "block/%s_vertical_slab_4", template = "item/parent_vertical_slab"),
        block = {
                @Model(name = "block/%s_vertical_slab_1", template = "block/templates/parent_vertical_slab_1"),
                @Model(name = "block/%s_vertical_slab_2", template = "block/templates/parent_vertical_slab_2"),
                @Model(name = "block/%s_vertical_slab_4", template = "block/templates/parent_vertical_slab_4"),
                @Model(name = "block/%s_vertical_slab_6", template = "block/templates/parent_vertical_slab_6"),
        }
)
@SpecialOffset(offsetType = SpecialOffsetType.DUPLICATE_DOWN)
public class VerticalSlab extends WaterloggedHorizontalDirectionalShape {

    public static final IntegerProperty LAYERS = ModBlockProperties.LAYERS_4;

    public static final VoxelShape[] EAST_SHAPE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D),Block.box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D)};
    public static final VoxelShape[] WEST_SHAPE = new VoxelShape[]{Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
    public static final VoxelShape[] SOUTH_SHAPE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D)};
    public static final VoxelShape[] NORTH_SHAPE = new VoxelShape[]{Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D), Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D), Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D), Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D)};
    protected Block fullBlock;


    public VerticalSlab(Props props) {
        super(props.toSettings());
        this.registerDefaultState((this.stateDefinition.any()).setValue(DIRECTION, Direction.NORTH).setValue(WATERLOGGED, false));
        this.fullBlock = props.getParent().getBlock();
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        switch (state.getValue(DIRECTION)) {
            case NORTH:
            default:
                return NORTH_SHAPE[state.getValue(LAYERS) - 1];
            case SOUTH:
                return SOUTH_SHAPE[state.getValue(LAYERS) - 1];
            case WEST:
                return WEST_SHAPE[state.getValue(LAYERS) - 1];
            case EAST:
                return EAST_SHAPE[state.getValue(LAYERS) - 1];
        }
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        int i = state.getValue(LAYERS);
        if (context.getItemInHand().getItem() == this.asItem() && i <= 4) {
            if (PlacementHelper.replacingClickedOnBlock(context)) {
                return context.getClickedFace() == state.getValue(DIRECTION);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (blockstate.getBlock() == this) {
            int i = blockstate.getValue(LAYERS);
            if (i == 4) {
                return fullBlock.defaultBlockState();
            }
            return blockstate.setValue(LAYERS, Math.min(4, i + 1));
        } else {
            return super.getStateForPlacement(context);
        }
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }
}
