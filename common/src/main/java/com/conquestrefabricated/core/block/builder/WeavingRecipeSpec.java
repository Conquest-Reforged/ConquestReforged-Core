package com.conquestrefabricated.core.block.builder;

import com.conquestrefabricated.content.loom.WeavingRecipe;
import net.minecraft.core.HolderGetter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

/**
 * What a block declared through {@link Props#woven} needs to have a weaving recipe generated for it:
 * what goes onto the loom, how much comes off, and how long it takes.
 *
 * <p>The ingredient is kept as an item or a tag rather than a resolved {@code Ingredient} because
 * blocks are built long before tags are bound - {@link #toIngredient} resolves it at data generation
 * time, when the generator's registries are available.</p>
 *
 * <p>Exactly one of {@code ingredient} and {@code ingredientTag} is set.</p>
 *
 * @see ToolRecipeSpec
 */
public record WeavingRecipeSpec(@Nullable ItemLike ingredient,
                                @Nullable TagKey<Item> ingredientTag,
                                int count,
                                int time) {

    public WeavingRecipeSpec {
        if ((ingredient == null) == (ingredientTag == null)) {
            throw new IllegalArgumentException("A weaving recipe needs exactly one of an item or a tag ingredient");
        }
        if (count < 1) {
            throw new IllegalArgumentException("Weaving recipe count must be at least 1, got " + count);
        }
        if (time < 0) {
            throw new IllegalArgumentException("Weaving recipe time cannot be negative, got " + time);
        }
    }

    public static WeavingRecipeSpec of(ItemLike ingredient, int count, int time) {
        return new WeavingRecipeSpec(ingredient, null, count, time);
    }

    public static WeavingRecipeSpec of(TagKey<Item> ingredientTag, int count, int time) {
        return new WeavingRecipeSpec(null, ingredientTag, count, time);
    }

    /**
     * @param items the generator's item lookup, needed to resolve a tag ingredient
     */
    public Ingredient toIngredient(HolderGetter<Item> items) {
        return this.ingredientTag != null
                ? Ingredient.of(items.getOrThrow(this.ingredientTag))
                : Ingredient.of(this.ingredient);
    }

    /** The time to write, falling back to {@link WeavingRecipe#DEFAULT_TIME} when none was given. */
    public int timeOrDefault() {
        return this.time > 0 ? this.time : WeavingRecipe.DEFAULT_TIME;
    }
}
