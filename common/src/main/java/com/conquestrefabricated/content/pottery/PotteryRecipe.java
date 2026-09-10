package com.conquestrefabricated.content.pottery;

import com.conquestrefabricated.content.station.TimedStationRecipe;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * One thing a pottery wheel can throw: put the clay in, pick a shape, wait for it.
 *
 * <pre>{@code
 * {
 *   "type": "conquest:pottery",
 *   "ingredient": "minecraft:clay_ball",
 *   "result": { "id": "conquest:terracotta_amphora", "count": 1 },
 *   "time": 100
 * }
 * }</pre>
 *
 * <p>All of the shape is {@link TimedStationRecipe}; this only says which station offers it.</p>
 *
 * @see PotteryRecipeBuilder
 */
public class PotteryRecipe extends TimedStationRecipe {

    public static final MapCodec<PotteryRecipe> MAP_CODEC = mapCodec(PotteryRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, PotteryRecipe> STREAM_CODEC =
            streamCodec(PotteryRecipe::new);

    public PotteryRecipe(CommonInfo commonInfo, Ingredient input, ItemStackTemplate result, int time) {
        super(commonInfo, input, result, time);
    }

    public PotteryRecipe(Ingredient input, ItemStackTemplate result, int time) {
        this(new CommonInfo(false), input, result, time);
    }

    @Override
    public RecipeType<PotteryRecipe> getType() {
        return PotteryWheelStation.RECIPE_TYPE;
    }

    @Override
    public RecipeSerializer<PotteryRecipe> getSerializer() {
        return PotteryWheelStation.RECIPE_SERIALIZER;
    }
}
