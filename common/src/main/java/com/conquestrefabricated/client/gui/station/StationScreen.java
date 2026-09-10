package com.conquestrefabricated.client.gui.station;

import com.conquestrefabricated.content.station.StationMenu;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.ChatFormatting;
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
 * The recipe picker shared by Conquest's crafting stations, drawn on the vanilla stonecutter
 * background.
 *
 * <p>Structurally a port of {@code StonecutterScreen}. The only difference is where the options come
 * from: the stonecutter reads them back out of the client's synced recipe list, while a station
 * renders the already-assembled preview stacks the server handed to {@link StationMenu}.</p>
 *
 * @param <T> the station menu this screen is showing
 */
public class StationScreen<T extends StationMenu<?, ?>> extends AbstractContainerScreen<T> {

    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/scroller");
    private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/scroller_disabled");
    protected static final Identifier RECIPE_SELECTED_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/recipe_selected");
    protected static final Identifier RECIPE_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/recipe_highlighted");
    protected static final Identifier RECIPE_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/recipe");
    /** Vanilla's "this slot is switched off" artwork, for an option the slots cannot yet pay for. */
    protected static final Identifier RECIPE_UNAVAILABLE_SPRITE =
            Identifier.withDefaultNamespace("container/crafter/disabled_slot");
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

    /**
     * The variant toggle, in the blank strip between the input slot and the inventory label. Drawn
     * with the picker's own button sprites so it reads as part of the same control, rather than
     * needing artwork of its own, and sized a little over the sprite's own 16x18 to give it a
     * target worth aiming at.
     */
    private static final int TOGGLE_X = 18;
    private static final int TOGGLE_Y = 50;
    private static final int TOGGLE_WIDTH = 20;
    private static final int TOGGLE_HEIGHT = 20;

    private static final String NEEDS_KEY = "container.conquest.station.needs";

    private static final String SHOW_VARIANTS_KEY = "container.conquest.station.show_variants";
    private static final String HIDE_VARIANTS_KEY = "container.conquest.station.hide_variants";

    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;

    public StationScreen(T menu, Inventory inventory, Component title) {
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
        this.extractVariantToggle(graphics, mouseX, mouseY);
    }

    private void extractVariantToggle(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
        if (!this.menu.supportsVariants()) {
            return;
        }

        int x = this.leftPos + toggleX();
        int y = this.topPos + this.toggleY();
        boolean hovered = this.isOverToggle(mouseX, mouseY);
        boolean on = this.menu.showingVariants();

        Identifier sprite;
        if (on) {
            sprite = RECIPE_SELECTED_SPRITE;
        } else if (hovered) {
            sprite = RECIPE_HIGHLIGHTED_SPRITE;
        } else {
            sprite = RECIPE_SPRITE;
        }
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, this.toggleWidth(), this.toggleHeight());
        graphics.centeredText(this.font, on ? "-" : "+", x + this.toggleWidth() / 2,
                y + (this.toggleHeight() - this.font.lineHeight) / 2, 0xFFDDDDDD);

        if (hovered) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }

    /** Where the variant toggle sits. Overridable for stations whose slots need that corner. */
    protected int toggleX() {
        return TOGGLE_X;
    }

    protected int toggleY() {
        return TOGGLE_Y;
    }

    protected int toggleWidth() {
        return TOGGLE_WIDTH;
    }

    protected int toggleHeight() {
        return TOGGLE_HEIGHT;
    }

    private boolean isOverToggle(final double mouseX, final double mouseY) {
        if (!this.menu.supportsVariants()) {
            return false;
        }
        int x = this.leftPos + toggleX();
        int y = this.topPos + this.toggleY();
        return mouseX >= x && mouseX < x + this.toggleWidth() && mouseY >= y && mouseY < y + this.toggleHeight();
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        if (this.isOverToggle(mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(this.font, Component.translatable(
                    this.menu.showingVariants() ? HIDE_VARIANTS_KEY : SHOW_VARIANTS_KEY), mouseX, mouseY);
        }

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
                ItemStack requirement = this.menu.getOptionRequirement(index);
                if (requirement.isEmpty()) {
                    graphics.setTooltipForNextFrame(this.font, options.get(index), mouseX, mouseY);
                } else {
                    graphics.setComponentTooltipForNextFrame(this.font, List.of(
                            options.get(index).getHoverName(),
                            Component.translatable(NEEDS_KEY, requirement.getHoverName())
                                    .withStyle(ChatFormatting.GRAY)), mouseX, mouseY);
                }
            }
        }
    }

    private void extractButtons(GuiGraphicsExtractor graphics, int xm, int ym, int x, int y, int endIndex) {
        for (int index = this.startIndex; index < endIndex && index < this.menu.getNumberOfVisibleRecipes(); index++) {
            int posIndex = index - this.startIndex;
            int posX = x + posIndex % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
            int posY = y + posIndex / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2;

            Identifier sprite;
            if (!this.menu.isOptionReady(index)) {
                sprite = RECIPE_UNAVAILABLE_SPRITE;
            } else if (index == this.menu.getSelectedRecipeIndex()) {
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
        if (this.isOverToggle(event.x(), event.y())) {
            this.menu.clickMenuButton(this.minecraft.player, StationMenu.TOGGLE_VARIANTS_BUTTON);
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, StationMenu.TOGGLE_VARIANTS_BUTTON);
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
            return true;
        }

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
