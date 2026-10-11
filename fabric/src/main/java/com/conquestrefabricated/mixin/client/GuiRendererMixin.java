package com.conquestrefabricated.mixin.client;

import com.conquestrefabricated.client.gui.render.ItemOutline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.gui.render.GuiItemAtlas;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {

    @Shadow
    @org.spongepowered.asm.mixin.Final
    private GuiRenderState renderState;

    /** Same blit as vanilla, but through the silhouette pipeline for items tagged by {@link ItemOutline}. */
    @Inject(method = "submitBlitFromItemAtlas", at = @At("HEAD"), cancellable = true)
    private void conquest$blitOutline(GuiItemRenderState state, GuiItemAtlas.SlotView slot, CallbackInfo ci) {
        int color = ((ItemOutline.Tagged) (Object) state).conquest$outlineColor();
        if (color == 0) {
            return;
        }
        renderState.addBlitToCurrentLayer(new BlitRenderState(
                ItemOutline.PIPELINE,
                TextureSetup.singleTexture(slot.textureView(), RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)),
                state.pose(),
                state.x(), state.y(), state.x() + 16, state.y() + 16,
                slot.u0(), slot.u1(), slot.v0(), slot.v1(),
                color,
                state.scissorArea(),
                null));
        ci.cancel();
    }
}
