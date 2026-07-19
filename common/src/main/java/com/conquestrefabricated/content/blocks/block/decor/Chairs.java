package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.content.blocks.util.ChairBehavior;
import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.content.blocks.util.Sittable;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.base.ShapeBlock;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ParallelConnectionShape2;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class contains the static classes:
 * NonDirectional
 * Directional
 * Toggle2
 * Toggle4
 * Toggle6
 * Bench
 * WideBench
 */
public class Chairs {
    @Render(RenderLayer.CUTOUT)
    @SpecialOffset(offsetType = SpecialOffsetType.Y)
    public static class NonDirectional extends ShapeBlock implements EntityBlock, Sittable {
        private final ChairBehavior behavior;

        public NonDirectional(Props properties) {
            super(properties
                    .customOffsetType(CustomOffsetType.LAYER_Y)
                    .offset(OffsetType.NONE)
                    .dynamicBounds(true)
            );

            double chairHeight = properties.get("seatHeight", Double.class);
            this.behavior = new ChairBehavior(chairHeight);
            this.registerDefaultState(this.stateDefinition.any().setValue(ChairBehavior.OFFSET_TOGGLE, false));
        }

        @Override
        public double getSeatHeight() {
            return behavior.getSeatHeight();
        }

        @Override
        public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            return behavior.getCollisionShape(state, worldIn, pos, context, super.getCollisionShape(state, worldIn, pos, context));
        }

        @Override
        @NotNull
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getClickedFace().getOpposite();
            BlockGetter blockreader = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = blockreader.getBlockState(pos.relative(facing));
            Block block = state.getBlock();
            boolean isSlab = behavior.calculateSlabOffset(facing, block, state, context);

