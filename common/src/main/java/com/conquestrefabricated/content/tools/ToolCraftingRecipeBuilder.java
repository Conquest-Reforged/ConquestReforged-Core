package com.conquestrefabricated.content.tools;

import com.conquestrefabricated.core.block.builder.ToolRecipeSpec;
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
 * Builds {@code conquest:tool_crafting} recipes from a data generator.
 *
 * <p>Most of the time you won't call this directly - declaring
 * {@code Props.craftedWith(CraftingTools.MASON.id(), Blocks.GRANITE)} on a block is enough, and the
 * core's recipe provider turns that into one of these for the family's parent. Reach for the builder
 * for one-off recipes that don't belong to a block family:</p>
 *
 * <pre>{@code
 * ToolCraftingRecipeBuilder.toolCrafting(CraftingTools.MASON.id(), Blocks.CLAY, ModBlocks.ROOF_TILES)
 *         .count(4)
 *         .save(this.output);
 * }</pre>
 *
 * <p>Recipes are written without an unlock advancement: the tools themselves are the discovery
 * surface, not the recipe book.</p>
 */
public final class ToolCraftingRecipeBuilder {

    /** Appended to the result's item name when no explicit recipe id is given. */
    public static final String DEFAULT_ID_SUFFIX = "_from_tools";

    private final Identifier tool;
    private final Ingredient input;
    private final ItemLike result;
    private int count = 1;
    private boolean showNotification = false;

    private ToolCraftingRecipeBuilder(Identifier tool, Ingredient input, ItemLike result) {
        this.tool = tool;
        this.input = input;
        this.result = result;
    }

    public static ToolCraftingRecipeBuilder toolCrafting(Identifier tool, Ingredient input, ItemLike result) {
        return new ToolCraftingRecipeBuilder(tool, input, result);
    }

    public static ToolCraftingRecipeBuilder toolCrafting(Identifier tool, ItemLike input, ItemLike result) {
        return toolCrafting(tool, Ingredient.of(input), result);
    }

    /** Builds from a {@link ToolRecipeSpec} declared on a block's {@code Props}. */
    public static ToolCraftingRecipeBuilder from(ToolRecipeSpec spec, HolderGetter<Item> items, ItemLike result) {
        return toolCrafting(spec.tool(), spec.toIngredient(items), result).count(spec.count());
    }

    /** How many of the result a single craft yields. Defaults to 1. */
    public ToolCraftingRecipeBuilder count(int count) {
        this.count = count;
        return this;
    }

    /** Whether crafting this pops the "new recipe unlocked" toast. Defaults to false. */
    public ToolCraftingRecipeBuilder showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    public ToolCraftingRecipe build() {
        return new ToolCraftingRecipe(
                new Recipe.CommonInfo(this.showNotification),
                this.tool,
                this.input,
                new ItemStackTemplate(this.result.asItem(), this.count));
    }

    /** Writes the recipe under {@code id}. */
    public void save(RecipeOutput output, Identifier id) {
        output.accept(ResourceKey.create(Registries.RECIPE, id), this.build(), null);
    }

    /** Writes the recipe under {@code <result namespace>:<result path>_from_tools}. */
    public void save(RecipeOutput output) {
        this.save(output, defaultId(this.result));
    }

    /** The id {@link #save(RecipeOutput)} would use for {@code result}. */
    public static Identifier defaultId(ItemLike result) {
        Identifier resultId = BuiltInRegistries.ITEM.getKey(result.asItem());
        return Identifier.fromNamespaceAndPath(resultId.getNamespace(), resultId.getPath() + DEFAULT_ID_SUFFIX);
    }
}
