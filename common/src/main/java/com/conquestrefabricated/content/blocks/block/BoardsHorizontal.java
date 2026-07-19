package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.content.blocks.block.directional.Half;
import com.conquestrefabricated.core.asset.annotation.*;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.conquestrefabricated.api.tags.ModTags.CYCLING_TOOLS;
import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@Assets(
        state = @State(name = "%s_boards", template = "parent_boards_horizontal"),
        item = @Model(name = "item/%s_boards", parent = "block/%s_boards_vertical_long_thin", template = "item/acacia_slab"),
        render = @Render(RenderLayer.CUTOUT)
)

@ItemDescription(description = "board_toggle")
public class BoardsHorizontal extends Half.DirectionalWaterlogged {

    private static final VoxelShape TOP_NORTHSOUTH_SHORT_THIN_SHAPE = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape TOP_NORTHSOUTH_SHORT_MID_SHAPE = Block.box(-8.0D, 8.0D, 0.0D, 24.0D, 16.0D, 16.0D);
    private static final VoxelShape TOP_NORTHSOUTH_SHORT_WIDE_SHAPE = Block.box(-16.0D, 8.0D, 0.0D, 32.0D, 16.0D, 16.0D);
    private static final VoxelShape TOP_NORTHSOUTH_LONG_THIN_SHAPE = Block.box(0.0D, 8.0D, -8.0D, 16.0D, 16.0D, 24.0D);
    private static final VoxelShape TOP_NORTHSOUTH_LONG_MID_SHAPE = Block.box(-8.0D, 8.0D, -8.0D, 24.0D, 16.0D, 24.0D);
    private static final VoxelShape TOP_NORTHSOUTH_LONG_WIDE_SHAPE = Block.box(-16.0D, 8.0D, -8.0D, 32.0D, 16.0D, 24.0D);

    private static final VoxelShape TOP_EASTWEST_SHORT_THIN_SHAPE = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape TOP_EASTWEST_SHORT_MID_SHAPE = Block.box(0.0D, 8.0D, -8.0D, 16.0D, 16.0D, 24.0D);
    private static final VoxelShape TOP_EASTWEST_SHORT_WIDE_SHAPE = Block.box(0.0D, 8.0D, -16.0D, 16.0D, 16.0D, 32.0D);
    private static final VoxelShape TOP_EASTWEST_LONG_THIN_SHAPE = Block.box(-8.0D, 8.0D, 0.0D, 24.0D, 16.0D, 16.0D);
    private static final VoxelShape TOP_EASTWEST_LONG_MID_SHAPE = Block.box(-8.0D, 8.0D, -8.0D, 24.0D, 16.0D, 24.0D);
    private static final VoxelShape TOP_EASTWEST_LONG_WIDE_SHAPE = Block.box(-8.0D, 8.0D, -16.0D, 24.0D, 16.0D, 32.0D);

    private static final VoxelShape BOTTOM_NORTHSOUTH_SHORT_THIN_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private static final VoxelShape BOTTOM_NORTHSOUTH_SHORT_MID_SHAPE = Block.box(-8.0D, 0.0D, 0.0D, 24.0D, 8.0D, 16.0D);
    private static final VoxelShape BOTTOM_NORTHSOUTH_SHORT_WIDE_SHAPE = Block.box(-16.0D, 0.0D, 0.0D, 32.0D, 8.0D, 16.0D);
    private static final VoxelShape BOTTOM_NORTHSOUTH_LONG_THIN_SHAPE = Block.box(0.0D, 0.0D, -8.0D, 16.0D, 8.0D, 24.0D);
    private static final VoxelShape BOTTOM_NORTHSOUTH_LONG_MID_SHAPE = Block.box(-8.0D, 0.0D, -8.0D, 24.0D, 8.0D, 24.0D);
    private static final VoxelShape BOTTOM_NORTHSOUTH_LONG_WIDE_SHAPE = Block.box(-16.0D, 0.0D, -8.0D, 32.0D, 8.0D, 24.0D);

    private static final VoxelShape BOTTOM_EASTWEST_SHORT_THIN_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private static final VoxelShape BOTTOM_EASTWEST_SHORT_MID_SHAPE = Block.box(0.0D, 0.0D, -8.0D, 16.0D, 8.0D, 24.0D);
    private static final VoxelShape BOTTOM_EASTWEST_SHORT_WIDE_SHAPE = Block.box(0.0D, 0.0D, -16.0D, 16.0D, 8.0D, 32.0D);
    private static final VoxelShape BOTTOM_EASTWEST_LONG_THIN_SHAPE = Block.box(-8.0D, 0.0D, 0.0D, 24.0D, 8.0D, 16.0D);
    private static final VoxelShape BOTTOM_EASTWEST_LONG_MID_SHAPE = Block.box(-8.0D, 0.0D, -8.0D, 24.0D, 8.0D, 24.0D);
    private static final VoxelShape BOTTOM_EASTWEST_LONG_WIDE_SHAPE = Block.box(-8.0D, 0.0D, -16.0D, 24.0D, 8.0D, 32.0D);

