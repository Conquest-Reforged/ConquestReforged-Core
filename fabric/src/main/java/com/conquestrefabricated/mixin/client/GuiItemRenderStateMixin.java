package com.conquestrefabricated.mixin.client;

import com.conquestrefabricated.client.gui.render.ItemOutline;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiItemRenderState.class)
public class GuiItemRenderStateMixin implements ItemOutline.Tagged {

    @Unique
    private int conquest$outlineColor;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void conquest$tagOutline(CallbackInfo ci) {
        this.conquest$outlineColor = ItemOutline.pending();
    }

    @Override
    public int conquest$outlineColor() {
        return conquest$outlineColor;
    }
}
