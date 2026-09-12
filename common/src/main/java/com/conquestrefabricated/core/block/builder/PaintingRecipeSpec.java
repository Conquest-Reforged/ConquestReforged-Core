package com.conquestrefabricated.core.block.builder;

import net.minecraft.world.item.Item;

/**
 * What a block declared through {@link Props#painted} needs to have a painting recipe generated for
 * it: what is being painted, what it is painted with, and how much comes off.
 *
 * @see RecipeIngredient
 * @see ToolRecipeSpec
 */
public record PaintingRecipeSpec(RecipeIngredient base, RecipeIngredient paint, int count) {

    public PaintingRecipeSpec {
        if (count < 1) {
            throw new IllegalArgumentException("Painting recipe count must be at least 1, got " + count);
        }
        if (count > Item.ABSOLUTE_MAX_STACK_SIZE) {
            throw new IllegalArgumentException("Painting recipe count " + count + " is above the largest"
                    + " stack of " + Item.ABSOLUTE_MAX_STACK_SIZE + ".");
        }
    }

    public static PaintingRecipeSpec of(RecipeIngredient base, RecipeIngredient paint, int count) {
        return new PaintingRecipeSpec(base, paint, count);
    }
}
