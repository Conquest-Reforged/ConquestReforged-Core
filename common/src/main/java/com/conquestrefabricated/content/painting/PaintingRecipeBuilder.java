package com.conquestrefabricated.content.painting;

import com.conquestrefabricated.core.block.builder.PaintingRecipeSpec;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

/**
 * Builds {@code conquest:painting} recipes from a data generator.
 *
 * <p>Most of the time you won't call this directly - declaring
 * {@code Props.painted(Blocks.COBBLESTONE, Items.RED_DYE)} on a block is enough, and the core's
 * recipe provider turns that into one of these for the family's parent. Reach for the builder for
 * one-off recipes that don't belong to a block family:</p>
 *
 * <pre>{@code
 * PaintingRecipeBuilder.painting(Blocks.COBBLESTONE, Items.LIME, ModBlocks.LIMEWASHED_COBBLE)
 *         .count(1)
 *         .save(this.output);
 * }</pre>
 *
 * <p>Recipes are written without an unlock advancement: the kit itself is the discovery surface,
 * not the recipe book.</p>
 */
public final class PaintingRecipeBuilder {

    /** Appended to the result's item name when no explicit recipe id is given. */
    public static final String DEFAULT_ID_SUFFIX = "_from_painting";

    private final Ingredient base;
    private final Ingredient paint;
    private final ItemLike result;
    private int count = 1;
    private boolean showNotification = false;

    private PaintingRecipeBuilder(Ingredient base, Ingredient paint, ItemLike result) {
        this.base = base;
        this.paint = paint;
        this.result = result;
    }

    public static PaintingRecipeBuilder painting(Ingredient base, Ingredient paint, ItemLike result) {
        return new PaintingRecipeBuilder(base, paint, result);
    }

    public static PaintingRecipeBuilder painting(ItemLike base, ItemLike paint, ItemLike result) {
        return painting(Ingredient.of(base), Ingredient.of(paint), result);
    }

    /** Builds from a {@link PaintingRecipeSpec} declared on a block's {@code Props}. */
    public static PaintingRecipeBuilder from(PaintingRecipeSpec spec, HolderGetter<Item> items, ItemLike result) {
        return painting(spec.base().toIngredient(items), spec.paint().toIngredient(items), result)
                .count(spec.count());
    }

    /** How many of the result a single craft yields. Defaults to 1. */
    public PaintingRecipeBuilder count(int count) {
        this.count = count;
        return this;
    }

    /** Whether crafting this pops the "new recipe unlocked" toast. Defaults to false. */
    public PaintingRecipeBuilder showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    public PaintingRecipe build() {
        return new PaintingRecipe(
                new Recipe.CommonInfo(this.showNotification),
                this.base,
                this.paint,
                new ItemStackTemplate(this.result.asItem(), this.count));
    }

    /** Writes the recipe under {@code id}. */
    public void save(RecipeOutput output, Identifier id) {
        output.accept(ResourceKey.create(Registries.RECIPE, id), this.build(), null);
    }

    /** Writes the recipe under {@code <result namespace>:<result path>_from_painting}. */
    public void save(RecipeOutput output) {
        this.save(output, defaultId(this.result));
    }

    /** The id {@link #save(RecipeOutput)} would use for {@code result}. */
    public static Identifier defaultId(ItemLike result) {
        Identifier resultId = BuiltInRegistries.ITEM.getKey(result.asItem());
        return Identifier.fromNamespaceAndPath(resultId.getNamespace(), resultId.getPath() + DEFAULT_ID_SUFFIX);
    }
}
