package com.conquestrefabricated.compat.rei;

import com.conquestrefabricated.content.station.Stations;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * One station recipe, as Roughly Enough Items sees it.
 *
 * <p>These are built on the <i>server</i>, from the recipes themselves, and sent to the client over
 * REI's own display sync - which is why they are serialisable. That is the road REI actually uses;
 * the vanilla recipe book is not involved. See {@link ConquestReiCommonPlugin}.</p>
 *
 * <p>The station id travels with the display, and the category is derived from it, so one class
 * covers every station and an addon's tool set needs no code of its own.</p>
 */
public class StationDisplay extends BasicDisplay {

    /** Registry id of this display's serializer. */
    public static final Identifier SERIALIZER_ID =
            Identifier.fromNamespaceAndPath(com.conquestrefabricated.core.Namespaces.DEFAULT, "station");

    public static final DisplaySerializer<StationDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("inputs")
                            .forGetter(StationDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("outputs")
                            .forGetter(StationDisplay::getOutputEntries),
                    Identifier.CODEC.fieldOf("station").forGetter(StationDisplay::station),
                    Identifier.CODEC.optionalFieldOf("location")
                            .forGetter(StationDisplay::getDisplayLocation)
            ).apply(instance, StationDisplay::new)),
            StreamCodec.composite(
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                    StationDisplay::getInputEntries,
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                    StationDisplay::getOutputEntries,
                    Identifier.STREAM_CODEC, StationDisplay::station,
                    ByteBufCodecs.optional(Identifier.STREAM_CODEC), StationDisplay::getDisplayLocation,
                    StationDisplay::new));

    private final Identifier station;

    public StationDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs,
                          Identifier station, Optional<Identifier> location) {
        super(inputs, outputs, location);
        this.station = station;
    }

    /** The single-ingredient case, which is every station but the painter's kit. */
    public static StationDisplay of(RecipeHolder<?> holder, Ingredient ingredient, ItemStack result,
                                    Stations.Station station) {
        return of(holder, List.of(ingredient), result, station);
    }

    public static StationDisplay of(RecipeHolder<?> holder, List<Ingredient> ingredients,
                                    ItemStack result, Stations.Station station) {
        List<EntryIngredient> inputs = new ArrayList<>(ingredients.size());
        for (Ingredient ingredient : ingredients) {
            inputs.add(EntryIngredients.ofIngredient(ingredient));
        }
        return new StationDisplay(inputs, List.of(EntryIngredients.of(result)), station.id(),
                Optional.ofNullable(holder).map(h -> h.id().identifier()));
    }

    /** Which station this belongs to, e.g. {@code conquest:mason_tools}. */
    public Identifier station() {
        return this.station;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return StationCategory.identifierFor(this.station);
    }

    @Override
    public DisplaySerializer<? extends BasicDisplay> getSerializer() {
        return SERIALIZER;
    }
}
