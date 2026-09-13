package com.conquestrefabricated.content.station.fabric;

import com.conquestrefabricated.client.gui.station.StationClient;
import com.conquestrefabricated.content.station.StationNetwork;
import com.conquestrefabricated.content.station.StationOptionsPayload;
import com.conquestrefabricated.content.station.StationRecipeDisplay;
import com.conquestrefabricated.content.station.StationRecipeUnlock;
import com.conquestrefabricated.content.station.Stations;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/** Fabric-side wiring shared by every crafting station: the option sync channel. */
public final class StationInit {

    private StationInit() {
    }

    public static void register() {
        // How station recipes reach the client at all: the recipes themselves are not synced, their
        // displays are. Without this the server cannot describe them and no recipe viewer sees them.
        Registry.register(BuiltInRegistries.RECIPE_DISPLAY,
                StationRecipeDisplay.ID, StationRecipeDisplay.TYPE);
        Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY,
                Stations.RECIPE_BOOK_CATEGORY_ID, Stations.RECIPE_BOOK_CATEGORY);
        StationRecipeUnlock.register();

        PayloadTypeRegistry.clientboundPlay().register(StationOptionsPayload.ID, StationOptionsPayload.CODEC);
        StationNetwork.setSender(ServerPlayNetworking::send);
    }

    public static void registerClient() {

        ClientPlayNetworking.registerGlobalReceiver(StationOptionsPayload.ID,
                (payload, context) -> context.client().execute(() -> StationClient.applyOptions(payload)));
    }
}
