package com.conquestrefabricated.mixin;

import com.conquestrefabricated.content.cauldron.CauldronBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Rain does not top up a cauldron of brine: it would make it fresh without telling anyone. */
@Mixin(LayeredCauldronBlock.class)
public abstract class LayeredCauldronBlockMixin {

    @Inject(method = "handlePrecipitation", at = @At("HEAD"), cancellable = true)
    private void conquest$keepBrine(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation,
                                    CallbackInfo callback) {
        if (CauldronBlocks.isBrine(level, pos)) {
            callback.cancel();
        }
    }
}
