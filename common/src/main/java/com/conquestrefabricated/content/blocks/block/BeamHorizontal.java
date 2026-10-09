package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.HorizontalDirectionalShape;
import com.conquestrefabricated.core.block.properties.Third;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPMIDDLEDOWN;

@Assets(
        state = @State(name = "%s_horizontal", template = "parent_beam_horizontal"),
        item = @Model(name = "item/%s_horizontal", parent = "block/%s_beam_horizontal_ns", template = "item/parent_beam_horizontal"),
        block = {
                @Model(name = "block/%s_beam_horizontal_ne", template = "block/parent_beam_horizontal_ne"),
                @Model(name = "block/%s_beam_horizontal_ns", template = "block/parent_beam_horizontal_ns"),
                @Model(name = "block/%s_beam_horizontal_nse", template = "block/parent_beam_horizontal_nse"),
                @Model(name = "block/%s_beam_horizontal_nsew", template = "block/parent_beam_horizontal_nsew"),
                @Model(name = "block/%s_beam_horizontal_ne_bottom", template = "block/parent_beam_horizontal_ne_bottom"),
                @Model(name = "block/%s_beam_horizontal_ns_bottom", template = "block/parent_beam_horizontal_ns_bottom"),
                @Model(name = "block/%s_beam_horizontal_nse_bottom", template = "block/parent_beam_horizontal_nse_bottom"),
                @Model(name = "block/%s_beam_horizontal_nsew_bottom", template = "block/parent_beam_horizontal_nsew_bottom")
        }
)
public class BeamHorizontal extends HorizontalDirectionalShape {

    public static final IntegerProperty ACTIVATED = IntegerProperty.create("activated", 1, 4);

    private static final VoxelShape HORIZONTAL_BEAM_NS_TOP = Block.box(5, 12, 0, 11, 16, 16);
    private static final VoxelShape HORIZONTAL_BEAM_EW_TOP = Block.box(0, 12, 5, 16, 16, 11);

