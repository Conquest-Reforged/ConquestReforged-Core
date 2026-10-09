package com.conquestrefabricated.mixin;

import com.conquestrefabricated.content.blocks.block.plants.Bush;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererDestroyProgressMixin {

    /** Plants break slowly by hand; the crack overlay looks wrong on their cutout models, so skip it. */
    @Inject(method = "destroyBlockProgress", at = @At("HEAD"), cancellable = true)
    private void conquest$skipPlantCracks(int breakerId, BlockPos pos, int progress, CallbackInfo ci) {
        var level = Minecraft.getInstance().level;
        if (level != null && level.getBlockState(pos).getBlock() instanceof Bush) {
            ci.cancel();
        }
    }
}
