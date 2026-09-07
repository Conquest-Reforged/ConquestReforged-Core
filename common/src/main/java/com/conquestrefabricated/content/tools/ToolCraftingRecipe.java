package com.conquestrefabricated.content.tools;

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
        // Kept out of the recipe book: the tool's own picker is where these are found.
        return true;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.STONECUTTER;
    }
}
