package com.conquestrefabricated.client.gui.station;

import com.conquestrefabricated.content.loom.LoomMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/**
 * The loom's picker: the shared station screen with a progress bar, since weaving takes time.
 *
 * <p>The bar sits in the gap between the scrollbar and the result slot, which is the only free strip
 * on the stonecutter background wide enough for it. It is drawn with vanilla's brewing sprite, which
 * fills downward towards the slot the cloth is about to appear in.</p>
 */
public class LoomScreen extends StationScreen<LoomMenu> {

    private static final Identifier PROGRESS_SPRITE =
            Identifier.withDefaultNamespace("container/brewing_stand/brew_progress");

    private static final int PROGRESS_X = 132;
    private static final int PROGRESS_Y = 17;
    private static final int PROGRESS_WIDTH = 9;
    private static final int PROGRESS_HEIGHT = 28;

    private static final String WEAVING_KEY = "container.conquest.loom.weaving";

    public LoomScreen(LoomMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        int filled = this.filledHeight();
        if (filled <= 0) {
            return;
        }
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_SPRITE,
                PROGRESS_WIDTH, PROGRESS_HEIGHT, 0, 0,
                this.leftPos + PROGRESS_X, this.topPos + PROGRESS_Y, PROGRESS_WIDTH, filled);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);

        int duration = this.menu.getWeaveDuration();
        if (duration > 0 && this.isOverProgress(mouseX, mouseY)) {
            int percent = Mth.clamp(this.menu.getWeaveProgress() * 100 / duration, 0, 100);
            graphics.setTooltipForNextFrame(this.font,
                    Component.translatable(WEAVING_KEY, percent), mouseX, mouseY);
        }
    }

    /** How much of the bar is filled, or 0 when the loom is not weaving anything. */
    private int filledHeight() {
        int duration = this.menu.getWeaveDuration();
        if (duration <= 0) {
            return 0;
        }
        return Mth.clamp(this.menu.getWeaveProgress() * PROGRESS_HEIGHT / duration, 0, PROGRESS_HEIGHT);
    }

    private boolean isOverProgress(double mouseX, double mouseY) {
        int x = this.leftPos + PROGRESS_X;
        int y = this.topPos + PROGRESS_Y;
        return mouseX >= x && mouseX < x + PROGRESS_WIDTH && mouseY >= y && mouseY < y + PROGRESS_HEIGHT;
    }
}
