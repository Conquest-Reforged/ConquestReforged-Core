package com.conquestrefabricated.content.painting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * What a painter's kit has in front of it: something to paint, and something to paint it with.
 *
 * @param base  the material being painted - cobblestone, a plain plaster block
 * @param paint what it is being painted with - a dye, lime, wet plaster
 */
public record PaintingInput(ItemStack base, ItemStack paint) implements RecipeInput {

    public static final int BASE_SLOT = 0;
    public static final int PAINT_SLOT = 1;
    public static final int SLOT_COUNT = 2;

    /** Neither slot filled, for asking a recipe what it makes without handing it anything. */
    public static final PaintingInput EMPTY = new PaintingInput(ItemStack.EMPTY, ItemStack.EMPTY);

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case BASE_SLOT -> this.base;
            case PAINT_SLOT -> this.paint;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return SLOT_COUNT;
    }
}
