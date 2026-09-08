package com.conquestrefabricated.core.block.builder;

import com.conquestrefabricated.content.loom.WeavingRecipe;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * What a block declared through {@link Props#woven} needs to have a weaving recipe generated for it:
 * what goes onto the loom, how much comes off, and how long it takes.
 *
 * @see RecipeIngredient
 * @see ToolRecipeSpec
 */
public record WeavingRecipeSpec(RecipeIngredient ingredient, int count, int time) {

    public WeavingRecipeSpec {
        if (count < 1) {
            throw new IllegalArgumentException("Weaving recipe count must be at least 1, got " + count);
        }
        if (time < 0) {
            throw new IllegalArgumentException("Weaving recipe time cannot be negative, got " + time);
        }
    }

    public static WeavingRecipeSpec of(RecipeIngredient ingredient, int count, int time) {
        return new WeavingRecipeSpec(ingredient, count, time);
    }

    /**
     * @param items the generator's item lookup, needed to resolve a tag
     */
    public Ingredient toIngredient(HolderGetter<Item> items) {
        return this.ingredient.toIngredient(items);
    }

    /** The time to write, falling back to {@link WeavingRecipe#DEFAULT_TIME} when none was given. */
    public int timeOrDefault() {
        return this.time > 0 ? this.time : WeavingRecipe.DEFAULT_TIME;
    }
}
