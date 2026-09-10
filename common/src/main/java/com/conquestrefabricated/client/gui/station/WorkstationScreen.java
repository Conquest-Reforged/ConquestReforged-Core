package com.conquestrefabricated.client.gui.station;

import com.conquestrefabricated.content.station.WorkstationMenu;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/**
 * The picker for a workstation you leave running: the shared station screen with a progress bar and a
 * control to start a craft, since the work takes time and picking a new job should not throw away the
 * one already running.
 *
 * <p>Both sit on the result side. The strip between the scrollbar and the result slot is narrow -
 * eleven pixels on the stonecutter background - so the bar is pushed hard against the scrollbar to
 * leave what clearance there is next to the slot, and the confirm control drops below the slot where
 * there is room for a target worth clicking.</p>
 *
 * @param <T> the workstation menu this screen is showing
 */
public abstract class WorkstationScreen<T extends WorkstationMenu<?>> extends StationScreen<T> {

    private static final Identifier PROGRESS_SPRITE =
            Identifier.withDefaultNamespace("container/brewing_stand/brew_progress");

    private static final int PROGRESS_X = 121;
    private static final int PROGRESS_Y = 18;
    private static final int PROGRESS_WIDTH = 9;
    private static final int PROGRESS_HEIGHT = 28;

    /** The empty bar, so the control is there to be read before anything is being made. */
    private static final int PROGRESS_TRACK_COLOR = 0xFF4A4A4A;

    private static final int CONFIRM_X = 122;
    private static final int CONFIRM_Y = 55;
    private static final int CONFIRM_WIDTH = 16;
    private static final int CONFIRM_HEIGHT = 18;

    protected WorkstationScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    /**
     * Lang key prefix for this station's controls, e.g. {@code container.conquest.loom}. The screen
     * appends {@code .confirm}, {@code .stop}, {@code .no_selection} and {@code .progress}, so each
     * station can name its own work - weaving, shaping - rather than sharing one flat wording.
     */
    protected abstract String langPrefix();

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        this.extractProgress(graphics);
        this.extractConfirm(graphics, mouseX, mouseY);
    }

    /**
     * The bar is always drawn, dimmed, and the filled part painted over it - a control that appears
     * out of nowhere the moment a craft starts reads as a glitch rather than as progress.
     */
    private void extractProgress(GuiGraphicsExtractor graphics) {
        int x = this.leftPos + PROGRESS_X;
        int y = this.topPos + PROGRESS_Y;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_SPRITE,
                PROGRESS_WIDTH, PROGRESS_HEIGHT, 0, 0, x, y, PROGRESS_WIDTH, PROGRESS_HEIGHT,
                PROGRESS_TRACK_COLOR);

        int filled = this.filledHeight();
        if (filled > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_SPRITE,
                    PROGRESS_WIDTH, PROGRESS_HEIGHT, 0, 0, x, y, PROGRESS_WIDTH, filled);
        }
    }

    private void extractConfirm(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = this.leftPos + CONFIRM_X;
        int y = this.topPos + CONFIRM_Y;
        boolean enabled = this.menu.canConfirm();
        boolean hovered = enabled && this.isOverConfirm(mouseX, mouseY);
        boolean running = this.menu.isSelectionActive();

        Identifier sprite;
        if (running) {
            sprite = RECIPE_SELECTED_SPRITE;
        } else if (hovered) {
            sprite = RECIPE_HIGHLIGHTED_SPRITE;
        } else {
            sprite = RECIPE_SPRITE;
        }
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, CONFIRM_WIDTH, CONFIRM_HEIGHT);
        graphics.centeredText(this.font, running ? "✖" : "✔", x + CONFIRM_WIDTH / 2,
                y + (CONFIRM_HEIGHT - this.font.lineHeight) / 2, enabled ? 0xFFDDDDDD : 0xFF666666);

        if (hovered) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        if (this.isOverConfirm(mouseX, mouseY)) {
            String suffix;
            if (!this.menu.canConfirm()) {
                suffix = ".no_selection";
            } else if (this.menu.isSelectionActive()) {
                suffix = ".stop";
            } else {
                suffix = ".confirm";
            }
            graphics.setTooltipForNextFrame(this.font,
                    Component.translatable(this.langPrefix() + suffix), mouseX, mouseY);
            return;
        }

        int duration = this.menu.getCraftDuration();
        if (duration > 0 && this.isOverProgress(mouseX, mouseY)) {
            int percent = Mth.clamp(this.menu.getCraftProgress() * 100 / duration, 0, 100);
            graphics.setTooltipForNextFrame(this.font,
                    Component.translatable(this.langPrefix() + ".progress", percent), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.isOverConfirm(event.x(), event.y()) && this.menu.canConfirm()) {
            this.menu.clickMenuButton(this.minecraft.player, WorkstationMenu.CONFIRM_BUTTON);
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, WorkstationMenu.CONFIRM_BUTTON);
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    /** How much of the bar is filled, or 0 when nothing is being made. */
    private int filledHeight() {
        int duration = this.menu.getCraftDuration();
        if (duration <= 0) {
            return 0;
        }
        return Mth.clamp(this.menu.getCraftProgress() * PROGRESS_HEIGHT / duration, 0, PROGRESS_HEIGHT);
    }

    private boolean isOverProgress(double mouseX, double mouseY) {
        return this.isOver(mouseX, mouseY, PROGRESS_X, PROGRESS_Y, PROGRESS_WIDTH, PROGRESS_HEIGHT);
    }

    private boolean isOverConfirm(double mouseX, double mouseY) {
        return this.isOver(mouseX, mouseY, CONFIRM_X, CONFIRM_Y, CONFIRM_WIDTH, CONFIRM_HEIGHT);
    }

    private boolean isOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        int left = this.leftPos + x;
        int top = this.topPos + y;
        return mouseX >= left && mouseX < left + width && mouseY >= top && mouseY < top + height;
    }
}
