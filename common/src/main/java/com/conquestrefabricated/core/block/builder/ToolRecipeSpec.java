package com.conquestrefabricated.core.block.builder;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * What a block declared through {@link Props#craftedWith} needs to have a crafting tool recipe
 * generated for it: which set of tools makes it, what goes in, and how much comes out.
 *
 * @see RecipeIngredient
 */
public record ToolRecipeSpec(Identifier tool, RecipeIngredient ingredient, int count) {

    public ToolRecipeSpec {
        if (count < 1) {
            throw new IllegalArgumentException("Tool recipe count must be at least 1, got " + count);
        }
    }

    public static ToolRecipeSpec of(Identifier tool, RecipeIngredient ingredient, int count) {
        return new ToolRecipeSpec(tool, ingredient, count);
    }

    /**
     * @param items the generator's item lookup, needed to resolve a tag
     */
    public Ingredient toIngredient(HolderGetter<Item> items) {
        return this.ingredient.toIngredient(items);
    }
}
