package com.conquestrefabricated.content.arms.fabric;

import com.conquestrefabricated.client.gui.arms.ArmsStationClient;
import com.conquestrefabricated.client.gui.arms.ArmsStationScreen;
import com.conquestrefabricated.content.arms.ArmsStation;
import com.conquestrefabricated.content.arms.ArmsStationNetwork;
import com.conquestrefabricated.content.arms.ArmsStationOptionsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/** Fabric-side registration for the arms station block, menu, recipe type and option sync. */
public final class ArmsStationInit {

    private ArmsStationInit() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, ArmsStation.BLOCK_KEY, ArmsStation.createBlock());
        Registry.register(BuiltInRegistries.ITEM, ArmsStation.ITEM_KEY, ArmsStation.createItem());
        Registry.register(BuiltInRegistries.MENU, ArmsStation.ID, ArmsStation.createMenu());
        Registry.register(BuiltInRegistries.RECIPE_TYPE, ArmsStation.ID, ArmsStation.RECIPE_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ArmsStation.ID, ArmsStation.RECIPE_SERIALIZER);

        PayloadTypeRegistry.clientboundPlay().register(ArmsStationOptionsPayload.ID, ArmsStationOptionsPayload.CODEC);
        ArmsStationNetwork.setSender(ServerPlayNetworking::send);
    }

    public static void registerClient() {
        MenuScreens.register(ArmsStation.MENU, ArmsStationScreen::new);
        ClientPlayNetworking.registerGlobalReceiver(ArmsStationOptionsPayload.ID,
                (payload, context) -> context.client().execute(() -> ArmsStationClient.applyOptions(payload)));
    }
}
