package com.conquestrefabricated.client.gui;

import com.conquestrefabricated.client.gui.render.Render;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class Hotbar {

    private static final Identifier HOTBAR = Identifier.parse("minecraft:textures/gui/widgets.png");

    private final Container inventory;

    public Hotbar(Container inventory) {
        this.inventory = inventory;
    }

    public Container getInventory() {
        return inventory;
    }

    public int getSlotSize() {
        return 20;
    }

    public int getHeight() {
        return getSlotSize() - 1;
    }

    public void renderBackground(Screen screen, GuiGraphicsExtractor drawContext) {
        int u = 0;
        int v = 0;
        int uMax = 182;
        int vMax = 22;
        int left = (screen.width / 2) - (uMax / 2);
        int top = screen.height - vMax;
        Render.drawTexture(HOTBAR, drawContext, left, top, uMax, vMax, u, v, 256, 256);
    }

    public void addTo(AbstractContainer container, int left, int top) {
        int hotbarWidth = (9 * getSlotSize());
        int x = left - (hotbarWidth / 2) + 2;
        for (int i = 0; i < 9; ++i) {
            int dx = i * getSlotSize();
            container.addSlot(new Slot(inventory, i, x + dx, top));
        }
    }
}