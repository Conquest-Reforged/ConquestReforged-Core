package com.conquestrefabricated.core.client.color;

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

    private static int defaultWaterColor() {
        return -1;
    }
}
