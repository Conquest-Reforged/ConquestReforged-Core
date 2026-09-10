package com.conquestrefabricated.content.loom;

import com.conquestrefabricated.content.station.TimedStationRecipe;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

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
 * <p>All of the shape is {@link TimedStationRecipe}; this only says which station offers it.</p>
 *
 * @see WeavingRecipeBuilder
 */
public class WeavingRecipe extends TimedStationRecipe {

    public static final MapCodec<WeavingRecipe> MAP_CODEC = mapCodec(WeavingRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, WeavingRecipe> STREAM_CODEC =
            streamCodec(WeavingRecipe::new);

    public WeavingRecipe(CommonInfo commonInfo, Ingredient input, ItemStackTemplate result, int time) {
        super(commonInfo, input, result, time);
    }

    public WeavingRecipe(Ingredient input, ItemStackTemplate result, int time) {
        this(new CommonInfo(false), input, result, time);
    }

    @Override
    public RecipeType<WeavingRecipe> getType() {
        return LoomStation.RECIPE_TYPE;
    }

    @Override
    public RecipeSerializer<WeavingRecipe> getSerializer() {
        return LoomStation.RECIPE_SERIALIZER;
    }
}
