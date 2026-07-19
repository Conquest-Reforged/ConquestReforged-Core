package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
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
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@Render(RenderLayer.CUTOUT)
public class Sapling extends SaplingBlock {

    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;

    public Sapling(Props props) {
        super(props.get("tree", TreeGrower.class), props.toSettings());
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 8));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS, STAGE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos down = blockpos.below();
        BlockState blockStateDown = iblockreader.getBlockState(down);

        if (blockStateDown.hasProperty(Layer.LAYERS) || (blockStateDown.hasProperty(Slab.LAYERS) && blockStateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            return super.getStateForPlacement(context).setValue(LAYERS, blockStateDown.getValue(LAYERS));
        } else {
            return super.getStateForPlacement(context).setValue(LAYERS, 8);
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (directionToNeighbour == Direction.DOWN) {
            // Check if the neighbor block is air (meaning it was broken)
            if (neighbourState.isAir()) {
                if (ConquestConfig.INSTANCE.plantBreaking.get()) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }

        BlockPos down = currentPos.below();
        BlockState blockStateDown = level.getBlockState(down);

        if (blockStateDown.hasProperty(Layer.LAYERS) || (blockStateDown.hasProperty(Slab.LAYERS) && blockStateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random).setValue(LAYERS, blockStateDown.getValue(LAYERS));
        } else {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random).setValue(LAYERS, 8);
        }
    }

    @Override
    public boolean mayPlaceOn(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }
}
