package com.conquestrefabricated.client.gui.painting;

import com.conquestrefabricated.api.painting.Painting;
import com.conquestrefabricated.api.painting.art.Art;
import com.conquestrefabricated.client.gui.PickerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;


public class PaintingScreen<T> extends PickerScreen<Art<T>> {

    private final Painting type;

    public PaintingScreen(ItemStack stack, Painting type, Art<T> art) {
        super("Painting Selector", stack, art, art.getAll());
        this.type = type;
    }

    @Override
    public int getWidth(Art<T> option) {
        return option.width();
    }

    @Override
    public int getHeight(Art<T> option) {
        return option.height();
    }

    @Override
    public String getDisplayName(Art<T> option) {
        return option.getName();
    }

    @Override
    public void render(Art<T> option, GuiGraphicsExtractor graphics, int x, int y, int width, int height, float scale, int color) {
        option.getRenderer().render(type, option, graphics, x, y, width, height, color);
    }

    @Override
    public ItemStack createItemStack(ItemStack original, Art<T> value) {
        ItemStack stack = type.createStack(value);
        stack.setCount(original.getCount());
        return stack;
    }
}