    private static final VoxelShape HORIZONTAL_BEAM_NE_TOP = Shapes.join(Block.box(5, 12, 0, 11, 16, 11), Block.box(11, 12, 5, 16, 16, 11), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_ES_TOP = Shapes.join(Block.box(5, 12, 5, 16, 16, 11), Block.box(5, 12, 11, 11, 16, 16), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_SW_TOP = Shapes.join(Block.box(5, 12, 5, 11, 16, 16), Block.box(0, 12, 5, 5, 16, 11), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_WN_TOP = Shapes.join(Block.box(0, 12, 5, 11, 16, 11), Block.box(5, 12, 0, 11, 16, 5), BooleanOp.OR);

    private static final VoxelShape HORIZONTAL_BEAM_NSE_TOP = Shapes.join(Block.box(11, 12, 5, 16, 16, 11), Block.box(5, 12, 0, 11, 16, 16), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_ESW_TOP = Shapes.join(Block.box(5, 12, 11, 11, 16, 16), Block.box(0, 12, 5, 16, 16, 11), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_SWN_TOP = Shapes.join(Block.box(0, 12, 5, 5, 16, 11), Block.box(5, 12, 0, 11, 16, 16), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_WNS_TOP = Shapes.join(Block.box(5, 12, 0, 11, 16, 5), Block.box(0, 12, 5, 16, 16, 11), BooleanOp.OR);

    private static final VoxelShape HORIZONTAL_BEAM_NSEW_TOP = Shapes.join(HORIZONTAL_BEAM_NS_TOP, HORIZONTAL_BEAM_EW_TOP, BooleanOp.OR);

    private static final VoxelShape HORIZONTAL_BEAM_NS_MIDDLE = Block.box(5, 6, 0, 11, 10, 16);
    private static final VoxelShape HORIZONTAL_BEAM_EW_MIDDLE = Block.box(0, 6, 5, 16, 10, 11);

    private static final VoxelShape HORIZONTAL_BEAM_NE_MIDDLE = Shapes.join(Block.box(5, 6, 0, 11, 16, 11), Block.box(11, 6, 5, 16, 10, 11), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_ES_MIDDLE = Shapes.join(Block.box(5, 6, 5, 16, 16, 11), Block.box(5, 6, 11, 11, 10, 16), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_SW_MIDDLE = Shapes.join(Block.box(5, 6, 5, 11, 16, 16), Block.box(0, 6, 5, 5, 10, 11), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_WN_MIDDLE = Shapes.join(Block.box(0, 6, 5, 11, 16, 11), Block.box(5, 6, 0, 11, 10, 5), BooleanOp.OR);

    private static final VoxelShape HORIZONTAL_BEAM_NSE_MIDDLE = Shapes.join(Block.box(11, 6, 5, 16, 10, 11), Block.box(5, 6, 0, 11, 10, 16), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_ESW_MIDDLE = Shapes.join(Block.box(5, 6, 11, 11, 10, 16), Block.box(0, 6, 5, 16, 10, 11), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_SWN_MIDDLE = Shapes.join(Block.box(0, 6, 5, 5, 10, 11), Block.box(5, 6, 0, 11, 10, 16), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_WNS_MIDDLE = Shapes.join(Block.box(5, 6, 0, 11, 10, 5), Block.box(0, 6, 5, 16, 10, 11), BooleanOp.OR);

    private static final VoxelShape HORIZONTAL_BEAM_NSEW_MIDDLE = Shapes.join(HORIZONTAL_BEAM_NS_MIDDLE, HORIZONTAL_BEAM_EW_MIDDLE, BooleanOp.OR);

    private static final VoxelShape HORIZONTAL_BEAM_NS_BOTTOM = Block.box(5, 0, 0, 11, 4, 16);
    private static final VoxelShape HORIZONTAL_BEAM_EW_BOTTOM = Block.box(0, 0, 5, 16, 4, 11);

    private static final VoxelShape HORIZONTAL_BEAM_NE_BOTTOM = Shapes.join(Block.box(5, 0, 0, 11, 4, 11), Block.box(11, 0, 5, 16, 4, 11), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_ES_BOTTOM = Shapes.join(Block.box(5, 0, 5, 16, 4, 11), Block.box(5, 0, 11, 11, 4, 16), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_SW_BOTTOM = Shapes.join(Block.box(5, 0, 5, 11, 4, 16), Block.box(0, 0, 5, 5, 4, 11), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_WN_BOTTOM = Shapes.join(Block.box(0, 0, 5, 11, 4, 11), Block.box(5, 0, 0, 11, 4, 5), BooleanOp.OR);

    private static final VoxelShape HORIZONTAL_BEAM_NSE_BOTTOM = Shapes.join(Block.box(11, 0, 5, 16, 4, 11), Block.box(5, 0, 0, 11, 4, 16), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_ESW_BOTTOM = Shapes.join(Block.box(5, 0, 11, 11, 4, 16), Block.box(0, 0, 5, 16, 4, 11), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_SWN_BOTTOM = Shapes.join(Block.box(0, 0, 5, 5, 4, 11), Block.box(5, 0, 0, 11, 4, 16), BooleanOp.OR);
    private static final VoxelShape HORIZONTAL_BEAM_WNS_BOTTOM = Shapes.join(Block.box(5, 0, 0, 11, 4, 5), Block.box(0, 0, 5, 16, 4, 11), BooleanOp.OR);

    private static final VoxelShape HORIZONTAL_BEAM_NSEW_BOTTOM = Shapes.join(HORIZONTAL_BEAM_NS_BOTTOM, HORIZONTAL_BEAM_EW_BOTTOM, BooleanOp.OR);

    private static final VoxelShape[][][] SHAPES = {
            {
                    { HORIZONTAL_BEAM_NS_TOP, HORIZONTAL_BEAM_EW_TOP, HORIZONTAL_BEAM_NS_TOP, HORIZONTAL_BEAM_EW_TOP },
                    { HORIZONTAL_BEAM_NE_TOP, HORIZONTAL_BEAM_ES_TOP, HORIZONTAL_BEAM_SW_TOP, HORIZONTAL_BEAM_WN_TOP },
                    { HORIZONTAL_BEAM_NSE_TOP, HORIZONTAL_BEAM_ESW_TOP, HORIZONTAL_BEAM_SWN_TOP, HORIZONTAL_BEAM_WNS_TOP },
                    { HORIZONTAL_BEAM_NSEW_TOP, HORIZONTAL_BEAM_NSEW_TOP, HORIZONTAL_BEAM_NSEW_TOP, HORIZONTAL_BEAM_NSEW_TOP }
            },
            {
                    { HORIZONTAL_BEAM_NS_MIDDLE, HORIZONTAL_BEAM_EW_MIDDLE, HORIZONTAL_BEAM_NS_MIDDLE, HORIZONTAL_BEAM_EW_MIDDLE },
                    { HORIZONTAL_BEAM_NE_MIDDLE, HORIZONTAL_BEAM_ES_MIDDLE, HORIZONTAL_BEAM_SW_MIDDLE, HORIZONTAL_BEAM_WN_MIDDLE },
                    { HORIZONTAL_BEAM_NSE_MIDDLE, HORIZONTAL_BEAM_ESW_MIDDLE, HORIZONTAL_BEAM_SWN_MIDDLE, HORIZONTAL_BEAM_WNS_MIDDLE },
                    { HORIZONTAL_BEAM_NSEW_MIDDLE, HORIZONTAL_BEAM_NSEW_MIDDLE, HORIZONTAL_BEAM_NSEW_MIDDLE, HORIZONTAL_BEAM_NSEW_MIDDLE }
            },
            {
                    { HORIZONTAL_BEAM_NS_BOTTOM, HORIZONTAL_BEAM_EW_BOTTOM, HORIZONTAL_BEAM_NS_BOTTOM, HORIZONTAL_BEAM_EW_BOTTOM },
                    { HORIZONTAL_BEAM_NE_BOTTOM, HORIZONTAL_BEAM_ES_BOTTOM, HORIZONTAL_BEAM_SW_BOTTOM, HORIZONTAL_BEAM_WN_BOTTOM },
                    { HORIZONTAL_BEAM_NSE_BOTTOM, HORIZONTAL_BEAM_ESW_BOTTOM, HORIZONTAL_BEAM_SWN_BOTTOM, HORIZONTAL_BEAM_WNS_BOTTOM },
                    { HORIZONTAL_BEAM_NSEW_BOTTOM, HORIZONTAL_BEAM_NSEW_BOTTOM, HORIZONTAL_BEAM_NSEW_BOTTOM, HORIZONTAL_BEAM_NSEW_BOTTOM }
            }
    };

    public BeamHorizontal(Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state) {
        int position = switch (state.getValue(TYPE_UPMIDDLEDOWN)) {
            case TOP -> 0;
            case MIDDLE -> 1;
            case BOTTOM -> 2;
        };
        int facing = switch (state.getValue(DIRECTION)) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> throw new IllegalStateException("Non-horizontal direction");
        };
        int shape = state.getValue(ACTIVATED) - 1;
        return SHAPES[position][shape][facing];
    }

//    @Override
//    public VoxelShape getShape(BlockState state) {
//        if (state.getValue(TYPE_UPMIDDLEDOWN) == Third.BOTTOM) {
//            return HORIZONTAL_BEAM_NSEW_BOTTOM;
//        } else if (state.getValue(TYPE_UPMIDDLEDOWN) == Third.MIDDLE) {
//            return HORIZONTAL_BEAM_NSEW_MIDDLE;
//        } else {
//            return HORIZONTAL_BEAM_NSEW_TOP;
//        }
//    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED).add(TYPE_UPMIDDLEDOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();

        if ((context.getClickLocation().y - (double)pos.getY() < 0.33D) || (facing == Direction.DOWN)) {
            return super.getStateForPlacement(context)
                    .setValue(DIRECTION, facing)
                    .setValue(ACTIVATED, 1)
                    .setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM);
        } else if ((context.getClickLocation().y - (double)pos.getY() > 0.66D) || (facing == Direction.UP)) {
            return super.getStateForPlacement(context)
                    .setValue(DIRECTION, facing)
                    .setValue(ACTIVATED, 1)
                    .setValue(TYPE_UPMIDDLEDOWN, Third.TOP);
        } else {
            return super.getStateForPlacement(context)
                    .setValue(DIRECTION, facing)
                    .setValue(ACTIVATED, 1)
                    .setValue(TYPE_UPMIDDLEDOWN, Third.MIDDLE);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        return Interactions.onUseToggleItem(player, level, blockPos, state, ACTIVATED);
    }
}
