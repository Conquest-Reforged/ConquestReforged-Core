package com.conquestrefabricated.core.block.builder;

import com.conquestrefabricated.content.station.TimedStationRecipe;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * What a block declared for a workstation needs to have its recipe generated: what goes in, how much
 * comes out, and how long it takes.
 *
 * <p>The same shape serves every workstation - {@link Props#woven} at a loom, {@link Props#thrown} at
 * a pottery wheel - because only the recipe type differs between them.</p>
 *
 * @see RecipeIngredient
 * @see ToolRecipeSpec
 */
public record TimedRecipeSpec(RecipeIngredient ingredient, int count, int time) {

    public TimedRecipeSpec {
        if (count < 1) {
            throw new IllegalArgumentException("Workstation recipe count must be at least 1, got " + count);
        }
        if (count > Item.ABSOLUTE_MAX_STACK_SIZE) {
            // Nearly always a time written into the count: the two-argument form is (ingredient,
            // count), and a time large enough to be worth writing is larger than any stack.
            throw new IllegalArgumentException("Workstation recipe count " + count + " is above the"
                    + " largest stack of " + Item.ABSOLUTE_MAX_STACK_SIZE + ". If that was meant as a"
                    + " time in ticks, it is the third argument - woven(ingredient, count, time) - and"
                    + " the two-argument form sets the count.");
        }
        if (time < 0) {
            throw new IllegalArgumentException("Workstation recipe time cannot be negative, got " + time);
        }
    }

    public static TimedRecipeSpec of(RecipeIngredient ingredient, int count, int time) {
        return new TimedRecipeSpec(ingredient, count, time);
    }

    /**
     * @param items the generator's item lookup, needed to resolve a tag
     */
    public Ingredient toIngredient(HolderGetter<Item> items) {
        return this.ingredient.toIngredient(items);
    }

    /** The time to write, falling back to {@link TimedStationRecipe#DEFAULT_TIME} when none was given. */
    public int timeOrDefault() {
        return this.time > 0 ? this.time : TimedStationRecipe.DEFAULT_TIME;
    }
}
