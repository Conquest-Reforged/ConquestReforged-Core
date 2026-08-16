package com.conquestrefabricated.content.blocks.block.directional;

import com.conquestrefabricated.content.blocks.BlockVoxelShapes;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.block.base.HorizontalDirectionalShape;
import com.conquestrefabricated.core.block.base.WaterloggedHorizontalDirectionalShape;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.conquestrefabricated.api.tags.ModTags.CYCLING_TOOLS;


/**
 * This contains the following static classes:
 * Toggle2
 * Toggle3
 * Toggle4
 * Toggle5
 * Toggle6
 * OffsetXYZ
 * - Toggle2
 * - Toggle3
 * - Toggle4
 * - Toggle5
 * - Toggle6
 * Waterlogged
 * - Toggle2
 * - Toggle3
 * - Toggle4
 * - Toggle5
 * - Toggle6
 * - Toggle7
 * - Toggle8
 * - OffsetXYZ
 *    - Toggle2
 *    - Toggle3
 *    - Toggle4
 *    - Toggle5
 *    - Toggle6
 *    - Toggle7
 *    - Toggle8
 */
public class HorizontalDirectional extends HorizontalDirectionalShape {

    private final List<VoxelShape> hitBox;

    public HorizontalDirectional(Props props) {
        super(props.toSettings());
        this.hitBox = props.getOrDefault("hitBox", List.class, BlockVoxelShapes.cubePartialShape);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        boolean hasFourShapes = hitBox.size() == 4;
        switch (state.getValue(DIRECTION)) {
            default:
                return hitBox.get(0);
            case EAST:
                return hitBox.get(hasFourShapes ? 1 : 0);
            case SOUTH:
                return hitBox.get(hasFourShapes ? 2 : 0);
            case WEST:
                return hitBox.get(hasFourShapes ? 3 : 0);
        }
    }

    /**
     * ============================
     * Toggles
     * ============================
     */

    @ItemDescription(description = "toggle_2")
    public static class Toggle2 extends HorizontalDirectional {
        public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);

