package com.conquestrefabricated.core.block.builder;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

/**
 * What a block declared through {@link Props#craftedWith} needs to have a crafting tool recipe
 * generated for it: which set of tools makes it, and what goes in.
 *
 * <p>The ingredient is kept as an item or a tag rather than a resolved {@code Ingredient} because
 * blocks are built long before tags are bound - {@link #toIngredient} resolves it at data generation
 * time, when the generator's registries are available.</p>
 *
 * <p>Exactly one of {@code ingredient} and {@code ingredientTag} is set.</p>
 */
public record ToolRecipeSpec(Identifier tool,
                             @Nullable ItemLike ingredient,
                             @Nullable TagKey<Item> ingredientTag,
                             int count) {

    public ToolRecipeSpec {
        if ((ingredient == null) == (ingredientTag == null)) {
            throw new IllegalArgumentException("A tool recipe needs exactly one of an item or a tag ingredient");
        }
        if (count < 1) {
            throw new IllegalArgumentException("Tool recipe count must be at least 1, got " + count);
        }
    }

    public static ToolRecipeSpec of(Identifier tool, ItemLike ingredient, int count) {
        return new ToolRecipeSpec(tool, ingredient, null, count);
    }

    public static ToolRecipeSpec of(Identifier tool, TagKey<Item> ingredientTag, int count) {
        return new ToolRecipeSpec(tool, null, ingredientTag, count);
    }

    /**
     * @param items the generator's item lookup, needed to resolve a tag ingredient
     */
    public Ingredient toIngredient(HolderGetter<Item> items) {
        return this.ingredientTag != null
                ? Ingredient.of(items.getOrThrow(this.ingredientTag))
                : Ingredient.of(this.ingredient);
    }
}
