package com.conquestrefabricated.content.loom;

import com.conquestrefabricated.core.block.builder.WeavingRecipeSpec;
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
 * Builds {@code conquest:weaving} recipes from a data generator.
 *
 * <p>Most of the time you won't call this directly - declaring {@code Props.woven(Blocks.WHITE_WOOL)}
 * on a block is enough, and the core's recipe provider turns that into one of these for the family's
 * parent. Reach for the builder for one-off recipes that don't belong to a block family:</p>
 *
 * <pre>{@code
 * WeavingRecipeBuilder.weaving(Items.STRING, ModBlocks.LINEN)
 *         .count(2)
 *         .time(160)
 *         .save(this.output);
 * }</pre>
 *
 * <p>Recipes are written without an unlock advancement: the loom itself is the discovery surface,
 * not the recipe book.</p>
 */
public final class WeavingRecipeBuilder {

    /** Appended to the result's item name when no explicit recipe id is given. */
    public static final String DEFAULT_ID_SUFFIX = "_from_loom";

    private final Ingredient input;
    private final ItemLike result;
    private int count = 1;
    private int time = WeavingRecipe.DEFAULT_TIME;
    private boolean showNotification = false;

    private WeavingRecipeBuilder(Ingredient input, ItemLike result) {
        this.input = input;
        this.result = result;
    }

    public static WeavingRecipeBuilder weaving(Ingredient input, ItemLike result) {
        return new WeavingRecipeBuilder(input, result);
    }

    public static WeavingRecipeBuilder weaving(ItemLike input, ItemLike result) {
        return weaving(Ingredient.of(input), result);
    }

    /** Builds from a {@link WeavingRecipeSpec} declared on a block's {@code Props}. */
    public static WeavingRecipeBuilder from(WeavingRecipeSpec spec, HolderGetter<Item> items, ItemLike result) {
        return weaving(spec.toIngredient(items), result)
                .count(spec.count())
                .time(spec.timeOrDefault());
    }

    /** How many of the result a single craft yields. Defaults to 1. */
    public WeavingRecipeBuilder count(int count) {
        this.count = count;
        return this;
    }

    /**
     * How many ticks a single craft takes. Defaults to {@link WeavingRecipe#DEFAULT_TIME}.
     *
     * <p>Zero makes the craft instant, and works through the whole input stack in one go. That is
     * what the picker's family shapes do, so use it sparingly for a recipe of your own.</p>
     */
    public WeavingRecipeBuilder time(int time) {
        this.time = time;
        return this;
    }

    /** Whether crafting this pops the "new recipe unlocked" toast. Defaults to false. */
    public WeavingRecipeBuilder showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    public WeavingRecipe build() {
        return new WeavingRecipe(
                new Recipe.CommonInfo(this.showNotification),
                this.input,
                new ItemStackTemplate(this.result.asItem(), this.count),
                this.time);
    }

    /** Writes the recipe under {@code id}. */
    public void save(RecipeOutput output, Identifier id) {
        output.accept(ResourceKey.create(Registries.RECIPE, id), this.build(), null);
    }

    /** Writes the recipe under {@code <result namespace>:<result path>_from_loom}. */
    public void save(RecipeOutput output) {
        this.save(output, defaultId(this.result));
    }

    /** The id {@link #save(RecipeOutput)} would use for {@code result}. */
    public static Identifier defaultId(ItemLike result) {
        Identifier resultId = BuiltInRegistries.ITEM.getKey(result.asItem());
        return Identifier.fromNamespaceAndPath(resultId.getNamespace(), resultId.getPath() + DEFAULT_ID_SUFFIX);
    }
}
