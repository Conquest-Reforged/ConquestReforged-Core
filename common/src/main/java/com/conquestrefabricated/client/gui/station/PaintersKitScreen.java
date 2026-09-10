package com.conquestrefabricated.client.gui.station;

import com.conquestrefabricated.content.painting.PaintersKitMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * The painter's kit picker: the shared station screen with a second input slot under the first.
 *
 * <p>The stonecutter background only draws one input slot, so the paint slot is framed with vanilla's
 * loose slot sprite. That takes the strip the variant toggle normally sits in, so the toggle moves
 * across to the free space under the result slot.</p>
 */
public class PaintersKitScreen extends StationScreen<PaintersKitMenu> {

    private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");

    /** The frame is drawn one pixel out from the slot's contents, as the background's own slots are. */
    private static final int PAINT_FRAME_X = 19;
    private static final int PAINT_FRAME_Y = 50;
    private static final int SLOT_FRAME_SIZE = 18;

    private static final int TOGGLE_X = 142;
    private static final int TOGGLE_Y = 50;

    public PaintersKitScreen(PaintersKitMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Drawn before the shared screen so the toggle and the option grid sit on top of it.
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE,
                this.leftPos + PAINT_FRAME_X, this.topPos + PAINT_FRAME_Y,
                SLOT_FRAME_SIZE, SLOT_FRAME_SIZE);
    }

    @Override
    protected int toggleX() {
        return TOGGLE_X;
    }

    @Override
    protected int toggleY() {
        return TOGGLE_Y;
    }
}
