package com.conquestrefabricated.client.gui.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Draws an item as a flat-colour silhouette (the 1.12 BlockPalette "outline mode" highlight).
 * Items are rendered to an atlas and blitted later, so the colour is tagged onto the
 * GuiItemRenderState when it is created and picked up again when it is blitted (see GuiRendererMixin).
 */
public final class ItemOutline {

    public static final RenderPipeline PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(Identifier.parse("conquest:pipeline/gui_item_outline"))
            .withFragmentShader(Identifier.parse("conquest:core/item_outline"))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA))
            .build();

    private static int pending = 0;

    private ItemOutline() {
    }

    /** ARGB colour to tag the item state currently being created with, or 0 for none. */
    public static int pending() {
        return pending;
    }

    public static void item(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y, int argb) {
        pending = argb;
        try {
            graphics.item(stack, x, y);
        } finally {
            pending = 0;
        }
    }

    /** Implemented by GuiItemRenderState (via mixin). */
    public interface Tagged {
        int conquest$outlineColor();
    }
}
