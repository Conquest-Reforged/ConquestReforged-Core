package com.conquestrefabricated.content.blocks.block.vanilla;

import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class TallGrassVanilla extends DoublePlantBlock {

    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;

    public TallGrassVanilla(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(LAYERS, 8).setValue(OFFSET_TOGGLE, false));
    }

    @Override
    public boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
        if (PlacementHelper.isDuringWorldGen(world)) {
            return super.mayPlaceOn(state, world, pos);
        }
        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return super.canSurvive(state, worldIn, pos);
        } else {
            BlockState blockstate = worldIn.getBlockState(pos.below());
            if (state.getBlock() != this) {
                return super.canSurvive(state, worldIn, pos); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            }
            if (blockstate.getBlock() == this && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                return true;
            } else {
                return true;
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos down = blockpos.below();
        BlockState blockStateDown = iblockreader.getBlockState(down);

        if (iblockreader.getBlockState(blockpos.above()).canBeReplaced(context)) {
            if (blockStateDown.hasProperty(Layer.LAYERS) || blockStateDown.hasProperty(Slab.LAYERS) || blockStateDown.hasProperty(TallGrassVanilla.LAYERS)) {
                return super.getStateForPlacement(context).setValue(LAYERS, blockStateDown.getValue(LAYERS));
            } else {
                return super.getStateForPlacement(context).setValue(LAYERS, 8);
            }
        } else {
            return null;
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        DoubleBlockHalf doubleblockhalf = stateIn.getValue(HALF);
        BlockPos down = currentPos.below();
        BlockState blockStateDown = level.getBlockState(down);

        if ((directionToNeighbour.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (directionToNeighbour == Direction.UP) || neighbourState.getBlock() == this && neighbourState.getValue(HALF) != doubleblockhalf) && blockStateDown.hasProperty(Layer.LAYERS) || blockStateDown.hasProperty(Slab.LAYERS) || blockStateDown.hasProperty(TallGrassVanilla.LAYERS)) {
            return doubleblockhalf == DoubleBlockHalf.LOWER && directionToNeighbour == Direction.DOWN && !stateIn.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn.setValue(LAYERS, blockStateDown.getValue(LAYERS));
        } else if ((directionToNeighbour.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (directionToNeighbour == Direction.UP) || neighbourState.getBlock() == this && neighbourState.getValue(HALF) != doubleblockhalf)) {
            return doubleblockhalf == DoubleBlockHalf.LOWER && directionToNeighbour == Direction.DOWN && !stateIn.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn;
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, LAYERS, OFFSET_TOGGLE);
    }
}
