package com.conquestrefabricated.client.gui;

import com.conquestrefabricated.client.gui.palette.component.Style;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;


public abstract class CustomContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private Identifier SLOT_BACKGROUND = Identifier.parse("conquest:textures/gui/picker/slot.png");

    private Slot clickedSlot;
    private boolean isRightMouseClick;
    private boolean isOverSlot = false;

    public CustomContainerScreen(T screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    protected void onSlotClick(@Nullable Slot slot, int index, int button, ContainerInput containerInput) {
        clickedSlot = slot;
        isRightMouseClick = button == 2; // ?
    }

    @Override
    protected void slotClicked(@Nullable Slot slot, int index, int button, ContainerInput containerInput) {
        super.slotClicked(slot, index, button, containerInput);
        onSlotClick(slot, index, button, containerInput);
    }

    protected void setupRender(Matrix3x2fStack pose) {
        isOverSlot = false;
        pose.pushMatrix();
        pose.translate(leftPos, topPos);
    }

    protected void tearDownRender(Matrix3x2fStack pose) {
        pose.popMatrix();
    }

    public void renderDraggedItem(GuiGraphicsExtractor drawContext, int mx, int my, float depth, Style style) {
        ItemStack held = getMenu().getCarried();
        if (!held.isEmpty()) {
            Matrix3x2fStack pose = drawContext.pose();
            pose.pushMatrix();
            pose.translate(mx, my);

            drawContext.item(held, -8, -8);
            drawContext.itemDecorations(font, held, -8, -8);
            pose.popMatrix();
        }
    }

    public void renderSlotBackGround(GuiGraphicsExtractor drawContext, Slot slot, Style style, float depth, float scale) {
        int x = slot.x + 8;
        int y = slot.y + 8;

        Matrix3x2fStack pose = drawContext.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(scale, scale);

        if (style != null && style.background != null) {
            drawContext.blit(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND, -8, -6, 0, 0, 16, 16, 72, 72, 72, 72, 0xFFFFFFFF);
        }

        pose.popMatrix();
    }

    public void renderSlot(GuiGraphicsExtractor drawContext, Slot slot, int mx, int my, float depth, float scale) {
        renderSlot(drawContext, slot, null, mx, my, depth, scale);
    }

    public void renderSlot(GuiGraphicsExtractor drawContext, Slot slot, Style style, int mx, int my, float depth, float scale) {
        int x = slot.x + 8;
        int y = slot.y + 8;
        ItemStack itemstack = slot.getItem();

        Matrix3x2fStack pose = drawContext.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(scale, scale);

        drawContext.item(itemstack, -8, -8);
        drawContext.itemDecorations(font, itemstack, -8, -8, null);

        pose.popMatrix();
    }

    public static boolean isMouseOver(Slot slot, int mx, int my, int size, float scale) {
        float delta = size * scale;
        return mx >= slot.x - delta && mx <= slot.x + delta && my >= slot.y - delta && my <= slot.y + delta;
    }

}