            return super.getStateForPlacement(context).setValue(ChairBehavior.OFFSET_TOGGLE, isSlab);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
            return behavior.handleSittingInteraction(state, level, blockPos, player, hitResult,
                    () -> super.useWithoutItem(state, level, blockPos, player, hitResult), null);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
            return behavior.createBlockEntity(blockPos, blockState);
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(ChairBehavior.OFFSET_TOGGLE);
        }
    }

    @Render(RenderLayer.CUTOUT)
    @SpecialOffset(offsetType = SpecialOffsetType.XYZ)
    public static class Directional extends HorizontalDirectional.Waterlogged implements EntityBlock, Sittable {
        private final ChairBehavior behavior;

        public Directional(Props props) {
            super(props
                    .customOffsetType(CustomOffsetType.LAYER_XYZ)
                    .offset(BlockBehaviour.OffsetType.NONE)
                    .dynamicBounds(true)
            );

            double chairHeight = props.get("seatHeight", Double.class);
            this.behavior = new ChairBehavior(chairHeight);
            this.registerDefaultState(this.stateDefinition.any().setValue(ChairBehavior.OFFSET_TOGGLE, false));
        }

        @Override
        public double getSeatHeight() {
            return behavior.getSeatHeight();
        }

        @Override
        public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            return behavior.getCollisionShape(state, worldIn, pos, context, super.getCollisionShape(state, worldIn, pos, context));
        }

        @Override
        @NotNull
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getClickedFace().getOpposite();
            BlockGetter blockreader = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = blockreader.getBlockState(pos.relative(facing));
            Block block = state.getBlock();
            boolean isSlab = behavior.calculateSlabOffset(facing, block, state, context);

            return super.getStateForPlacement(context).setValue(ChairBehavior.OFFSET_TOGGLE, isSlab);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
            return behavior.handleSittingInteraction(state, level, blockPos, player, hitResult,
                    () -> super.useWithoutItem(state, level, blockPos, player, hitResult), null);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
            return behavior.createBlockEntity(blockPos, blockState);
        }

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(ChairBehavior.OFFSET_TOGGLE);
        }
    }

    @ItemDescription(description = "toggle_2")
    @Render(RenderLayer.CUTOUT)
    @SpecialOffset(offsetType = SpecialOffsetType.XYZ)
    public static class Toggle2 extends HorizontalDirectional.Waterlogged.Toggle2 implements EntityBlock, Sittable {
        private final ChairBehavior behavior;
        private final double chairHeight;

        public Toggle2(Props props) {
            super(props
                    .customOffsetType(CustomOffsetType.LAYER_XYZ)
                    .offset(BlockBehaviour.OffsetType.NONE)
                    .dynamicBounds(true)
            );

            this.chairHeight = props.get("seatHeight", Double.class);
            this.behavior = new ChairBehavior(chairHeight);
            this.registerDefaultState(this.stateDefinition.any().setValue(ChairBehavior.OFFSET_TOGGLE, false));
        }

        @Override
        public double getSeatHeight() {
            return chairHeight;
        }

        @Override
        public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            return behavior.getCollisionShape(state, worldIn, pos, context, super.getCollisionShape(state, worldIn, pos, context));
        }

        @Override
        @NotNull
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getClickedFace().getOpposite();
            BlockGetter blockreader = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = blockreader.getBlockState(pos.relative(facing));
            Block block = state.getBlock();
            boolean isSlab = behavior.calculateSlabOffset(facing, block, state, context);

            return super.getStateForPlacement(context).setValue(ChairBehavior.OFFSET_TOGGLE, isSlab);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
            return behavior.handleSittingInteraction(state, level, blockPos, player, hitResult,
                    () -> Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE), 3);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
            return behavior.createBlockEntity(blockPos, blockState);
        }

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(TOGGLE, ChairBehavior.OFFSET_TOGGLE);
        }
    }

    @ItemDescription(description = "toggle_3")
    @Render(RenderLayer.CUTOUT)
    @SpecialOffset(offsetType = SpecialOffsetType.XYZ)
    public static class Toggle3 extends HorizontalDirectional.Waterlogged.Toggle3 implements EntityBlock, Sittable {
        private final ChairBehavior behavior;
        private final double chairHeight;

        public Toggle3(Props props) {
            super(props
                    .customOffsetType(CustomOffsetType.LAYER_XYZ)
                    .offset(BlockBehaviour.OffsetType.NONE)
                    .dynamicBounds(true)
            );

            this.chairHeight = props.get("seatHeight", Double.class);
            this.behavior = new ChairBehavior(chairHeight);
            this.registerDefaultState(this.stateDefinition.any().setValue(ChairBehavior.OFFSET_TOGGLE, false).setValue(WATERLOGGED, false));
        }

        @Override
        public double getSeatHeight() {
            return chairHeight;
        }

        @Override
        public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            return behavior.getCollisionShape(state, worldIn, pos, context, super.getCollisionShape(state, worldIn, pos, context));
        }

        @Override
        @NotNull
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getClickedFace().getOpposite();
            BlockGetter blockreader = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = blockreader.getBlockState(pos.relative(facing));
            Block block = state.getBlock();
            boolean isSlab = behavior.calculateSlabOffset(facing, block, state, context);

            return super.getStateForPlacement(context).setValue(ChairBehavior.OFFSET_TOGGLE, isSlab);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
            return behavior.handleSittingInteraction(state, level, blockPos, player, hitResult,
                    () -> Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE), 2);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
            return behavior.createBlockEntity(blockPos, blockState);
        }

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(TOGGLE, ChairBehavior.OFFSET_TOGGLE);
        }
    }

    @ItemDescription(description = "toggle_4")
    @Render(RenderLayer.CUTOUT)
    @SpecialOffset(offsetType = SpecialOffsetType.XYZ)
    public static class Toggle4 extends HorizontalDirectional.Waterlogged.Toggle4 implements EntityBlock, Sittable {
        private final ChairBehavior behavior;
        private final double chairHeight;

        public Toggle4(Props props) {
            super(props
                    .customOffsetType(CustomOffsetType.LAYER_XYZ)
                    .offset(BlockBehaviour.OffsetType.NONE)
                    .dynamicBounds(true)
            );

            this.chairHeight = props.get("seatHeight", Double.class);
            this.behavior = new ChairBehavior(chairHeight);
            this.registerDefaultState(this.stateDefinition.any().setValue(ChairBehavior.OFFSET_TOGGLE, false));
        }

        @Override
        public double getSeatHeight() {
            return chairHeight;
        }

        @Override
        public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            return behavior.getCollisionShape(state, worldIn, pos, context, super.getCollisionShape(state, worldIn, pos, context));
        }

        @Override
        @NotNull
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getClickedFace().getOpposite();
            BlockGetter blockreader = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = blockreader.getBlockState(pos.relative(facing));
            Block block = state.getBlock();
            boolean isSlab = behavior.calculateSlabOffset(facing, block, state, context);

            return super.getStateForPlacement(context).setValue(ChairBehavior.OFFSET_TOGGLE, isSlab);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
            return behavior.handleSittingInteraction(state, level, blockPos, player, hitResult,
                    () -> Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE), 2);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
            return behavior.createBlockEntity(blockPos, blockState);
        }

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(TOGGLE, ChairBehavior.OFFSET_TOGGLE);
        }
    }

    @ItemDescription(description = "toggle_5")
    @Render(RenderLayer.CUTOUT)
    @SpecialOffset(offsetType = SpecialOffsetType.XYZ)
    public static class Toggle5 extends HorizontalDirectional.Toggle5 implements EntityBlock, Sittable {
        private final ChairBehavior behavior;
        private final double chairHeight;

        public Toggle5(Props props) {
            super(props
                    .customOffsetType(CustomOffsetType.LAYER_XYZ)
                    .offset(BlockBehaviour.OffsetType.NONE)
                    .dynamicBounds(true)
            );

            this.chairHeight = props.get("seatHeight", Double.class);
            this.behavior = new ChairBehavior(props.get("seatHeight", Double.class));
            this.registerDefaultState(this.stateDefinition.any().setValue(ChairBehavior.OFFSET_TOGGLE, false));
        }

        @Override
        public double getSeatHeight() {
            return chairHeight;
        }

        @Override
        public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            return behavior.getCollisionShape(state, worldIn, pos, context, super.getCollisionShape(state, worldIn, pos, context));
        }

        @Override
        @NotNull
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getClickedFace().getOpposite();
            BlockGetter blockreader = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = blockreader.getBlockState(pos.relative(facing));
            Block block = state.getBlock();
            boolean isSlab = behavior.calculateSlabOffset(facing, block, state, context);

            return super.getStateForPlacement(context).setValue(ChairBehavior.OFFSET_TOGGLE, isSlab);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
            return behavior.handleSittingInteraction(state, level, blockPos, player, hitResult,
                    () -> Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE), 5);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
            return behavior.createBlockEntity(blockPos, blockState);
        }

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(ChairBehavior.OFFSET_TOGGLE, TOGGLE);
        }
    }

    @ItemDescription(description = "toggle_6")
    @Render(RenderLayer.CUTOUT)
    @SpecialOffset(offsetType = SpecialOffsetType.XYZ)
    public static class Toggle6 extends HorizontalDirectional.Toggle6 implements EntityBlock, Sittable {
        private final ChairBehavior behavior;
        private final double chairHeight;

        public Toggle6(Props props) {
            super(props
                    .customOffsetType(CustomOffsetType.LAYER_XYZ)
                    .offset(BlockBehaviour.OffsetType.NONE)
                    .dynamicBounds(true)
            );

            this.chairHeight = props.get("seatHeight", Double.class);
            this.behavior = new ChairBehavior(props.get("seatHeight", Double.class));
            this.registerDefaultState(this.stateDefinition.any().setValue(ChairBehavior.OFFSET_TOGGLE, false));
        }

        @Override
        public double getSeatHeight() {
            return chairHeight;
        }

        @Override
        public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            return behavior.getCollisionShape(state, worldIn, pos, context, super.getCollisionShape(state, worldIn, pos, context));
        }

        @Override
        @NotNull
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getClickedFace().getOpposite();
            BlockGetter blockreader = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = blockreader.getBlockState(pos.relative(facing));
            Block block = state.getBlock();
            boolean isSlab = behavior.calculateSlabOffset(facing, block, state, context);

            return super.getStateForPlacement(context).setValue(ChairBehavior.OFFSET_TOGGLE, isSlab);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
            return behavior.handleSittingInteraction(state, level, blockPos, player, hitResult,
                    () -> Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE), 7);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
            return behavior.createBlockEntity(blockPos, blockState);
        }

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(ChairBehavior.OFFSET_TOGGLE, TOGGLE);
        }
    }

    @Render(RenderLayer.CUTOUT)
    @SpecialOffset(offsetType = SpecialOffsetType.XYZ)
    public static class Bench extends ParallelConnecting implements EntityBlock, Sittable {
        protected static final VoxelShape SHAPE = Block.box(1.0D, 10.0D, 1.0D, 15.0D, 16.0D, 15.0D);

        public static final VoxelShape SHAPE_NORTH = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 12.0D, 12.0D);
        public static final VoxelShape SHAPE_EAST = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 12.0D, 16.0D);

        public static final EnumProperty<ParallelConnectionShape2> FORM = EnumProperty.create("shape", ParallelConnectionShape2.class);
        private final ChairBehavior behavior;

        public Bench(Props props) {
            super(props);
            double chairHeight = props.get("seatHeight", Double.class);
            this.behavior = new ChairBehavior(chairHeight);
        }

        @Override
        public double getSeatHeight() {
            return behavior.getSeatHeight();
        }

        @Override
        public VoxelShape getShape(BlockState state) {
            return switch (state.getValue(DIRECTION)) {
                case EAST, WEST -> SHAPE_EAST;
                default -> SHAPE_NORTH;
            };
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
            return behavior.handleSittingInteraction(state, level, blockPos, player, hitResult,
                    () -> super.useWithoutItem(state, level, blockPos, player, hitResult), null);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
            return behavior.createBlockEntity(blockPos, blockState);
        }

        //================================================================
        public static class Toggle4 extends Bench implements EntityBlock, Sittable {
            public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 4);
            private final ChairBehavior behavior;

            public Toggle4(Props props) {
                super(props);
                this.behavior = new ChairBehavior(props.get("seatHeight", Double.class));
            }

            @Override
            protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
                super.addProperties(builder);
                builder.add(TOGGLE);
            }

            @Override
            protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
                return behavior.handleSittingInteraction(state, level, blockPos, player, hitResult,
                        () -> Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE), 5);
            }
        }
    }

    @Render(RenderLayer.CUTOUT)
    @SpecialOffset(offsetType = SpecialOffsetType.XYZ)
    public static class WideBench extends Bench implements EntityBlock, Sittable {

        protected static final VoxelShape SHAPE_FULLPARTIAL = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

        public WideBench(Props props) {
            super(props);
        }

        @Override
        public VoxelShape getShape(BlockState state) {
            return SHAPE_FULLPARTIAL;
        }
    }
}


