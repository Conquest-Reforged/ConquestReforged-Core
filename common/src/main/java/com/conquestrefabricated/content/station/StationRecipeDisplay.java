package com.conquestrefabricated.content.station;

import com.conquestrefabricated.core.Namespaces;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

/**
 * How one station recipe is described to whoever is looking at it.
 *
 * <p>Recipes themselves never reach the client - only vanilla's own types are serialised over the
 * wire, and a modded {@code RecipeType} is not among them. What does reach the client is this: the
 * server turns every recipe into {@link RecipeDisplay}s and syncs those instead. That is the only
 * road out to the client, so it is the road a recipe viewer has to use, and the reason this exists
 * at all. The picker inside a station does not need it - that has {@link StationOptionsPayload} -
 * but Roughly Enough Items and the vanilla recipe book do.</p>
 *
 * <p>One display type covers every station rather than one per station. They are all "some
 * ingredients, worked at this thing, giving that block", and the {@link #station} tells them apart,
 * exactly as {@code ToolCraftingRecipe} already distinguishes its tool sets by id. A viewer sorts
 * displays into categories by reading it.</p>
 *
 * @param ingredients     what goes in - one for most stations, two for a painter's kit
 * @param result          what comes out
 * @param craftingStation the tool or block this is worked at, drawn as the category's icon
 * @param station         which station this belongs to, e.g. {@code conquest:mason_tools}
 */
public record StationRecipeDisplay(List<SlotDisplay> ingredients, SlotDisplay result,
                                   SlotDisplay craftingStation, Identifier station)
        implements RecipeDisplay {

    /** Registry id of this display type. */
    public static final Identifier ID =
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "station");

    public static final MapCodec<StationRecipeDisplay> MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(StationRecipeDisplay::ingredients),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(StationRecipeDisplay::result),
                    SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(StationRecipeDisplay::craftingStation),
                    Identifier.CODEC.fieldOf("station").forGetter(StationRecipeDisplay::station)
            ).apply(instance, StationRecipeDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StationRecipeDisplay> STREAM_CODEC =
            StreamCodec.composite(
                    SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), StationRecipeDisplay::ingredients,
                    SlotDisplay.STREAM_CODEC, StationRecipeDisplay::result,
                    SlotDisplay.STREAM_CODEC, StationRecipeDisplay::craftingStation,
                    Identifier.STREAM_CODEC, StationRecipeDisplay::station,
                    StationRecipeDisplay::new);

    public static final RecipeDisplay.Type<StationRecipeDisplay> TYPE =
            new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeDisplay.Type<StationRecipeDisplay> type() {
        return TYPE;
    }

    /** The single-ingredient case, which is every station but the painter's kit. */
    public static List<RecipeDisplay> of(Ingredient ingredient, ItemStackTemplate result,
                                         Stations.Station station) {
        return List.of(new StationRecipeDisplay(
                List.of(ingredient.display()),
                new SlotDisplay.ItemStackSlotDisplay(result),
                new SlotDisplay.ItemSlotDisplay(station.displayItem()),
                station.id()));
    }

    /** The two-ingredient case: a base and the thing painted onto it. */
    public static List<RecipeDisplay> of(Ingredient base, Ingredient paint, ItemStackTemplate result,
                                         Stations.Station station) {
        return List.of(new StationRecipeDisplay(
                List.of(base.display(), paint.display()),
                new SlotDisplay.ItemStackSlotDisplay(result),
                new SlotDisplay.ItemSlotDisplay(station.displayItem()),
                station.id()));
    }
}
