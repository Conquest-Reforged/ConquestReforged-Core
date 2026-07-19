package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@ItemDescription(description = "toggle_6")
public class Branch6 extends HorizontalDirectional {

    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 6);

    public Branch6(Props props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(DOWN, false));
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE, DOWN);
    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter blockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();

        BlockPos blockposDown = blockpos.below();
        BlockState blockstateDown = blockreader.getBlockState(blockposDown);

        boolean isDown = false;

        if (blockstateDown.getBlock() instanceof Layer || (blockstateDown.getBlock() instanceof Slab && blockstateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            isDown = true;
        }

        return super.getStateForPlacement(context).setValue(TOGGLE, 1).setValue(DOWN, isDown);
    }

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
}
