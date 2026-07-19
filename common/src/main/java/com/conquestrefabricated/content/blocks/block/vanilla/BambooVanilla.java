package com.conquestrefabricated.content.blocks.block.vanilla;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BambooVanilla extends BambooStalkBlock {

    public BambooVanilla(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
        FluidState fluidstate = p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos());
        if (!fluidstate.isEmpty()) {
            return null;
        } else {
            BlockState blockstate = p_196258_1_.getLevel().getBlockState(p_196258_1_.getClickedPos().below());
            if (blockstate.is(BlockTags.SUPPORTS_BAMBOO)) {
                if (blockstate.is(Blocks.BAMBOO_SAPLING)) {
                    return this.defaultBlockState().setValue(AGE, Integer.valueOf(0));
                } else if (blockstate.is(this)) {
                    int i = blockstate.getValue(AGE) > 0 ? 1 : 0;
                    return this.defaultBlockState().setValue(AGE, Integer.valueOf(i));
                } else {
                    BlockState blockstate1 = p_196258_1_.getLevel().getBlockState(p_196258_1_.getClickedPos().above());
                    return !blockstate1.is(this) && !blockstate1.is(Blocks.BAMBOO_SAPLING) ? this.defaultBlockState().setValue(AGE, Integer.valueOf(0)) : this.defaultBlockState().setValue(AGE, blockstate1.getValue(AGE));
                }
            } else {
                return null;
            }
        }
    }

    @Override
    protected int getHeightAboveUpToMax(BlockGetter world, BlockPos pos) {
        int i;
        for(i = 0; i < 16 && world.getBlockState(pos.above(i + 1)).is(this); ++i) {
        }

        return i;
    }

    @Override
    protected int getHeightBelowUpToMax(BlockGetter world, BlockPos pos) {
        int i;
        for(i = 0; i < 16 && world.getBlockState(pos.below(i + 1)).is(this); ++i) {
        }

        return i;
    }
}
