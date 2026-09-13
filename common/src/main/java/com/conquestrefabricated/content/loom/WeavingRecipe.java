package com.conquestrefabricated.content.loom;

import java.util.List;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.Items;
import com.conquestrefabricated.content.station.StationRecipeDisplay;
import com.conquestrefabricated.content.station.Stations;
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

    /**
     * What a recipe viewer and the recipe book are shown. Modded recipes never cross to the client,
     * so this is the only description of it that gets there.
     *
     * @see com.conquestrefabricated.content.station.StationRecipeDisplay
     */
    @Override
    public List<RecipeDisplay> display() {
        return StationRecipeDisplay.of(this.input(), this.templateResult(), Stations.LOOM);
    }
}
