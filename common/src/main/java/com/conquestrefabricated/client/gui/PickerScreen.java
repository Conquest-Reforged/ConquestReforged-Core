package com.conquestrefabricated.client.gui;

import com.conquestrefabricated.client.gui.render.Render;
import com.conquestrefabricated.client.utils.CreativeUtils;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.ARGB;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class PickerScreen<T> extends Screen {

    private final ItemStack stack;
    private final T selected;
    private final List<T> options;

    private int index = -1;

    public PickerScreen(String title, ItemStack stack, T selected, List<T> options) {
        super(Component.literal(title));
        this.stack = stack;
        this.options = options;
        this.selected = selected;
    }

    public boolean match(T a, T b) {
        return a.equals(b);
    }

    @Override
    public void init() {
        super.init();
        this.index = indexOf(selected, options);
        Render.hideMouse();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        Minecraft.getInstance().setScreen(null);
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_LEFT) {
            if (--index < 0) {
                index = options.size() - 1;
            }
        }
        if (event.key() == GLFW.GLFW_KEY_RIGHT) {
            if (++index >= options.size()) {
                index = 0;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        int centerX = width / 2;
        int centerY = height / 2;

        int maxWidth = (options.size()) / 2;
        int size = Math.min(maxWidth, getSize());
        for (int i = size, visited = 0; i >= 0; i--) {
            if (i == 0) {
                renderOption(graphics, centerX, centerY, i);
            } else {
                renderOption(graphics, centerX, centerY, +i);
                if (++visited >= (options.size() - 1)) {
                    continue;
                }
                renderOption(graphics, centerX, centerY, -i);
            }
        }
        drawLabel(graphics, centerX, centerY);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontalAmount, double verticalAmount) {
        if (verticalAmount > 0) {
            if (--index < 0) {
                index = options.size() - 1;
            }
        }
        if (verticalAmount < 0) {
            if (++index >= options.size()) {
                index = 0;
            }
        }
        return false;
    }

    @Override
    public void removed() {
        Render.showMouse();

        if (minecraft == null || minecraft.player == null || !minecraft.player.isCreative()) {
            return;
        }

        T option = options.get(index);
        CreativeUtils.replaceItemStack(stack, createItemStack(this.stack, option));
    }

    private void renderOption(GuiGraphicsExtractor graphics, int cx, int cy, int di) {
        int index = this.index + di;

        if (index < 0) {
            index += (options.size());
        }

        if (index >= options.size()) {
            index -= options.size();
        }

        if (index < 0 || index >= options.size()) {
            return;
        }

        float scale = 2F - ((Math.abs(di)) / 4F);
        float count = (getSize() * 2) + 1;
        int size = Math.round((this.width / count) * scale);
        int left = cx + 1 + (di * (size + 1)) - (size / 2);
        int top = cy - (size / 2);

        T option = options.get(index);
        float w = 1F;
        float h = 1F;
        int width = getWidth(option);
        int height = getHeight(option);
        if (width != height) {
            float scale1 = 1F / Math.max(width, height);
            w = width * scale1;
            h = height * scale1;
        }

        int tw = Math.round(size * w);
        int th = Math.round(size * h);
        int tl = left + ((size - tw) / 2);
        int tt = top + ((size - th) / 2);

        float alpha = Math.min(1F, 0.4F + Math.max(0, 1F - (Math.abs(di) / 2F)));
        int color = ARGB.white(alpha);
        render(option, graphics, tl, tt, tw, th, scale, color);
    }

    private void drawLabel(GuiGraphicsExtractor graphics, int centerX, int centerY) {
        if (index < 0 || index >= options.size()) {
            return;
        }

        int height = (this.width / ((getSize() * 2) + 1)) + 10;

        int barWidth = 150;
        int barLeft = (this.width / 2) - (barWidth / 2);
        int barTop = centerY + height + getYOffset();
        int barRight = barLeft + barWidth;
        int barBottom = barTop + 3;

        float position = ((float) index) / (options.size() - 1);
        int posLeft = barLeft + Math.round(position * barWidth) - 1;
        int posRight = posLeft + 2;
        graphics.fillGradient(barLeft, barTop, barRight, barBottom, 0x44000000, 0x44000000);
        graphics.fillGradient(posLeft, barTop, posRight, barBottom, 0x66FFFFFF, 0x66FFFFFF);

        T option = options.get(index);
        String text = getDisplayName(option);
        int width = font.width(text);
        float top = barTop + 15;
        float left = centerX - (width / 2F);
        graphics.text(font, text, (int) left, (int) top, 0xFFFFFFFF);
    }

    private int indexOf(T value, List<T> options) {
        for (int i = 0; i < options.size(); i++) {
            if (match(value, options.get(i))) {
                return i;
            }
        }
        return 0;
    }

    public int getSize() {
        return 5;
    }

    public int getYOffset() {
        return 0;
    }

    public abstract int getWidth(T option);

    public abstract int getHeight(T option);

    public abstract String getDisplayName(T option);

    public abstract void render(T option, GuiGraphicsExtractor graphics, int x, int y, int width, int height, float scale, int color);

    public abstract ItemStack createItemStack(ItemStack original, T value);
}