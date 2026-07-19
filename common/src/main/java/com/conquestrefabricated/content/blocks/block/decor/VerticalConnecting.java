package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.base.WaterloggedHorizontalDirectionalShape;
import com.conquestrefabricated.core.block.base.WaterloggedShape;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.conquestrefabricated.core.block.properties.VerticalConnectionShape;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


public class VerticalConnecting extends WaterloggedShape {

    public static final EnumProperty<VerticalConnectionShape> FORM = EnumProperty.create("shape", VerticalConnectionShape.class);
    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;
    private final List<VoxelShape> hitBox;

    public VerticalConnecting(Props props) {
        super(((BlockSettingsAccessor) props.toSettings())
                .setCustomOffsetter(CustomOffsetType.LAYER_XZ)
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .dynamicShape()
        );
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FORM, VerticalConnectionShape.TOP)
                .setValue(WATERLOGGED, false));
        this.hitBox = props.get("hitBox", List.class);
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
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FORM, OFFSET_TOGGLE);
    }

    @SuppressWarnings("deprication")
    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (stateIn.getValue(WATERLOGGED)) {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (stateIn.getBlock() == this) {

            BlockState top = level.getBlockState(currentPos.above());
            BlockState bottom = level.getBlockState(currentPos.below());

            if (attachesTo(top)) {
                if (attachesTo(bottom)) {
                    return stateIn.setValue(FORM, VerticalConnectionShape.MIDDLE);
                } else {
                    return stateIn.setValue(FORM, VerticalConnectionShape.BOTTOM);
                }
            } else {
                return stateIn.setValue(FORM, VerticalConnectionShape.TOP);
            }
        }
        return stateIn;
    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean isSlab = PlacementHelper.isFacingSlab(context);

        BlockGetter iblockreader = context.getLevel();
        BlockState top = iblockreader.getBlockState(pos.above());
        BlockState bottom = iblockreader.getBlockState(pos.below());

        BlockState stateDefault = this.defaultBlockState()

                .setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER)
                .setValue(OFFSET_TOGGLE, isSlab);

        BlockState stateTop = stateDefault.setValue(FORM, VerticalConnectionShape.TOP);
        BlockState stateMiddle = stateDefault.setValue(FORM, VerticalConnectionShape.MIDDLE);
        BlockState stateBottom = stateDefault.setValue(FORM, VerticalConnectionShape.BOTTOM);

        if (attachesTo(top)) {
            if (attachesTo(bottom)) {
                return stateMiddle;
            } else {
                return stateBottom;
            }
        } else {
            return stateTop;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        return hitBox.get(0);
    }

    protected boolean attachesTo(BlockState state) {
        Block block = state.getBlock();
        return block instanceof VerticalConnecting;
    }

    //================================================================
    @SpecialOffset(offsetType = SpecialOffsetType.XYZ)
    public static class Directional extends WaterloggedHorizontalDirectionalShape {

        public static final EnumProperty<VerticalConnectionShape> FORM = EnumProperty.create("shape", VerticalConnectionShape.class);
        public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;
        private final List<VoxelShape> hitBox;

        public Directional(Props props) {
            super(((BlockSettingsAccessor) props.toSettings())
                    .setCustomOffsetter(CustomOffsetType.LAYER_XZ)
                    .offsetType(BlockBehaviour.OffsetType.NONE)
                    .dynamicShape()
            );
            this.registerDefaultState(this.stateDefinition.any()
                    .setValue(FORM, VerticalConnectionShape.TOP)
                    .setValue(DIRECTION, Direction.NORTH)
                    .setValue(WATERLOGGED, false));
            this.hitBox = props.get("hitBox", List.class);
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
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FORM, OFFSET_TOGGLE);
        }

        @SuppressWarnings("deprication")
        @Override
        public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
            if (stateIn.getValue(WATERLOGGED)) {
                ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }
            if (stateIn.getBlock() == this) {

                BlockState top = level.getBlockState(currentPos.above());
                BlockState bottom = level.getBlockState(currentPos.below());

                if (attachesTo(top, stateIn.getValue(DIRECTION))) {
                    if (attachesTo(bottom, stateIn.getValue(DIRECTION))) {
                        return stateIn.setValue(FORM, VerticalConnectionShape.MIDDLE);
                    } else {
                        return stateIn.setValue(FORM, VerticalConnectionShape.BOTTOM);
                    }
                } else {
                    return stateIn.setValue(FORM, VerticalConnectionShape.TOP);
                }
            }
            return stateIn;
        }

        @Override
        @NotNull
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            BlockPos pos = context.getClickedPos();
            FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
            boolean isSlab = PlacementHelper.isFacingSlab(context);

            BlockGetter iblockreader = context.getLevel();
            BlockState top = iblockreader.getBlockState(pos.above());
            BlockState bottom = iblockreader.getBlockState(pos.below());

            Direction facing = context.getHorizontalDirection().getOpposite();

            BlockState stateDefault = this.defaultBlockState()
                    .setValue(DIRECTION, facing)
                    .setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER)
                    .setValue(OFFSET_TOGGLE, isSlab);

            BlockState stateTop = stateDefault.setValue(FORM, VerticalConnectionShape.TOP);
            BlockState stateMiddle = stateDefault.setValue(FORM, VerticalConnectionShape.MIDDLE);
            BlockState stateBottom = stateDefault.setValue(FORM, VerticalConnectionShape.BOTTOM);

            if (attachesTo(top, facing)) {
                if (attachesTo(bottom, facing)) {
                    return stateMiddle;
                } else {
                    return stateBottom;
                }
            } else {
                return stateTop;
            }
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

        protected boolean attachesTo(BlockState state, Direction facing) {
            Block block = state.getBlock();
            return block instanceof Directional && state.getValue(DIRECTION) == facing;
        }

        //================================================================

        public static class Hinge extends VerticalConnecting {

            public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;

            public Hinge(Props props) {
                super(props);
            }

            @Override
            @NotNull
            public BlockState getStateForPlacement(BlockPlaceContext context) {
                return super.getStateForPlacement(context).setValue(DIRECTION, context.getHorizontalDirection().getOpposite()).setValue(HINGE, this.getHingeSide(context));
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

            @SuppressWarnings("deprication")
            @Override
            public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
                if (stateIn.getValue(WATERLOGGED)) {
                    ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                }
                if (stateIn.getBlock() == this) {

                    BlockState top = level.getBlockState(currentPos.above());
                    BlockState bottom = level.getBlockState(currentPos.below());

                    if (attachesTo(top, stateIn.getValue(DIRECTION))) {
                        if (attachesTo(bottom, stateIn.getValue(DIRECTION))) {
                            return stateIn.setValue(FORM, VerticalConnectionShape.MIDDLE);
                        } else {
                            return stateIn.setValue(FORM, VerticalConnectionShape.BOTTOM);
                        }
                    } else {
                        return stateIn.setValue(FORM, VerticalConnectionShape.TOP);
                    }
                }
                return stateIn;
            }

            protected boolean attachesTo(BlockState state, Direction facing) {
                Block block = state.getBlock();
                return block instanceof Hinge && state.getValue(DIRECTION) == facing;
            }
        }
    }
}