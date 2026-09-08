package com.conquestrefabricated.content.loom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;

/**
 * One thing a loom can weave: put the ingredient in, pick a cloth, wait for it.
 *
 * <pre>{@code
 * {
 *   "type": "conquest:weaving",
 *   "ingredient": "minecraft:red_wool",
 *   "result": { "id": "conquest:red_canvas", "count": 1 },
 *   "time": 100
 * }
 * }</pre>
 *
 * <p>The only thing this adds over a stonecutting recipe is {@code time}, the ticks a single craft
 * takes. Weaving is the slow half of the loom; the shapes cut from a finished cloth are reached
 * through the picker's family toggle instead, and those are instant.</p>
 *
 * @see WeavingRecipeBuilder
 */
public class WeavingRecipe extends SingleItemRecipe {

    /** Ticks a craft takes when the recipe doesn't say. Five seconds, as an iron ingot smelts. */
    public static final int DEFAULT_TIME = 100;

    public static final MapCodec<WeavingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(WeavingRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(WeavingRecipe::result),
            Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(WeavingRecipe::time)
    ).apply(instance, WeavingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WeavingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC, WeavingRecipe::input,
            ItemStackTemplate.STREAM_CODEC, WeavingRecipe::result,
            ByteBufCodecs.VAR_INT, WeavingRecipe::time,
            WeavingRecipe::new
    );

    private final int time;

    public WeavingRecipe(CommonInfo commonInfo, Ingredient input, ItemStackTemplate result, int time) {
        super(commonInfo, input, result);
        this.time = time;
    }

    public WeavingRecipe(Ingredient input, ItemStackTemplate result, int time) {
        this(new CommonInfo(false), input, result, time);
    }

    /** How many ticks one craft takes. */
    public int time() {
        return this.time;
    }

    @Override
    public RecipeType<WeavingRecipe> getType() {
        return LoomStation.RECIPE_TYPE;
    }

    @Override
    public RecipeSerializer<WeavingRecipe> getSerializer() {
        return LoomStation.RECIPE_SERIALIZER;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean isSpecial() {
        // Kept out of the recipe book: the loom's own picker is where these are found.
        return true;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.STONECUTTER;
    }
}
