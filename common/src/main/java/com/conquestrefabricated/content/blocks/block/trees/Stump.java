package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;


@Assets(
        state = @State(name = "%s_stump", template = "parent_stump"),
        item = @Model(name = "item/%s_stump", parent = "block/%s_stump_4", template = "item/dragon_egg"),
        block = {
                @Model(name = "block/%s_stump_2", template = "block/parent_pillar_2"),
                @Model(name = "block/%s_stump_4", template = "block/parent_pillar_4"),
                @Model(name = "block/%s_stump_6", template = "block/parent_pillar_6"),
                @Model(name = "block/%s_stump_down_2", template = "block/parent_stump_down_2"),
                @Model(name = "block/%s_stump_down_4", template = "block/parent_stump_down_4"),
                @Model(name = "block/%s_stump_down_6", template = "block/parent_stump_down_6"),
        }
)
public class Stump extends LogPillar {

    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    private Block fullBlock;

    public Stump(Props properties) {
        super(properties);
        this.fullBlock = properties.getParent().getBlock();
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

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();

        BlockPos blockposDown = blockpos.below();
        BlockState blockstateDown = iblockreader.getBlockState(blockposDown);

        boolean isDown = false;

        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());

        if (blockstateDown.getBlock() instanceof Layer || (blockstateDown.getBlock() instanceof Slab && blockstateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            isDown = true;
        }

        if (blockstate.getBlock() == this) {
            int i = blockstate.getValue(LAYERS);
            if (i == 5) {
                return blockstate.setValue(LAYERS, 1);
            }
            if (i == 1) {
                return blockstate.setValue(LAYERS, 4);
            }
            if (i == 2) {
                return blockstate.setValue(LAYERS, 3);
            }
            if (i == 4) {
                return blockstate.setValue(LAYERS, 2);
            }
            if (i == 3) {
                return fullBlock.defaultBlockState();
            }
            return blockstate.setValue(LAYERS, 3);
        } else {
            return super.getStateForPlacement(context).setValue(DOWN, isDown);
        }
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS, DOWN);
    }

}
