package com.conquestrefabricated.client.gui.palette;

import com.conquestrefabricated.client.ModBinds;
import com.conquestrefabricated.client.gui.CustomCreativeScreen;
import com.conquestrefabricated.client.gui.palette.component.PaletteSettings;
import com.conquestrefabricated.client.gui.palette.component.PaletteSlot;
import com.conquestrefabricated.client.gui.palette.component.Style;
import com.conquestrefabricated.client.gui.render.ItemOutline;
import com.conquestrefabricated.client.gui.render.Render;
import com.conquestrefabricated.client.tutorial.Tutorials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class PaletteScreen extends CustomCreativeScreen<PaletteContainer> {

    private static final Identifier WHEEL = Identifier.parse("conquest:textures/gui/picker/wheel.png");

    private static final int EXIT = 256;
    private static final int SIZE = (PaletteContainer.RADIUS + 44) * 2;

    private final Screen previous;
    private final PaletteSettings settings = new PaletteSettings();

    private Slot hovered = null;

    public PaletteScreen(Player player, Inventory inventory, PaletteContainer container) {
        this(null, player, inventory, container);
    }

    public PaletteScreen(Screen previous, Player player, Inventory inventory, PaletteContainer container) {
        super(container, inventory, Component.literal("Palette Screen"));
        this.previous = previous;
        player.containerMenu = container;
    }

    @Override
    protected void init() {
        super.init();
        settings.init(width, height);
        addRenderableOnly(settings);
        updateLayout(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        Tutorials.openPalette = true;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        updateLayout(width, height);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        extractBg(graphics, partialTicks, mouseX, mouseY);

        getMenu().updateStyle(settings);

        setupRender(graphics.pose());
        {
            final int mx = mouseX - leftPos;
            final int my = mouseY - topPos;
            // visitRadius yields the slot nearest the cursor first; draw it last so it sits on top
            final List<Map.Entry<PaletteSlot, Float>> radial = new ArrayList<>();
            getMenu().visitRadius(mx, my, (slot, depth) -> radial.add(Map.entry(slot, depth)));
            Collections.reverse(radial);
            for (Map.Entry<PaletteSlot, Float> e : radial) {
                PaletteSlot slot = e.getKey();
                renderSlotBackGround(graphics, slot, slot.getStyle(), e.getValue(), slot.getScale(mx, my, settings));
            }
            for (Map.Entry<PaletteSlot, Float> e : radial) {
                PaletteSlot slot = e.getKey();
                float depth = e.getValue();
                float scale = slot.getScale(mx, my, settings);
                if (depth == 1F) {
                    renderSlotOutline(graphics, slot, slot.getStyle(), scale);
                }
                renderSlot(graphics, slot, slot.getStyle(), mx, my, depth, scale);
            }
            getMenu().visitCenter(slot -> {
                float scale = slot.getScale(mx, my, settings);
                renderSlotBackGround(graphics, slot, slot.getStyle(), 1F, scale);
                if (slot.isMouseOver(mx, my)) {
                    renderSlotOutline(graphics, slot, slot.getStyle(), scale);
                }
                renderSlot(graphics, slot, slot.getStyle(), mx, my, 1F, scale);
            });
            getMenu().visitHotbar(slot -> renderSlot(graphics, slot, mx, my, 1F, 1F));
            renderDraggedItem(graphics, mx, my, 1F, getMenu().getDraggedStyle());
        }
        tearDownRender(graphics.pose());
    }

    /** Highlight: the item as a flat-colour silhouette, slightly larger, drawn behind the item itself. */
    private void renderSlotOutline(GuiGraphicsExtractor graphics, PaletteSlot slot, Style style, float scale) {
        if (!slot.hasItem()) {
            return;
        }
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(slot.x + 8, slot.y + 8);
        pose.scale(scale * style.highlightScale, scale * style.highlightScale);
        ItemOutline.item(graphics, slot.getItem(), -8, -8, 0xFF000000 | style.highlightColor);
        pose.popMatrix();
    }

    public void extractBg(GuiGraphicsExtractor drawContext, float partialTicks, int mouseX, int mouseY) {
        Render.drawTexture(WHEEL, drawContext, leftPos, topPos, SIZE, SIZE, 0, 0);
        getMenu().getHotbar().renderBackground(this, drawContext);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (minecraft == null) {
            return;
        }

        ItemStack display = getMenu().getCarried();
        if (display.isEmpty()) {
            Slot slot = getMenu().getClosestSlot(mouseX - this.leftPos, mouseY - this.topPos, true);
            hovered = slot;
            if (slot == null) {
                return;
            }
            display = slot.getItem();
        }

        if (display.getItem() == Items.AIR) {
            return;
        }

        int top = (height - 32);
        int left = width / 2;
        int color = 0xFFFFFFFF;

        String text = display.getHoverName().getString();
        graphics.centeredText(minecraft.font, text, left, top, color);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        char c = (char) event.codepoint();
        if (c >= '1' && c <= '9' && hovered != null && hovered.hasItem()) {
            menu.getHotbar().getInventory().setItem(c - '1', hovered.getItem());
            super.sendChanges();
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
        // Intentionally empty - we handle slot rendering ourselves
    }

    @Override
    protected boolean isContainerSlot(Slot slot) {
        return slot.container == getMenu().getPaletteInventory();
    }

    @Override
    public void onClose() {
        settings.onClose();
        if (previous != null) {
            previous.init(width, height);
        }
        Minecraft.getInstance().setScreen(previous);
    }

    private void updateLayout(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.leftPos = (width - SIZE) / 2;
        this.topPos = (height - SIZE) / 2;
        getMenu().init(this);
    }

    public static boolean closesGui(KeyEvent key) {
        return key.key() == EXIT || key.key() == ModBinds.getPaletteBind().getDefaultKey().getValue();
    }

    public static boolean closesGui(int key) {
        return key == EXIT || key == ModBinds.getPaletteBind().getDefaultKey().getValue();
    }
}