    public static final IntegerProperty LENGTH = IntegerProperty.create("length", 0, 1);
    public static final IntegerProperty WIDTH = IntegerProperty.create("width", 1, 3);
    private static final VoxelShape BOTTOM_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private static final VoxelShape TOP_SHAPE = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    private static final VoxelShape[][][][] SHAPES = {
            {
                    {
                            { TOP_NORTHSOUTH_SHORT_THIN_SHAPE, TOP_NORTHSOUTH_SHORT_MID_SHAPE, TOP_NORTHSOUTH_SHORT_WIDE_SHAPE },
                            { TOP_NORTHSOUTH_LONG_THIN_SHAPE,  TOP_NORTHSOUTH_LONG_MID_SHAPE,  TOP_NORTHSOUTH_LONG_WIDE_SHAPE  }
                    },
                    {
                            { TOP_EASTWEST_SHORT_THIN_SHAPE, TOP_EASTWEST_SHORT_MID_SHAPE, TOP_EASTWEST_SHORT_WIDE_SHAPE },
                            { TOP_EASTWEST_LONG_THIN_SHAPE,  TOP_EASTWEST_LONG_MID_SHAPE,  TOP_EASTWEST_LONG_WIDE_SHAPE  }
                    }
            },
            {
                    {
                            { BOTTOM_NORTHSOUTH_SHORT_THIN_SHAPE, BOTTOM_NORTHSOUTH_SHORT_MID_SHAPE, BOTTOM_NORTHSOUTH_SHORT_WIDE_SHAPE },
                            { BOTTOM_NORTHSOUTH_LONG_THIN_SHAPE,  BOTTOM_NORTHSOUTH_LONG_MID_SHAPE,  BOTTOM_NORTHSOUTH_LONG_WIDE_SHAPE  }
                    },
                    {
                            { BOTTOM_EASTWEST_SHORT_THIN_SHAPE, BOTTOM_EASTWEST_SHORT_MID_SHAPE, BOTTOM_EASTWEST_SHORT_WIDE_SHAPE },
                            { BOTTOM_EASTWEST_LONG_THIN_SHAPE,  BOTTOM_EASTWEST_LONG_MID_SHAPE,  BOTTOM_EASTWEST_LONG_WIDE_SHAPE  }
                    }
            }
    };

    public BoardsHorizontal(Props properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WIDTH, 1).setValue(LENGTH,1).setValue(WATERLOGGED, false));
    }


    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        super.addProperties(builder);
        builder.add(LENGTH, WIDTH);
    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(LENGTH, 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        if (player.getAbilities().instabuild) {
            if (player.isShiftKeyDown()) {
                level.setBlock(blockPos, state.cycle(WIDTH), 3);
                return InteractionResult.SUCCESS;
            }
            level.setBlock(blockPos, state.cycle(LENGTH), 3);
            return InteractionResult.SUCCESS;
        }

        if (player.getMainHandItem().is(CYCLING_TOOLS)) {
            if (player.isShiftKeyDown()) {
                level.setBlock(blockPos, state.cycle(WIDTH), 3);
                return InteractionResult.SUCCESS;
            }
            level.setBlock(blockPos, state.cycle(LENGTH), 3);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        if (state.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
            return BOTTOM_SHAPE;
        } else {
            return TOP_SHAPE;
        }
    }

    public VoxelShape getShape(BlockState state) {
        int half   = (state.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) ? 1 : 0;
        int axis   = (state.getValue(DIRECTION).getAxis() == Direction.Axis.Z) ? 1 : 0;
        int length = state.getValue(LENGTH);
        int width  = state.getValue(WIDTH) - 1;
        return SHAPES[half][axis][length][width];
    }

    @Override
    @NotNull
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state);
    }

    @Override
    @NotNull
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM ? BOTTOM_SHAPE : TOP_SHAPE;
    }

    public static PropertyDispatch<VariantMutator> createEastDefaultSymmetricalRotationStates() {
        return PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                .select(Direction.EAST, BlockModelGenerators.NOP)
                .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_90)
                .select(Direction.WEST, BlockModelGenerators.NOP)
                .select(Direction.NORTH, BlockModelGenerators.Y_ROT_90);
    }
}