        public Toggle2(Props props) {
            super(props);
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

            public Corner(Props props) {
                super(props);
            }

            @Override
            public BlockState mirror(BlockState state, Mirror mirrorIn) {
                return PlacementHelper.mirrorCornerDirection(state, mirrorIn, DIRECTION, super::mirror);
            }
        }
    }

    @ItemDescription(description = "toggle_3")
    public static class Toggle3 extends HorizontalDirectional {
        public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 3);

        public Toggle3(Props props) {
            super(props);
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

    @ItemDescription(description = "toggle_4")
    public static class Toggle4 extends HorizontalDirectional {
        public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 4);

        public Toggle4(Props props) {
            super(props);
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

    @ItemDescription(description = "toggle_5")
    public static class Toggle5 extends HorizontalDirectional {
        public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 5);

        public Toggle5(Props props) {
            super(props);
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

    @ItemDescription(description = "toggle_6")
    public static class Toggle6 extends HorizontalDirectional {
        public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 6);

        public Toggle6(Props props) {
            super(props);
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

    //================================================================

    public static class OffsetXYZ extends HorizontalDirectional {

        public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

        public OffsetXYZ(Props props) {
            super(props
                    .customOffsetType(CustomOffsetType.LAYER_XYZ)
                    .offset(BlockBehaviour.OffsetType.NONE)
                    .dynamicBounds(true)
            );
            this.registerDefaultState(this.stateDefinition.any().setValue(OFFSET_TOGGLE, false));
        }

        @Override
        public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            if (state.getValue(OFFSET_TOGGLE)) {
                return Shapes.empty();
            } else {
                return super.getCollisionShape(state, worldIn, pos, context);
            }
        }

        @Override
        @NotNull
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            boolean isSlab = PlacementHelper.isFacingSlab(context);
            return super.getStateForPlacement(context).setValue(OFFSET_TOGGLE, isSlab);
        }

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(OFFSET_TOGGLE);
        }

        @ItemDescription(description = "toggle_2")
        public static class Toggle2 extends OffsetXYZ {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);

            public Toggle2(Props props) {
                super(props);
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
            public static class Positions3 extends HorizontalDirectional.OffsetXYZ.Toggle2 {
                public static final IntegerProperty POSITION = IntegerProperty.create("position", 1, 3);

                public Positions3(Props props) {super(props);}

                @Override
                protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                    super.addProperties(builder);
                    builder.add(POSITION);
                }

                @SuppressWarnings("deprecation")
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
                    if (player.getAbilities().instabuild) {
                        if (player.isShiftKeyDown()) {
                            level.setBlock(blockPos, state.cycle(POSITION), 3);
                            return InteractionResult.SUCCESS;
                        }
                        level.setBlock(blockPos, state.cycle(TOGGLE), 3);
                        return InteractionResult.SUCCESS;
                    }

                    if (player.getMainHandItem().is(CYCLING_TOOLS)) {
                        if (player.isShiftKeyDown()) {
                            level.setBlock(blockPos, state.cycle(POSITION), 3);
                            return InteractionResult.SUCCESS;
                        }
                        level.setBlock(blockPos, state.cycle(TOGGLE), 3);
                        return InteractionResult.SUCCESS;
                    }

                    return InteractionResult.FAIL;
                }
            }
        }

        @ItemDescription(description = "toggle_3")
        public static class Toggle3 extends OffsetXYZ {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 3);

            public Toggle3(Props props) {
                super(props);
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
            public static class Positions4 extends HorizontalDirectional.OffsetXYZ.Toggle3 {
                public static final IntegerProperty POSITION = IntegerProperty.create("position", 1, 4);

                public Positions4(Props props) {super(props);}

                @Override
                protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                    super.addProperties(builder);
                    builder.add(POSITION);
                }

                @SuppressWarnings("deprecation")
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
                    if (player.getAbilities().instabuild) {
                        if (player.isShiftKeyDown()) {
                            level.setBlock(blockPos, state.cycle(POSITION), 3);
                            return InteractionResult.SUCCESS;
                        }
                        level.setBlock(blockPos, state.cycle(TOGGLE), 3);
                        return InteractionResult.SUCCESS;
                    }

                    if (player.getMainHandItem().is(CYCLING_TOOLS)) {
                        if (player.isShiftKeyDown()) {
                            level.setBlock(blockPos, state.cycle(POSITION), 3);
                            return InteractionResult.SUCCESS;
                        }
                        level.setBlock(blockPos, state.cycle(TOGGLE), 3);
                        return InteractionResult.SUCCESS;
                    }

                    return InteractionResult.FAIL;
                }
            }
        }

        @ItemDescription(description = "toggle_4")
        public static class Toggle4 extends OffsetXYZ {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 4);

            public Toggle4(Props props) {
                super(props);
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

            @ItemDescription(description = "toggle_4")
            public static class Positions5 extends HorizontalDirectional.OffsetXYZ.Toggle4 {
                public static final IntegerProperty POSITION = IntegerProperty.create("position", 1, 5);

                public Positions5(Props props) {super(props);}

                @Override
                protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                    super.addProperties(builder);
                    builder.add(POSITION);
                }

                @SuppressWarnings("deprecation")
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
                    if (player.getAbilities().instabuild) {
                        if (player.isShiftKeyDown()) {
                            level.setBlock(blockPos, state.cycle(POSITION), 3);
                            return InteractionResult.SUCCESS;
                        }
                        level.setBlock(blockPos, state.cycle(TOGGLE), 3);
                        return InteractionResult.SUCCESS;
                    }

                    if (player.getMainHandItem().is(CYCLING_TOOLS)) {
                        if (player.isShiftKeyDown()) {
                            level.setBlock(blockPos, state.cycle(POSITION), 3);
                            return InteractionResult.SUCCESS;
                        }
                        level.setBlock(blockPos, state.cycle(TOGGLE), 3);
                        return InteractionResult.SUCCESS;
                    }

                    return InteractionResult.FAIL;
                }
            }
        }

        @ItemDescription(description = "toggle_5")
        public static class Toggle5 extends OffsetXYZ {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 5);

            public Toggle5(Props props) {
                super(props);
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

        @ItemDescription(description = "toggle_6")
        public static class Toggle6 extends OffsetXYZ {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 6);

            public Toggle6(Props props) {
                super(props);
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

        @ItemDescription(description = "toggle_7")
        public static class Toggle7 extends OffsetXYZ {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 7);

            public Toggle7(Props props) {
                super(props);
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

        @ItemDescription(description = "toggle_8")
        public static class Toggle8 extends OffsetXYZ {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 8);

            public Toggle8(Props props) {
                super(props);
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

        @ItemDescription(description = "toggle_8")
        public static class Toggle8Position2 extends OffsetXYZ {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 8);
            public static final IntegerProperty POSITION = IntegerProperty.create("position", 1, 2);

            public Toggle8Position2(Props props) {
                super(props);
            }

            @Override
            protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                super.addProperties(builder);
                builder.add(TOGGLE, POSITION);
            }

            @SuppressWarnings("deprecation")
            @Override
            protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
                if (player.getAbilities().instabuild) {
                    if (player.isShiftKeyDown()) {
                        level.setBlock(blockPos, state.cycle(POSITION), 3);
                        return InteractionResult.SUCCESS;
                    }
                    level.setBlock(blockPos, state.cycle(TOGGLE), 3);
                    return InteractionResult.SUCCESS;
                }

                if (player.getMainHandItem().is(CYCLING_TOOLS)) {
                    if (player.isShiftKeyDown()) {
                        level.setBlock(blockPos, state.cycle(POSITION), 3);
                        return InteractionResult.SUCCESS;
                    }
                    level.setBlock(blockPos, state.cycle(TOGGLE), 3);
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.FAIL;
            }
        }

        @ItemDescription(description = "toggle_10")
        public static class Toggle10 extends OffsetXYZ {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 10);

            public Toggle10(Props props) {
                super(props);
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

    public static class OffsetXZ extends OffsetXYZ {
        public OffsetXZ(Props props) {
            super(props.customOffsetType(CustomOffsetType.LAYER_XZ));
        }
        
        public static class Hinge extends OffsetXZ {
            public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
            private final List<VoxelShape> hitBox;

            public Hinge(Props props) {
                super(props);
                this.registerDefaultState(this.stateDefinition.any().setValue(HINGE,  DoorHingeSide.LEFT));
                this.hitBox = props.get("hitBox", List.class);
            }

            @Override
            @NotNull
            public BlockState getStateForPlacement(BlockPlaceContext context) {
                return super.getStateForPlacement(context).setValue(DIRECTION, context.getHorizontalDirection().getOpposite()).setValue(HINGE, this.getHingeSide(context));
            }

            @Override
            public VoxelShape getShape(BlockState state) {
                boolean hasFourShapes = hitBox.size() == 4;
                if (state.getValue(HINGE) == DoorHingeSide.LEFT) {
                    switch (state.getValue(DIRECTION)) {
                        case NORTH:
                        default:
                            return hitBox.get(0);
                        case EAST:
                            return hitBox.get(hasFourShapes ? 1 : 0);
                        case SOUTH:
                            return hitBox.get(hasFourShapes ? 2 : 0);
                        case WEST:
                            return hitBox.get(hasFourShapes ? 3 : 0);
                    }
                } else {
                    switch (state.getValue(DIRECTION)) {
                        case NORTH:
                        default:
                            return hitBox.get(hasFourShapes ? 1 : 0);
                        case EAST:
                            return hitBox.get(hasFourShapes ? 2 : 0);
                        case SOUTH:
                            return hitBox.get(hasFourShapes ? 3 : 0);
                        case WEST:
                            return hitBox.get(0);
                    }
                }
            }

            private DoorHingeSide getHingeSide(BlockPlaceContext context) {
                BlockGetter iblockreader = context.getLevel();
                BlockPos blockpos = context.getClickedPos();
                Direction direction = context.getHorizontalDirection();
                BlockPos blockpos1 = blockpos.above();
                Direction direction1 = direction.getCounterClockWise();
                BlockPos blockpos2 = blockpos.relative(direction1);
                BlockState blockstate = iblockreader.getBlockState(blockpos2);
                BlockPos blockpos3 = blockpos1.relative(direction1);
                BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
                Direction direction2 = direction.getClockWise();
                BlockPos blockpos4 = blockpos.relative(direction2);
                BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
                BlockPos blockpos5 = blockpos1.relative(direction2);
                BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
                int i = (blockstate.isCollisionShapeFullBlock(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.isCollisionShapeFullBlock(iblockreader, blockpos3) ? -1 : 0) + (blockstate2.isCollisionShapeFullBlock(iblockreader, blockpos4) ? 1 : 0) + (blockstate3.isCollisionShapeFullBlock(iblockreader, blockpos5) ? 1 : 0);
                if (i <= 0) {
                    if (i == 0) {
                        int j = direction.getStepX();
                        int k = direction.getStepZ();
                        Vec3 vec3d = context.getClickLocation();
                        double d0 = vec3d.x - (double) blockpos.getX();
                        double d1 = vec3d.z - (double) blockpos.getZ();
                        return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
                    } else {
                        return DoorHingeSide.LEFT;
                    }
                } else {
                    return DoorHingeSide.RIGHT;
                }
            }

            protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                super.addProperties(builder);
                builder.add(HINGE);
            }
        }

        @ItemDescription(description = "toggle_5")
        public static class Toggle5 extends OffsetXZ {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 5);

            public Toggle5(Props props) {
                super(props);
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

    public static class Waterlogged extends WaterloggedHorizontalDirectionalShape {

        private final List<VoxelShape> hitBox;

        public Waterlogged(Props props) {
            super(props.toSettings());
            this.hitBox = props.get("hitBox", List.class);
        }

        @Override
        public VoxelShape getShape(BlockState state) {
            boolean hasFourShapes = hitBox.size() == 4;
            return switch (state.getValue(DIRECTION)) {
                case EAST -> hitBox.get(hasFourShapes ? 1 : 0);
                case SOUTH -> hitBox.get(hasFourShapes ? 2 : 0);
                case WEST -> hitBox.get(hasFourShapes ? 3 : 0);
                default -> hitBox.get(0);
            };
        }

        @ItemDescription(description = "toggle_2")
        public static class Toggle2 extends Waterlogged {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);

            public Toggle2(Props props) {
                super(props);
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

        @ItemDescription(description = "toggle_3")
        public static class Toggle3 extends Waterlogged {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 3);

            public Toggle3(Props props) {
                super(props);
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

        @ItemDescription(description = "toggle_4")
        public static class Toggle4 extends Waterlogged {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 4);

            public Toggle4(Props props) {
                super(props);
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

        @ItemDescription(description = "toggle_5")
        public static class Toggle5 extends Waterlogged {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 5);

            public Toggle5(Props props) {
                super(props);
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

        @ItemDescription(description = "toggle_6")
        public static class Toggle6 extends Waterlogged {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 6);

            public Toggle6(Props props) {
                super(props);
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

        @ItemDescription(description = "toggle_7")
        public static class Toggle7 extends Waterlogged {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 7);

            public Toggle7(Props props) {
                super(props);
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

        @ItemDescription(description = "toggle_8")
        public static class Toggle8 extends Waterlogged {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 8);

            public Toggle8(Props props) {
                super(props);
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

        public static class OffsetXYZ extends Waterlogged {

            public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

            public OffsetXYZ(Props props) {
                super(props
                        .customOffsetType(CustomOffsetType.LAYER_XYZ)
                        .offset(BlockBehaviour.OffsetType.NONE)
                        .dynamicBounds(true)
                );
                this.registerDefaultState(this.stateDefinition.any().setValue(OFFSET_TOGGLE, false).setValue(WATERLOGGED, false));
            }

            @Override
            public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
                if (state.getValue(OFFSET_TOGGLE)) {
                    return Shapes.empty();
                } else {
                    return super.getCollisionShape(state, worldIn, pos, context);
                }
            }

            @Override
            @NotNull
            public BlockState getStateForPlacement(BlockPlaceContext context) {
                boolean isSlab = PlacementHelper.isFacingSlab(context);
                return super.getStateForPlacement(context).setValue(OFFSET_TOGGLE, isSlab);
            }

            @Override
            protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                super.addProperties(builder);
                builder.add(OFFSET_TOGGLE);
            }

            @ItemDescription(description = "toggle_2")
            public static class Toggle2 extends OffsetXYZ {
                public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);

                public Toggle2(Props props) {
                    super(props);
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

            @ItemDescription(description = "toggle_3")
            public static class Toggle3 extends OffsetXYZ {
                public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 3);

                public Toggle3(Props props) {
                    super(props);
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

            @ItemDescription(description = "toggle_4")
            public static class Toggle4 extends OffsetXYZ {
                public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 4);

                public Toggle4(Props props) {
                    super(props);
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

            @ItemDescription(description = "toggle_5")
            public static class Toggle5 extends OffsetXYZ {
                public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 5);

                public Toggle5(Props props) {
                    super(props);
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

            @ItemDescription(description = "toggle_6")
            public static class Toggle6 extends OffsetXYZ {
                public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 6);

                public Toggle6(Props props) {
                    super(props);
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

            @ItemDescription(description = "toggle_7")
            public static class Toggle7 extends OffsetXYZ {
                public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 7);

                public Toggle7(Props props) {
                    super(props);
                }

                @Override
                protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                    super.addProperties(builder);
                    builder.add(TOGGLE);
                }

                @SuppressWarnings("deprecation")
                @Override
                public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
                    return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
                }
            }

            @ItemDescription(description = "toggle_8")
            public static class Toggle8 extends OffsetXYZ {
                public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 8);

                public Toggle8(Props props) {
                    super(props);
                }

                @Override
                protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                    super.addProperties(builder);
                    builder.add(TOGGLE);
                }

                @SuppressWarnings("deprecation")
                @Override
                public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
                    return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
                }
            }
        }
    }
}
