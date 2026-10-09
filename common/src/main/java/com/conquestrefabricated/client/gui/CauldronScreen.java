package com.conquestrefabricated.client.gui;

import com.conquestrefabricated.content.cauldron.CauldronData;
import com.conquestrefabricated.content.cauldron.CauldronMenu;
import com.conquestrefabricated.core.Namespaces;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

/**
 * The screen of a cooking cauldron. Laid out like a furnace's - the vanilla flame and arrow are drawn
 * over a background of our own - with a gauge down the left for what is in the pot.
 */
public class CauldronScreen extends AbstractContainerScreen<CauldronMenu> {

    private static final Identifier BACKGROUND =
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "textures/gui/container/cauldron.png");
    private static final Identifier FLAME = Identifier.withDefaultNamespace("container/furnace/lit_progress");
    private static final Identifier ARROW = Identifier.withDefaultNamespace("container/furnace/burn_progress");

    private static final int FLAME_X = 50;
    private static final int FLAME_Y = 36;
    private static final int ARROW_X = 98;
    private static final int ARROW_Y = 35;

    private static final int GAUGE_X = 9;
    private static final int GAUGE_Y = 18;
    private static final int GAUGE_WIDTH = 12;
    private static final int GAUGE_HEIGHT = 50;

    private static final int WATER = 0xFF3F76E4;
    private static final int BRINE = 0xFF2E8C9C;

    public CauldronScreen(CauldronMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int left = this.leftPos;
        int top = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, left, top, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        if (this.menu.isLit()) {
            int height = (int) Math.ceil(this.menu.litProgress() * 13.0F) + 1;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FLAME, 14, 14, 0, 14 - height,
                    left + FLAME_X, top + FLAME_Y + 14 - height, 14, height);
        }
        int width = (int) Math.ceil(this.menu.burnProgress() * 24.0F);
        if (width > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARROW, 24, 16, 0, 0,
                    left + ARROW_X, top + ARROW_Y, width, 16);
        }

        int filled = GAUGE_HEIGHT * this.menu.fluidLevel() / 3;
        if (filled > 0) {
            graphics.fill(left + GAUGE_X, top + GAUGE_Y + GAUGE_HEIGHT - filled,
                    left + GAUGE_X + GAUGE_WIDTH, top + GAUGE_Y + GAUGE_HEIGHT,
                    this.menu.isBrine() ? BRINE : WATER);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        // An empty slot says what it is for; a full one already has an item tooltip.
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem() && this.menu.getCarried().isEmpty()) {
            int index = this.hoveredSlot.index;
            String key = index < CauldronData.INGREDIENT_SLOTS ? "ingredient"
                    : index == CauldronData.FUEL_SLOT ? "fuel" : index == CauldronData.OUTPUT_SLOT ? "output" : null;
            if (key != null) {
                graphics.setComponentTooltipForNextFrame(this.font, List.of(
                        Component.translatable("container.conquest.cauldron.slot." + key),
                        Component.translatable("container.conquest.cauldron.slot." + key + ".hint")
                                .withStyle(ChatFormatting.GRAY)), mouseX, mouseY);
            }
            return;
        }
        int left = this.leftPos + GAUGE_X;
        int top = this.topPos + GAUGE_Y;
        if (mouseX >= left && mouseX < left + GAUGE_WIDTH && mouseY >= top && mouseY < top + GAUGE_HEIGHT) {
            int level = this.menu.fluidLevel();
            Component text = level <= 0 ? Component.translatable("container.conquest.cauldron.empty")
                    : Component.translatable(this.menu.isBrine() ? "container.conquest.cauldron.sea_water"
                            : "container.conquest.cauldron.water", level);
            graphics.setTooltipForNextFrame(this.font, text, mouseX, mouseY);
        }
    }
}
