package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.content.blocks.BlockVoxelShapes;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@Render(RenderLayer.CUTOUT)
public class BeanPole extends CropBlock {

    private final ItemLike seeds;
    private final ItemLike crop;

    public BeanPole(Props props) {
        super(props.toSettings());
        this.seeds = props.get("seeds", ItemLike.class);
        this.crop = props.get("crop", ItemLike.class);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return BlockVoxelShapes.pillarShape.get(0);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        if (PlacementHelper.isDuringWorldGen(reader)) {
            return super.canSurvive(state, reader, pos);
        }
        return true;
    }

    @Override
    public boolean mayPlaceOn(BlockState state, BlockGetter reader, BlockPos pos) {
        if (PlacementHelper.isDuringWorldGen(reader)) {
            return super.mayPlaceOn(state, reader, pos);
        }
        return true;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seeds;
    }

    //protected IItemProvider getCropsItem() {
    //    return crop;
    //}

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(this);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        if (world.getRawBrightness(pos, 0) >= 9) {
            if (world.getRawBrightness(pos.above(), 0) >= 9) {
                int i = this.getAge(state);

                if (i < this.getMaxAge()) {
                    int height;

                    for (height = 1; world.getBlockState(pos.below(height)).getBlock() == this; ++height) {

                    }

                    if (height < 3) {
                        float f = getGrowthSpeed(this, world, pos);

                        if (rand.nextInt((int)(25.0F / f) + 1) == 0) {
                            if ((world.getBlockState(pos.below()).getBlock() != this) || (world.getBlockState(pos.below()).getValue(this.getAgeProperty()) > i)) {
                                world.setBlock(pos, state.setValue(AGE, i + 1), 2);
                                if (world.getBlockState(pos.above()).getBlock() == BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("conquest", "wooden_pole")).get().value()) {
                                    world.setBlockAndUpdate(pos.above(), this.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
