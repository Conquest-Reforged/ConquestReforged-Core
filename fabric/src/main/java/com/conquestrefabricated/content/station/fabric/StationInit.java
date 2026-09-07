package com.conquestrefabricated.content.station.fabric;

import com.conquestrefabricated.client.gui.station.StationClient;
import com.conquestrefabricated.content.station.StationNetwork;
import com.conquestrefabricated.content.station.StationOptionsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/** Fabric-side wiring shared by every crafting station: the option sync channel. */
public final class StationInit {

    private StationInit() {
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(StationOptionsPayload.ID, StationOptionsPayload.CODEC);
        StationNetwork.setSender(ServerPlayNetworking::send);
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(StationOptionsPayload.ID,
                (payload, context) -> context.client().execute(() -> StationClient.applyOptions(payload)));
    }
}
