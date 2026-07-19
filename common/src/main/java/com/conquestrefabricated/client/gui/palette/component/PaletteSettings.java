package com.conquestrefabricated.client.gui.palette.component;

import com.conquestrefabricated.client.gui.render.Curve;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@net.fabricmc.api.Environment(EnvType.CLIENT)
public class PaletteSettings extends Screen {

    private static final boolean test = false;

    public transient Curve zoomCurve = Curve.SQUARE;

    public float zoomScale = 1.1f;
    public float zoomSpread = 1f;
    public float highlightScale = 1.1f;

    private final Panel left = Panel.left(true);
    private final Panel right = Panel.right(true);

    public PaletteSettings() {
        super(Component.literal("Settings"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mx, int my, float ticks) {
        left.tick();
        right.tick();
    }

    private void add(Panel panel, AbstractWidget widget) {
        super.addRenderableWidget(widget);
        panel.add(widget);
    }
}