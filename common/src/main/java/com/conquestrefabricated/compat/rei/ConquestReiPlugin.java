package com.conquestrefabricated.compat.rei;

import com.conquestrefabricated.content.station.Stations;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

/**
 * Makes Conquest's crafting stations findable in Roughly Enough Items.
 *
 * <p>Every station gets a category, and the block or kit you work at is registered as its
 * workstation, so looking up a block shows what makes it and clicking the station shows everything
 * it can make.</p>
 *
 * <p>Displays are filled from {@link StationRecipeDisplay} rather than from the recipes, because the
 * recipes are never sent to the client - see that class. Everything here is driven off
 * {@link Stations#all()}, so an addon that registers a set of crafting tools is picked up with no
 * extra code.</p>
 *
 * <p>This class is only ever loaded when REI is installed; Conquest does not depend on it.</p>
 */
public class ConquestReiPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        for (Stations.Station station : Stations.all()) {
            StationCategory category = new StationCategory(station);
            registry.add(category);
            registry.addWorkstations(category.getCategoryIdentifier(),
                    EntryStacks.of(station.displayStack()));
        }
    }

    /**
     * Only the shape toggle is registered here. The recipes themselves become displays on the
     * server - see {@link ConquestReiCommonPlugin} - because the client never receives a modded
     * recipe to build one from.
     */
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (Stations.Station station : Stations.all()) {
            if (station.shapes()) {
                registry.registerDisplayGenerator(StationCategory.identifierFor(station.id()),
                        new StationShapeGenerator(station, station.shapesOwnOutput()));
            }
        }
    }
}
