package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.content.blocks.BlockVoxelShapes;
import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@Render(RenderLayer.CUTOUT)
public class Corn extends Crops {

    public Corn(Props props) {
        super(props);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return BlockVoxelShapes.pillarShape.get(0);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        super.tick(state, world, pos, rand);

        if (world.hasChunksAt(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            if (world.getRawBrightness(pos.above(), 0) >= 9) {
                int i = this.getAge(state);

                if (i < this.getMaxAge()) {

                    if (world.isEmptyBlock(pos.above())) {
                        int height;

                        for (height = 1; world.getBlockState(pos.below(height)).getBlock() == this; ++height) {
                        }

                        if (height < 3) {

                            float f = getAvailableMoisture1(this, world, pos);
                            if (rand.nextInt((int)(25.0F / f) + 1) == 0) {
                                world.setBlock(pos, state.setValue(AGE, i + 1), 2);
                                world.setBlock(pos.above(), this.defaultBlockState().setValue(LAYERS, state.getValue(LAYERS)), 2);
                            }
                        } else {
                            float f = getAvailableMoisture1(this, world, pos);
                            if (rand.nextInt((int)(25.0F / f) + 1) == 0) {
                                world.setBlock(pos, state.setValue(AGE, i + 1), 2);
                            }
                        }
                    } else {
                        float f = getAvailableMoisture1(this, world, pos);
                        if (rand.nextInt((int)(25.0F / f) + 1) == 0) {
                            world.setBlock(pos, state.setValue(AGE, i + 1), 2);
                        }
                    }
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos up = blockpos.above();
        BlockPos down = blockpos.below();
        BlockState blockStateUp = iblockreader.getBlockState(up);
        BlockState blockStateDown = iblockreader.getBlockState(down);
        int layerBlockState = 8;

        if (blockStateDown.hasProperty(Layer.LAYERS) || blockStateDown.hasProperty(Slab.LAYERS) || blockStateDown.hasProperty(Bush.LAYERS)) {
            layerBlockState = blockStateDown.getValue(LAYERS);
        } else if (blockStateUp.hasProperty(Layer.LAYERS) || blockStateUp.hasProperty(Slab.LAYERS) || blockStateUp.hasProperty(Bush.LAYERS)) {
            layerBlockState = blockStateUp.getValue(LAYERS);
        }

        return super.getStateForPlacement(context)
                .setValue(LAYERS, layerBlockState);
    }

    //Copy so that other farmlands can work
    protected static float getAvailableMoisture1(Block block, BlockGetter world, BlockPos pos) {
        float f = 1.0F;
        BlockPos blockPos = pos.below();

        for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
                float g = 0.0F;
                BlockState blockState = world.getBlockState(blockPos.offset(i, 0, j));
                if (
                        blockState.is(Blocks.FARMLAND) ||
                        blockState.is(BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("conquest", "directional_farmland")).get()) ||
                        blockState.is(BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("conquest", "directional_farmland_layer")).get()) ||
                        blockState.is(BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("conquest", "diagonally_plowed_farmland")).get()) ||
                        blockState.is(BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("conquest", "diagonally_plowed_farmland_layer")).get())
                ) { //Line Difference btwn Mod and  Vanilla is with above condition
                    g = 1.0F;
                    if (blockState.is(Blocks.FARMLAND) && blockState.getValue(FarmlandBlock.MOISTURE) > 0) {
                        g = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    g /= 4.0F;
                }

                f += g;
            }
        }

        BlockPos blockPos2 = pos.north();
        BlockPos blockPos3 = pos.south();
        BlockPos blockPos4 = pos.west();
        BlockPos blockPos5 = pos.east();
        boolean bl = world.getBlockState(blockPos4).is(block) || world.getBlockState(blockPos5).is(block);
        boolean bl2 = world.getBlockState(blockPos2).is(block) || world.getBlockState(blockPos3).is(block);
        if (bl && bl2) {
            f /= 2.0F;
        } else {
            boolean bl3 = world.getBlockState(blockPos4.north()).is(block) || world.getBlockState(blockPos5.north()).is(block) || world.getBlockState(blockPos5.south()).is(block) || world.getBlockState(blockPos4.south()).is(block);
            if (bl3) {
                f /= 2.0F;
            }
        }

        return f;
    }
}
