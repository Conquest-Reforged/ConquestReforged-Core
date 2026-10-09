package com.conquestrefabricated.client.gui;

import com.conquestrefabricated.content.curing.CuringVesselBlockEntity;
import com.conquestrefabricated.content.curing.PackingBoxMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

/**
 * The screen of a packing crate: a chest, with a bar along the bottom of each slot that is being cured. A
 * slot that cannot be cured gets a red bar, and hovering it says why.
 */
public class PackingBoxScreen extends AbstractContainerScreen<PackingBoxMenu> {

    private static final Identifier BACKGROUND = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");

    private static final int ROWS = 3;
    private static final int BAR = 0xFF55CC55;
    private static final int BLOCKED = 0xFFC04040;
    private static final int TRACK = 0xFF202020;

    public PackingBoxScreen(PackingBoxMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 114 + ROWS * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, 0.0F, 0.0F,
                this.imageWidth, ROWS * 18 + 17, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos + ROWS * 18 + 17, 0.0F, 126.0F,
                this.imageWidth, 96, 256, 256);
    }

    @Override
    protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
        super.extractSlot(graphics, slot, mouseX, mouseY);
        int state = slot.index < CuringVesselBlockEntity.SLOTS && slot.hasItem() ? this.menu.state(slot.index) : 0;
        if (state == CuringVesselBlockEntity.STATE_NONE) {
            return;
        }
        int x = slot.x;
        int y = slot.y + 13;
        graphics.fill(x, y, x + 16, y + 3, TRACK);
        if (state > 100) {
            graphics.fill(x, y, x + 16, y + 3, BLOCKED);
        } else {
            graphics.fill(x, y, x + Math.max(1, state * 16 / 100), y + 2, BAR);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Slot slot = this.hoveredSlot;
        if (slot != null && slot.hasItem() && this.menu.getCarried().isEmpty()
                && slot.index < CuringVesselBlockEntity.SLOTS) {
            int state = this.menu.state(slot.index);
            if (state != CuringVesselBlockEntity.STATE_NONE) {
                List<Component> lines = new ArrayList<>(this.getTooltipFromContainerItem(slot.getItem()));
                lines.add(state > 100
                        ? Component.translatable("container.conquest.packing_box.state." + state).withStyle(ChatFormatting.RED)
                        : Component.translatable("container.conquest.packing_box.curing", state).withStyle(ChatFormatting.GREEN));
                graphics.setComponentTooltipForNextFrame(this.font, lines, mouseX, mouseY);
                return;
            }
        }
        super.extractTooltip(graphics, mouseX, mouseY);
    }
}
