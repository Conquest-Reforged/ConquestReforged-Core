package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.content.blocks.BlockVoxelShapes;
import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.content.blocks.block.directional.ThirdBlock;
import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.properties.Third;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;
import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPMIDDLEDOWN;

@ItemDescription(description = "toggle_2")
public class BranchTip extends HorizontalDirectional {

    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);

    public BranchTip(Props props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(DOWN, false).setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM).setValue(TOGGLE, 1).setValue(DIRECTION, Direction.NORTH));
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE, DOWN, TYPE_UPMIDDLEDOWN);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();

        BlockGetter blockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();

        BlockPos blockposDown = blockpos.below();
        BlockState blockstateDown = blockreader.getBlockState(blockposDown);

        boolean isDown = false;

        if (blockstateDown.getBlock() instanceof Layer || (blockstateDown.getBlock() instanceof Slab && blockstateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            isDown = true;
        }

        if ((context.getClickLocation().y - (double)pos.getY() < 0.33D) || (facing == Direction.DOWN)) {
            return super.getStateForPlacement(context)
                    .setValue(DIRECTION, facing)
                    .setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM)
                    .setValue(TOGGLE, 1)
                    .setValue(DOWN, isDown);
        } else if ((context.getClickLocation().y - (double)pos.getY() > 0.66D) || (facing == Direction.UP)) {
            return super.getStateForPlacement(context)
                    .setValue(DIRECTION, facing)
                    .setValue(TYPE_UPMIDDLEDOWN, Third.TOP)
                    .setValue(TOGGLE, 1)
                    .setValue(DOWN, isDown);
        } else {
            return super.getStateForPlacement(context)
                    .setValue(DIRECTION, facing)
                    .setValue(TYPE_UPMIDDLEDOWN, Third.MIDDLE)
                    .setValue(TOGGLE, 1)
                    .setValue(DOWN, isDown);
        }
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        BlockState down = level.getBlockState(currentPos.below());
        if (down.getBlock() instanceof Layer || (down.getBlock() instanceof Slab && down.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            return stateIn.setValue(DOWN, true);
        } else {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
    }



    // Tip Corner
    @ItemDescription(description = "toggle_2")
    public static class Corner extends BranchTip {

        public Corner(Props props) {
            super(props);
            this.registerDefaultState(this.stateDefinition.any().setValue(DOWN, false).setValue(TYPE_UPMIDDLEDOWN, Third.BOTTOM).setValue(TOGGLE, 1).setValue(DIRECTION, Direction.NORTH));
        }

        @NotNull
        @Override
        public BlockState mirror(BlockState state, Mirror mirrorIn) {
            switch (mirrorIn) {
                case FRONT_BACK:
                    switch(state.getValue(DIRECTION)) {
                        case NORTH:
                            return state.setValue(DIRECTION, Direction.EAST);
                        case EAST:
                            return state.setValue(DIRECTION, Direction.NORTH);
                        case SOUTH:
                            return state.setValue(DIRECTION, Direction.WEST);
                        case WEST:
                            return state.setValue(DIRECTION, Direction.SOUTH);
                        default:
                            return super.mirror(state, mirrorIn);
                    }
                case LEFT_RIGHT:
                    switch(state.getValue(DIRECTION)) {
                        case NORTH:
                            return state.setValue(DIRECTION, Direction.WEST);
                        case EAST:
                            return state.setValue(DIRECTION, Direction.SOUTH);
                        case SOUTH:
                            return state.setValue(DIRECTION, Direction.EAST);
                        case WEST:
                            return state.setValue(DIRECTION, Direction.NORTH);
                        default:
                            return super.mirror(state, mirrorIn);
                    }
            }
            return super.mirror(state, mirrorIn);
        }
    }



    // Tip2
    @ItemDescription(description = "toggle_2")
    public static class Top extends ThirdBlock.Directional.Toggle2 {

        public Top(Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(TYPE_UPMIDDLEDOWN, Third.TOP).setValue(TOGGLE, 1).setValue(DIRECTION, Direction.NORTH));
        }

        private static final VoxelShape CUBE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);


        @Override
        public VoxelShape getShape(BlockState state) {
            return BlockVoxelShapes.cubePartialShape.getFirst();
        }

        // Tip2 Corner
        @ItemDescription(description = "toggle_2")
        public static class Corner extends BranchTip.Top {

            public Corner(Properties properties) {
                super(properties);
                this.registerDefaultState(this.stateDefinition.any().setValue(TYPE_UPMIDDLEDOWN, Third.TOP).setValue(TOGGLE, 1).setValue(DIRECTION, Direction.NORTH));
            }

            @Override
            public BlockState mirror(BlockState state, Mirror mirrorIn) {
                switch (mirrorIn) {
                    case FRONT_BACK:
                        switch(state.getValue(DIRECTION)) {
                            case NORTH:
                                return state.setValue(DIRECTION, Direction.EAST);
                            case EAST:
                                return state.setValue(DIRECTION, Direction.NORTH);
                            case SOUTH:
                                return state.setValue(DIRECTION, Direction.WEST);
                            case WEST:
                                return state.setValue(DIRECTION, Direction.SOUTH);
                            default:
                                return super.mirror(state, mirrorIn);
                        }
                    case LEFT_RIGHT:
                        switch(state.getValue(DIRECTION)) {
                            case NORTH:
                                return state.setValue(DIRECTION, Direction.WEST);
                            case EAST:
                                return state.setValue(DIRECTION, Direction.SOUTH);
                            case SOUTH:
                                return state.setValue(DIRECTION, Direction.EAST);
                            case WEST:
                                return state.setValue(DIRECTION, Direction.NORTH);
                            default:
                                return super.mirror(state, mirrorIn);
                        }
                }
                return super.mirror(state, mirrorIn);
            }
        }
    }
}