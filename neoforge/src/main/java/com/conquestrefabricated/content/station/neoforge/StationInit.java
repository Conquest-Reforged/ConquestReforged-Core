package com.conquestrefabricated.content.station.neoforge;

import com.conquestrefabricated.content.station.StationRecipeDisplay;
import com.conquestrefabricated.content.station.StationRecipeUnlock;
import com.conquestrefabricated.content.station.Stations;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

/** NeoForge-side wiring shared by every crafting station. */
@EventBusSubscriber(modid = "conquest")
public class StationInit {

    /** Awards station recipes on join, which is what gets their displays to the client. */
    public static void register() {
        StationRecipeUnlock.register();
    }

    /**
     * How station recipes reach the client at all: the recipes themselves are not synced, their
     * displays are. Without this the server cannot describe them and no recipe viewer sees them.
     */
    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.RECIPE_DISPLAY, helper ->
                helper.register(StationRecipeDisplay.ID, StationRecipeDisplay.TYPE));
        event.register(Registries.RECIPE_BOOK_CATEGORY, helper ->
                helper.register(Stations.RECIPE_BOOK_CATEGORY_ID, Stations.RECIPE_BOOK_CATEGORY));
    }
}
