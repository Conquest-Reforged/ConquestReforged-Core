package com.conquestrefabricated.content.blocks.block.directional;

import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.HorizontalDirectionalShape;
import com.conquestrefabricated.core.block.base.Shape;
import com.conquestrefabricated.core.block.base.WaterloggedHorizontalDirectionalShape;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.properties.Third;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.List;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPMIDDLEDOWN;

/**
 * This contains the following static classes:
 * - Directional
 *    - Toggle2
 *    - Toggle3
 *    - Toggle4
 * - DirectionalWaterlogged
 *    - FamilyVariant
 */
public class ThirdBlock extends Shape {

    private static final VoxelShape TOP_SHAPE = Block.box(1.0D, 8.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    private static final VoxelShape MIDDLE_SHAPE = Block.box(1.0D, 8.0D, 1.0D, 15.0D, 12.0D, 15.0D);
    private static final VoxelShape BOTTOM_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D);

    public ThirdBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM));
    }

//    @Override
//    public BlockState getStateForPlacement(BlockPlaceContext context) {
//        BlockState state2 = this.defaultBlockState().setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM);
//        Direction facing = context.getClickedFace();
//        return facing != Direction.DOWN && (facing == Direction.UP || !(context.getClickLocation().y - (double) context.getClickedPos().getY() > 0.5D)) ? state2 : state2.setValue(TYPE_UPMIDDLEDOWN, Third.TOP);
//    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state2 = this.defaultBlockState().setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM);
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();

        if ((context.getClickLocation().y - (double)pos.getY() < 0.33D) || (facing == Direction.DOWN)) {
            return state2.setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM);
        } else if ((context.getClickLocation().y - (double)pos.getY() > 0.66D) || (facing == Direction.UP)) {
            return state2.setValue(TYPE_UPMIDDLEDOWN, Third.TOP);
        } else {
            return state2.setValue(TYPE_UPMIDDLEDOWN, Third.MIDDLE);
        }
    }

    @Override
    protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE_UPMIDDLEDOWN);
    }
    @Override
    public VoxelShape getShape(BlockState state) {
        if (state.getValue(TYPE_UPMIDDLEDOWN) == Third.BOTTOM) {
            return BOTTOM_SHAPE;
        } else if (state.getValue(TYPE_UPMIDDLEDOWN) == Third.MIDDLE) {
            return MIDDLE_SHAPE;
        } else {
            return TOP_SHAPE;
        }
    }

    public static class Directional extends HorizontalDirectionalShape {

        public Directional(Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM));
        }

        @Override
        public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
            return getShape(state);
        }

//        @Override
//        public BlockState getStateForPlacement(BlockPlaceContext context) {
//            Direction facingHorizontal = context.getHorizontalDirection().getOpposite();
//            BlockState state2 = this.defaultBlockState().setValue(DIRECTION, facingHorizontal).setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM);
//            Direction facing = context.getClickedFace();
//            return facing != Direction.DOWN && (facing == Direction.UP || !(context.getClickLocation().y - (double) context.getClickedPos().getY() > 0.5D)) ? state2 : state2.setValue(TYPE_UPMIDDLEDOWN, Third.TOP);
//        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getHorizontalDirection().getOpposite();
            BlockPos pos = context.getClickedPos();

            if ((context.getClickLocation().y - (double)pos.getY() < 0.33D) || (facing == Direction.DOWN)) {
                return super.getStateForPlacement(context)
                        .setValue(DIRECTION, facing)
                        .setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM);
            } else if ((context.getClickLocation().y - (double)pos.getY() > 0.66D) || (facing == Direction.UP)) {
                return super.getStateForPlacement(context)
                        .setValue(DIRECTION, facing)
                        .setValue(TYPE_UPMIDDLEDOWN, Third.TOP);
            } else {
                return super.getStateForPlacement(context)
                        .setValue(DIRECTION, facing)
                        .setValue(TYPE_UPMIDDLEDOWN, Third.MIDDLE);
            }
        }

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> container) {
            container.add(TYPE_UPMIDDLEDOWN);
        }

        @Override
        public VoxelShape getShape(BlockState state) {
            if (state.getValue(TYPE_UPMIDDLEDOWN) == Third.BOTTOM) {
                return BOTTOM_SHAPE;
            } else {
                return TOP_SHAPE;
            }
        }

        @ItemDescription(description = "toggle_2")
        public static class Toggle2 extends Directional {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);

            public Toggle2(Properties properties) {
                super(properties);
            }

            @Override
            protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                super.addProperties(builder);
                builder.add(TOGGLE);
            }

            @SuppressWarnings("deprecation")
            @Override
            protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
                return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
            }

            @ItemDescription(description = "toggle_2")
            public static class Corner extends Toggle2 {

                public Corner(Properties props) {
                    super(props);
                }

                @Override
                public BlockState mirror(BlockState state, Mirror mirrorIn) {
                    return PlacementHelper.mirrorCornerDirection(state, mirrorIn, DIRECTION, super::mirror);
                }
            }
        }

        @ItemDescription(description = "toggle_3")
        public static class Toggle3 extends Directional {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 3);

            public Toggle3(Properties properties) {
                super(properties);
            }

            @Override
            protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                super.addProperties(builder);
                builder.add(TOGGLE);
            }

            @SuppressWarnings("deprecation")
            @Override
            protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
                return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
            }

            @ItemDescription(description = "toggle_3")
            public static class Corner extends Toggle3 {

                public Corner(Properties props) {
                    super(props);
                }

                @Override
                public BlockState mirror(BlockState state, Mirror mirrorIn) {
                    return PlacementHelper.mirrorCornerDirection(state, mirrorIn, DIRECTION, super::mirror);
                }
            }
        }

        @ItemDescription(description = "toggle_4")
        public static class Toggle4 extends Directional {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 4);

            public Toggle4(Properties properties) {
                super(properties);
            }

            @Override
            protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                super.addProperties(builder);
                builder.add(TOGGLE);
            }

            @SuppressWarnings("deprecation")
            @Override
            protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
                return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
            }
        }
    }

    public static class DirectionalWaterlogged extends WaterloggedHorizontalDirectionalShape {

        private final List<VoxelShape> hitBox;

        public DirectionalWaterlogged(Props props) {
            super(props.toSettings());
            this.hitBox = props.getOrDefault("hitBox", List.class, Arrays.asList(BOTTOM_SHAPE, TOP_SHAPE));
            this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM).setValue(WATERLOGGED, false));
        }

