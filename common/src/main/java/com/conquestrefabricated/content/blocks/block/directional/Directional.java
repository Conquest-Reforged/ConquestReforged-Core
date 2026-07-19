package com.conquestrefabricated.content.blocks.block.directional;

import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.block.base.DirectionalShape;
import com.conquestrefabricated.core.block.base.WaterloggedDirectionalShape;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/**
 * This contains the following static classes:
 * Toggle2
 * Waterlogged
 * - WaterloggedToggle2
 */
public class Directional extends DirectionalShape {

    private final List<VoxelShape> hitBox;

    public Directional(Props props) {
        super(props.toSettings());
        this.hitBox = props.get("hitBox", List.class);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        return getVoxelShape(hitBox, state, DIRECTION);
    }

    private static VoxelShape getVoxelShape(List<VoxelShape> hitBox, BlockState state, EnumProperty<Direction> direction) {
        boolean hasSixShapes = hitBox.size() == 6;
        return switch (state.getValue(direction)) {
            case EAST -> hitBox.get(hasSixShapes ? 1 : 0);
            case SOUTH -> hitBox.get(hasSixShapes ? 2 : 0);
            case WEST -> hitBox.get(hasSixShapes ? 3 : 0);
            case UP -> hitBox.get(hasSixShapes ? 4 : 0);
            case DOWN -> hitBox.get(hasSixShapes ? 5 : 0);
            default -> hitBox.get(0);
        };
    }

    public static class Waterlogged extends WaterloggedDirectionalShape {
        private final List<VoxelShape> hitBox;

        public Waterlogged(Props props) {
            super(props.toSettings());
            this.registerDefaultState((this.stateDefinition.any()).setValue(WATERLOGGED, false));
            this.hitBox = props.get("hitBox", List.class);
        }

        @Override
        public VoxelShape getShape(BlockState state) {
            return getVoxelShape(hitBox, state, DIRECTION);
        }
    }

    @ItemDescription(description = "toggle_2")
    public static class Toggle2 extends Directional {
        public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);

        public Toggle2(Props props) {
            super(props);
        }

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

    @ItemDescription(description = "toggle_2")
    public static class WaterloggedToggle2 extends Waterlogged {
        public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);

        public WaterloggedToggle2(Props props) {
            super(props);
        }

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
