package com.conquestrefabricated.core.client.color;

import com.conquestrefabricated.content.leatherworking.SoakingBarrelBlockEntity;
import com.conquestrefabricated.content.leatherworking.SoakingRecipe;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.DryFoliageColor;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockColors {

    public static final BlockTintSource GRASS = new BlockTintSource() {
        @Override
        public int color(BlockState state) {
            return defaultGrassColor();
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            return BiomeColors.getAverageGrassColor(level, pos);
        }
    };

    public static final BlockTintSource FOLIAGE = new BlockTintSource() {
        @Override
        public int color(BlockState state) {
            return defaultFoliageColor();
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            return BiomeColors.getAverageFoliageColor(level, pos);
        }
    };

    public static final BlockTintSource BIRCH = new BlockTintSource() {
        @Override
        public int color(BlockState state) {
            return defaultBirchColor();
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            return defaultBirchColor();
        }
    };

    public static final BlockTintSource DRY_FOLIAGE = new BlockTintSource() {
        @Override
        public int color(BlockState state) {
            return defaultDryFoliageColor();
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            return BiomeColors.getAverageDryFoliageColor(level, pos);
        }
    };

    public static final BlockTintSource WATER = new BlockTintSource() {
        @Override
        public int color(BlockState state) {
            return defaultWaterColor();
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            return BiomeColors.getAverageWaterColor(level, pos);
        }
    };

    /**
     * The water in a soaking barrel: whatever colour the barrel says it is, and the biome's own water
     * colour for plain water. The texture is a light grey that a tint can only darken, so plain water has
     * to be tinted too or nothing dissolved in it could ever read as lighter.
     */
    public static final BlockTintSource SOAKING_WATER = new BlockTintSource() {
        @Override
        public int color(BlockState state) {
            return defaultWaterColor();
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            if (level.getBlockEntity(pos) instanceof SoakingBarrelBlockEntity barrel
                    && barrel.waterColor() != SoakingRecipe.NO_COLOR) {
                return 0xFF000000 | barrel.waterColor();
            }
            return BiomeColors.getAverageWaterColor(level, pos);
        }
    };

    public static ItemTintSource toItemColor(BlockTintSource blockTint) {
        return new ItemTintSource() {
            @Override
            public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
                BlockState state = ((BlockItem) itemStack.getItem()).getBlock().defaultBlockState();
                return blockTint.color(state);
            }

            @Override
            public MapCodec<? extends ItemTintSource> type() {
                throw new UnsupportedOperationException("toItemColor()-wrapped sources are code-only and not meant to be serialized");
            }
        };
    }

    private static int defaultGrassColor() {
        return GrassColor.get(0.5, 1.0);
    }

    private static int defaultFoliageColor() {
        return FoliageColor.FOLIAGE_DEFAULT;
    }

    private static int defaultBirchColor() {
        return FoliageColor.FOLIAGE_BIRCH;
    }

    private static int defaultDryFoliageColor() {
        return DryFoliageColor.FOLIAGE_DRY_DEFAULT;
    }

    private static int defaultWaterColor() {
        return -1;
    }
}
