package com.conquestrefabricated.compat.rei;

import com.conquestrefabricated.content.station.Stations;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe viewer category for one station: its ingredients on the left, an arrow, the block it
 * makes on the right.
 *
 * <p>One class covers every station rather than one per station, the same way
 * {@link com.conquestrefabricated.content.station.StationRecipeDisplay} does, so an addon that
 * registers its own set of crafting tools gets a category without writing any of this.</p>
 */
public class StationCategory implements DisplayCategory<StationDisplay> {

    private final Stations.Station station;
    private final CategoryIdentifier<StationDisplay> identifier;

    public StationCategory(Stations.Station station) {
        this.station = station;
        this.identifier = identifierFor(station.id());
    }

    /** A station's category id. Derived from the station id so both sides agree without a lookup. */
    public static CategoryIdentifier<StationDisplay> identifierFor(Identifier station) {
        return CategoryIdentifier.of(station);
    }

    public Stations.Station station() {
        return this.station;
    }

    @Override
    public CategoryIdentifier<? extends StationDisplay> getCategoryIdentifier() {
        return this.identifier;
    }

    /**
     * Named after the station's own item, so the category reads "Mason's Tools" and follows the
     * player's language without a translation key of its own.
     */
    @Override
    public Component getTitle() {
        return this.station.displayStack().getHoverName();
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(this.station.displayStack());
    }

    @Override
    public int getDisplayHeight() {
        return 49;
    }

    @Override
    public List<Widget> setupDisplay(StationDisplay display, Rectangle bounds) {
        Point origin = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(origin.x + 27, origin.y + 4)));

        // A painter's kit takes two: the base, then what is painted onto it. Everything else takes
        // one, and its single slot sits where the base would be.
        List<EntryIngredient> inputs = display.getInputEntries();
        for (int i = 0; i < inputs.size(); i++) {
            widgets.add(Widgets.createSlot(new Point(origin.x + 1, origin.y + 1 + i * 18))
                    .entries(inputs.get(i))
                    .markInput());
        }

        widgets.add(Widgets.createResultSlotBackground(new Point(origin.x + 61, origin.y + 5)));
        widgets.add(Widgets.createSlot(new Point(origin.x + 61, origin.y + 5))
                .entries(display.getOutputEntries().isEmpty()
                        ? EntryIngredient.empty()
                        : display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());
        return widgets;
    }
}
