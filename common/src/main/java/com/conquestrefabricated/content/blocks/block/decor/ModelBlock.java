package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.base.Shape;
import com.conquestrefabricated.core.block.base.WaterloggedShape;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.conquestrefabricated.content.blocks.util.PlacementHelper.isFacingSlab;


/**
 * This class contains the static classes:
 * Waterlogged
 * - Toggle2
 * - Toggle3
 * - Toggle4
 * OffsetY
 * OffsetXYZ
 */
public class ModelBlock extends Shape {

    private final List<VoxelShape> hitBox;

    public ModelBlock(Props props) {
        super(props.toSettings());
        this.hitBox = props.get("hitBox", List.class);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        return hitBox.get(0);
    }

    public static class Waterlogged extends WaterloggedShape {

        private final List<VoxelShape> hitBox;

        public Waterlogged(Props props) {
            super(props.toSettings());
            this.hitBox = props.get("hitBox", List.class);
            registerDefaultState((this.stateDefinition.any()).setValue(WATERLOGGED, false));
        }

        @Override
        public VoxelShape getShape(BlockState state) {
            return hitBox.get(0);
        }
    }

    @SpecialOffset(offsetType = SpecialOffsetType.Y)
    public static class OffsetY extends ModelBlock {

        public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

        public OffsetY(Props props) {
            super(props
                    .customOffsetType(CustomOffsetType.LAYER_Y)
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
            boolean isSlab = isFacingSlab(context);
            return super.getStateForPlacement(context).setValue(OFFSET_TOGGLE, isSlab);
        }

        @Override
        protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(OFFSET_TOGGLE);
        }
    }

    @SpecialOffset(offsetType = SpecialOffsetType.XYZ)
    public static class OffsetXYZ extends OffsetY {
        public OffsetXYZ(Props props) {
            super(props.customOffsetType(CustomOffsetType.LAYER_XYZ));
        }
    }

    /**
     * This is primarily used for hanging rope and chain toggles
     */
    @ItemDescription(description = "toggle_2")
    public static class Toggle2 extends Waterlogged {
        public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);

        public Toggle2(Props props) {super(props);}

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
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

        public Toggle3(Props props) {super(props);}

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
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

        public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1,  4);

        public Toggle4(Props props) {super(props);}

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(TOGGLE);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
            return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
        }
    }

}
