package com.conquestrefabricated.client.gui.arms;

import com.conquestrefabricated.content.arms.ArmsStationMenu;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * The arms station's recipe picker, drawn on the vanilla stonecutter background.
 *
 * <p>Structurally a port of {@code StonecutterScreen}. The only difference is where the options come
 * from: the stonecutter reads them back out of the client's synced recipe list, while the arms
 * station renders the already-assembled preview stacks the server handed to
 * {@link ArmsStationMenu}.</p>
 */
public class ArmsStationScreen extends AbstractContainerScreen<ArmsStationMenu> {

    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/scroller");
    private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/scroller_disabled");
    private static final Identifier RECIPE_SELECTED_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/recipe_selected");
    private static final Identifier RECIPE_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/recipe_highlighted");
    private static final Identifier RECIPE_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/recipe");
    private static final Identifier BG_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/stonecutter.png");

    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int RECIPES_COLUMNS = 4;
    private static final int RECIPES_ROWS = 3;
    private static final int RECIPES_PER_PAGE = RECIPES_COLUMNS * RECIPES_ROWS;
    private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
    private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
    private static final int SCROLLER_FULL_HEIGHT = 54;
    private static final int RECIPES_X = 52;
    private static final int RECIPES_Y = 14;

    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;

    public ArmsStationScreen(ArmsStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        menu.registerUpdateListener(this::containerChanged);
        this.titleLabelY--;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        int sy = (int) (41.0F * this.scrollOffs);
        Identifier sprite = this.isScrollBarActive() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
        int scrollerXStart = xo + 119;
        int scrollerYStart = yo + 15;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, scrollerXStart, scrollerYStart + sy, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        if (mouseX >= scrollerXStart && mouseY >= scrollerYStart
                && mouseX < scrollerXStart + SCROLLER_WIDTH && mouseY < scrollerYStart + SCROLLER_FULL_HEIGHT) {
            if (this.isScrollBarActive()) {
                graphics.requestCursor(this.scrolling ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
            } else {
                graphics.requestCursor(CursorTypes.NOT_ALLOWED);
            }
        }

        int x = this.leftPos + RECIPES_X;
        int y = this.topPos + RECIPES_Y;
        int endIndex = this.startIndex + RECIPES_PER_PAGE;
        this.extractButtons(graphics, mouseX, mouseY, x, y, endIndex);
        this.extractRecipes(graphics, x, y, endIndex);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        if (!this.displayRecipes) {
            return;
        }

        int edgeLeft = this.leftPos + RECIPES_X;
        int edgeTop = this.topPos + RECIPES_Y;
        int endIndex = this.startIndex + RECIPES_PER_PAGE;
        List<ItemStack> options = this.menu.getOptionIcons();

        for (int index = this.startIndex; index < endIndex && index < options.size(); index++) {
            int posIndex = index - this.startIndex;
            int itemLeft = edgeLeft + posIndex % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
            int itemTop = edgeTop + posIndex / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2;
            if (mouseX >= itemLeft && mouseX < itemLeft + RECIPES_IMAGE_SIZE_WIDTH
                    && mouseY >= itemTop && mouseY < itemTop + RECIPES_IMAGE_SIZE_HEIGHT) {
                graphics.setTooltipForNextFrame(this.font, options.get(index), mouseX, mouseY);
            }
        }
    }

    private void extractButtons(GuiGraphicsExtractor graphics, int xm, int ym, int x, int y, int endIndex) {
        for (int index = this.startIndex; index < endIndex && index < this.menu.getNumberOfVisibleRecipes(); index++) {
            int posIndex = index - this.startIndex;
            int posX = x + posIndex % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
            int posY = y + posIndex / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2;

            Identifier sprite;
            if (index == this.menu.getSelectedRecipeIndex()) {
                sprite = RECIPE_SELECTED_SPRITE;
            } else if (xm >= posX && ym >= posY && xm < posX + RECIPES_IMAGE_SIZE_WIDTH && ym < posY + RECIPES_IMAGE_SIZE_HEIGHT) {
                sprite = RECIPE_HIGHLIGHTED_SPRITE;
            } else {
                sprite = RECIPE_SPRITE;
            }

            int textureY = posY - 1;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, posX, textureY, RECIPES_IMAGE_SIZE_WIDTH, RECIPES_IMAGE_SIZE_HEIGHT);
            if (xm >= posX && ym >= textureY && xm < posX + RECIPES_IMAGE_SIZE_WIDTH && ym < textureY + RECIPES_IMAGE_SIZE_HEIGHT) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }
        }
    }

    private void extractRecipes(GuiGraphicsExtractor graphics, int x, int y, int endIndex) {
        List<ItemStack> options = this.menu.getOptionIcons();
        for (int index = this.startIndex; index < endIndex && index < options.size(); index++) {
            int posIndex = index - this.startIndex;
            int posX = x + posIndex % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
            int posY = y + posIndex / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2;
            graphics.item(options.get(index), posX, posY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.displayRecipes) {
            int xo = this.leftPos + RECIPES_X;
            int yo = this.topPos + RECIPES_Y;
            int endIndex = this.startIndex + RECIPES_PER_PAGE;

            for (int index = this.startIndex; index < endIndex; index++) {
                int posIndex = index - this.startIndex;
                double xx = event.x() - (xo + posIndex % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH);
                double yy = event.y() - (yo + posIndex / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT);
                if (xx >= 0.0 && yy >= 0.0 && xx < RECIPES_IMAGE_SIZE_WIDTH && yy < RECIPES_IMAGE_SIZE_HEIGHT
                        && this.menu.clickMenuButton(this.minecraft.player, index)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, index);
                    return true;
                }
            }

            xo = this.leftPos + 119;
            yo = this.topPos + 9;
            if (event.x() >= xo && event.x() < xo + SCROLLER_WIDTH && event.y() >= yo && event.y() < yo + SCROLLER_FULL_HEIGHT) {
                this.scrolling = true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (this.scrolling && this.isScrollBarActive()) {
            int yscr = this.topPos + RECIPES_Y;
            int yscr2 = yscr + SCROLLER_FULL_HEIGHT;
            this.scrollOffs = ((float) event.y() - yscr - 7.5F) / (yscr2 - yscr - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.startIndex = (int) (this.scrollOffs * this.getOffscreenRows() + 0.5) * RECIPES_COLUMNS;
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.scrolling = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (super.mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        }

        if (this.isScrollBarActive()) {
            int offscreenRows = this.getOffscreenRows();
            float scrolledDelta = (float) scrollY / offscreenRows;
            this.scrollOffs = Mth.clamp(this.scrollOffs - scrolledDelta, 0.0F, 1.0F);
            this.startIndex = (int) (this.scrollOffs * offscreenRows + 0.5) * RECIPES_COLUMNS;
        }

        return true;
    }

    private boolean isScrollBarActive() {
        return this.displayRecipes && this.menu.getNumberOfVisibleRecipes() > RECIPES_PER_PAGE;
    }

    protected int getOffscreenRows() {
        return (this.menu.getNumberOfVisibleRecipes() + RECIPES_COLUMNS - 1) / RECIPES_COLUMNS - RECIPES_ROWS;
    }

    private void containerChanged() {
        this.displayRecipes = this.menu.hasInputItem();
        this.scrollOffs = 0.0F;
        this.startIndex = 0;
    }
}