//        @Override
//        public BlockState getStateForPlacement(BlockPlaceContext context) {
//            FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
//            Direction facingHorizontal = context.getHorizontalDirection().getOpposite();
//            BlockState state2 = this.defaultBlockState().setValue(DIRECTION, facingHorizontal).setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM).setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
//            Direction facing = context.getClickedFace();
//            return facing != Direction.DOWN && (facing == Direction.UP || !(context.getClickLocation().y - (double) context.getClickedPos().getY() > 0.5D)) ? state2 : state2.setValue(TYPE_UPMIDDLEDOWN, Third.TOP);
//        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getHorizontalDirection().getOpposite();
            BlockPos pos = context.getClickedPos();

            if ((context.getClickLocation().y - (double)pos.getY() < 0.33D) || (facing == Direction.DOWN)) {
                return super.getStateForPlacement(context)
                        .setValue(DIRECTION, facing)
                        .setValue(WATERLOGGED, false)
                        .setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM);
            } else if ((context.getClickLocation().y - (double)pos.getY() > 0.66D) || (facing == Direction.UP)) {
                return super.getStateForPlacement(context)
                        .setValue(DIRECTION, facing)
                        .setValue(WATERLOGGED, false)
                        .setValue(TYPE_UPMIDDLEDOWN, Third.TOP);
            } else {
                return super.getStateForPlacement(context)
                        .setValue(DIRECTION, facing)
                        .setValue(WATERLOGGED, false)
                        .setValue(TYPE_UPMIDDLEDOWN, Third.MIDDLE);
            }
        }

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> container) {
            container.add(TYPE_UPMIDDLEDOWN);
        }

        @Override
        public VoxelShape getShape(BlockState state) {
            int shapesAmount = hitBox.size();

            switch (shapesAmount) {
                default:
                case 0:
                    return hitBox.get(0);
                case 2:
                    if (state.getValue(TYPE_UPMIDDLEDOWN) == Third.BOTTOM) {
                        return hitBox.get(0);
                    } else {
                        return hitBox.get(1);
                    }
                case 8:
                    if (state.getValue(TYPE_UPMIDDLEDOWN) == Third.BOTTOM) {
                        return switch (state.getValue(DIRECTION)) {
                            case EAST -> hitBox.get(1);
                            case SOUTH -> hitBox.get(2);
                            case WEST -> hitBox.get(3);
                            default -> hitBox.get(0);
                        };
                    } else {
                        return switch (state.getValue(DIRECTION)) {
                            case EAST -> hitBox.get(5);
                            case SOUTH -> hitBox.get(6);
                            case WEST -> hitBox.get(7);
                            default -> hitBox.get(4);
                        };
                    }
            }
        }

        /**
         * Use this when creating families (creates a block with _slab_directional suffix)
         */
        @Assets(
                state = @State(name = "%s_slab_directional", template = "parent_slab"),
                item = @Model(name = "item/%s_slab_directional", parent = "block/%s_slab", template = "item/acacia_slab"),
                block = {
                        @Model(name = "block/%s_slab_directional", template = "block/orientable"),
                }
        )
        public static class FamilyVariant extends DirectionalWaterlogged {

            public FamilyVariant(Props properties) {
                super(properties);
            }
        }
    }
}