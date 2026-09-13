package com.conquestrefabricated.content.tools;

import java.util.List;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.Items;
import com.conquestrefabricated.content.station.StationRecipeDisplay;
import com.conquestrefabricated.content.station.Stations;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;

/**
 * One entry in a crafting tool's picker: put the ingredient in, pick a shape, get the block.
 *
 * <p>Every tool set shares this recipe type and is told apart by the {@code tool} field, so adding a
 * new set of tools needs an item and a menu type, not a new recipe type:</p>
 * <pre>{@code
 * {
 *   "type": "conquest:tool_crafting",
 *   "tool": "conquest:mason",
 *   "ingredient": "minecraft:stone",
 *   "result": { "id": "conquest:stone_ashlar", "count": 1 }
 * }
 * }</pre>
 *
 * <p>Unlike the arms station these carry nothing over from the input - a block is a block.</p>
 *
 * @see ToolCraftingRecipeBuilder
 */
public class ToolCraftingRecipe extends SingleItemRecipe {

    public static final MapCodec<ToolCraftingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
            Identifier.CODEC.fieldOf("tool").forGetter(ToolCraftingRecipe::tool),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(ToolCraftingRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(ToolCraftingRecipe::result)
    ).apply(instance, ToolCraftingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            Identifier.STREAM_CODEC, ToolCraftingRecipe::tool,
            Ingredient.CONTENTS_STREAM_CODEC, ToolCraftingRecipe::input,
            ItemStackTemplate.STREAM_CODEC, ToolCraftingRecipe::result,
            ToolCraftingRecipe::new
    );

    private final Identifier tool;

    public ToolCraftingRecipe(CommonInfo commonInfo, Identifier tool, Ingredient input, ItemStackTemplate result) {
        super(commonInfo, input, result);
        this.tool = tool;
    }

    public ToolCraftingRecipe(Identifier tool, Ingredient input, ItemStackTemplate result) {
        this(new CommonInfo(false), tool, input, result);
    }

    /** Which tool set offers this recipe, e.g. {@code conquest:mason}. */
    public Identifier tool() {
        return this.tool;
    }

    @Override
    public RecipeType<ToolCraftingRecipe> getType() {
        return CraftingTools.RECIPE_TYPE;
    }

    @Override
    public RecipeSerializer<ToolCraftingRecipe> getSerializer() {
        return CraftingTools.RECIPE_SERIALIZER;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean isSpecial() {
        // Not special, though nothing crafts these at a bench. A special recipe is skipped by
        // ServerRecipeBook.addRecipes, which is the only thing that sends a recipe's display to the
        // client - so marking these special made them invisible to every recipe viewer. They are
        // kept out of the recipe book by their category instead, see Stations.
        return false;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return Stations.RECIPE_BOOK_CATEGORY;
    }

    /**
     * What a recipe viewer and the recipe book are shown. Modded recipes never cross to the client,
     * so this is the only description of it that gets there.
     *
     * @see com.conquestrefabricated.content.station.StationRecipeDisplay
     */
    @Override
    public List<RecipeDisplay> display() {
        return StationRecipeDisplay.of(this.input(), this.result(),
                CraftingTools.get(this.tool).map(Stations::of).orElseGet(
                        () -> new Stations.Station(this.tool, Items.STONECUTTER, true, false)));
    }
}
