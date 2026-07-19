package com.conquestrefabricated.api.painting.art;

import com.conquestrefabricated.api.painting.Painting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public enum ArtRenderer {
    MOD {
        @Override
        public void render(Painting painting, Art<?> art, GuiGraphicsExtractor drawContext, int x, int y, int w, int h, int color) {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, painting.getRegistryName(), x, y, art.u(), art.v(), w, h, art.width(), art.height(), art.textureWidth(), art.textureHeight(), color);
        }
    },
    VANILLA {
        @Override
        public void render(Painting painting, Art<?> art, GuiGraphicsExtractor drawContext, int x, int y, int w, int h, int color) {
            String artName = art.getName().replace("minecraft:", "");
            Identifier texture = Identifier.parse("minecraft:textures/painting/" + artName + ".png");
            drawContext.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, art.u(), art.v(), w, h, art.width(), art.height(), art.textureWidth(), art.textureHeight(), color);
        }
    },
    ;

    public abstract void render(Painting painting, Art<?> art, GuiGraphicsExtractor drawContext, int x, int y, int w, int h, int color);
}
