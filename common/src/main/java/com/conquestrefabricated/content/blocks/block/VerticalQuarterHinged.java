package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.WaterloggedHorizontalDirectionalShape;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

@Assets(
        state = @State(name = "%s_vertical_quarter", template = "parent_vertical_quarter"),
        item = @Model(name = "item/%s_vertical_corner_slab", parent = "block/%s_vertical_corner_slab_left", template = "item/parent_slab_corner"),
        block = {
                @Model(name = "block/%s_vertical_corner_slab_left", template = "block/parent_vertical_corner_slab_left"),
                @Model(name = "block/%s_vertical_corner_slab_bottom_left", template = "block/parent_vertical_corner_slab_bottom_left"),
                @Model(name = "block/%s_vertical_corner_slab_right", template = "block/parent_vertical_corner_slab_right"),
                @Model(name = "block/%s_vertical_corner_slab_bottom_right", template = "block/parent_vertical_corner_slab_bottom_right"),
        }
)
public class VerticalQuarterHinged extends WaterloggedHorizontalDirectionalShape {

    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final IntegerProperty LAYERS = IntegerProperty.create("layer", 1, 4);
    protected Block fullBlock;

    public VerticalQuarterHinged(Props props) {
        super(props.toSettings());
        this.registerDefaultState((this.stateDefinition.any()).setValue(DIRECTION, Direction.NORTH).setValue(WATERLOGGED, false));
        this.fullBlock = props.getParent().getBlock();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        FluidState fluid = context.getLevel().getFluidState(blockpos);
        Direction facingHorizontal = context.getHorizontalDirection().getOpposite();
        BlockState state2 = defaultBlockState().setValue(DIRECTION, facingHorizontal).setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);

        BlockState blockstate = context.getLevel().getBlockState(blockpos);
        if (blockstate.getBlock() == this) {
            int i = blockstate.getValue(LAYERS);
            if (i == 4) {
                return fullBlock.defaultBlockState();
            }
            return blockstate.setValue(LAYERS, Math.min(4, i + 1));
        }
        return state2.setValue(HINGE, this.getHingeSide(facingHorizontal, blockpos, context));
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        int i = state.getValue(LAYERS);
        if (context.getItemInHand().getItem() == this.asItem() && i <= 4) {
            if (PlacementHelper.replacingClickedOnBlock(context)) {
                return context.getClickedFace() == state.getValue(DIRECTION) || context.getClickedFace() == state.getValue(DIRECTION).getCounterClockWise();
            } else {
                return true;
            }
        }
        else {
            return false;
        }
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        switch (mirrorIn) {
            case FRONT_BACK:
                switch(state.getValue(DIRECTION)) {
                    case NORTH:
                    case SOUTH:
                        return state.cycle(HINGE);
                    case EAST:
                    case WEST:
                    default:
                        return super.mirror(state, mirrorIn);
                }
            case LEFT_RIGHT:
                switch(state.getValue(DIRECTION)) {
                    case WEST:
                    case EAST:
                        return state.cycle(HINGE);
                    case SOUTH:
                    case NORTH:
                    default:
                        return super.mirror(state, mirrorIn);
                }
        }
        return super.mirror(state, mirrorIn);
    }


    private DoorHingeSide getHingeSide(Direction facing, BlockPos pos, BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getHorizontalDirection();
        int j = direction.getStepX();
        int k = direction.getStepZ();
        Vec3 vec3d = context.getClickLocation();
        double d = vec3d.x - (double)blockPos.getX();
        double e = vec3d.z - (double)blockPos.getZ();
        return (j >= 0 || !(e < 0.5)) && (j <= 0 || !(e > 0.5)) && (k >= 0 || !(d > 0.5)) && (k <= 0 || !(d < 0.5)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> container) {
        container.add(HINGE).add(LAYERS);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
                if (state.getValue(HINGE) == DoorHingeSide.LEFT) {
                    switch (state.getValue(DIRECTION)) {
                        case NORTH:
                        default:
                            return VerticalQuarter.NORTH_SHAPE[state.getValue(LAYERS) - 1];
                        case SOUTH:
                            return VerticalQuarter.SOUTH_SHAPE[state.getValue(LAYERS) - 1];
                        case WEST:
                            return VerticalQuarter.WEST_SHAPE[state.getValue(LAYERS) - 1];
                        case EAST:
                            return VerticalQuarter.EAST_SHAPE[state.getValue(LAYERS) - 1];
                    }
                } else {
                    switch (state.getValue(DIRECTION)) {
                        case NORTH:
                        default:
                            return VerticalQuarter.EAST_SHAPE[state.getValue(LAYERS) - 1];
                        case SOUTH:
                            return VerticalQuarter.WEST_SHAPE[state.getValue(LAYERS) - 1];
                        case WEST:
                            return VerticalQuarter.NORTH_SHAPE[state.getValue(LAYERS) - 1];
                        case EAST:
                            return VerticalQuarter.SOUTH_SHAPE[state.getValue(LAYERS) - 1];
                    }
                }
        }
}