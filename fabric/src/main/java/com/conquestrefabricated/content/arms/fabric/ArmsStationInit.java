package com.conquestrefabricated.content.arms.fabric;

import com.conquestrefabricated.client.gui.station.StationScreen;
import com.conquestrefabricated.content.arms.ArmsStation;
import com.conquestrefabricated.content.arms.ArmsStationMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/** Fabric-side registration for the arms station block, menu and recipe type. */
public final class ArmsStationInit {

    private ArmsStationInit() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, ArmsStation.BLOCK_KEY, ArmsStation.createBlock());
        Registry.register(BuiltInRegistries.ITEM, ArmsStation.ITEM_KEY, ArmsStation.createItem());
        Registry.register(BuiltInRegistries.MENU, ArmsStation.ID, ArmsStation.createMenu());
        Registry.register(BuiltInRegistries.RECIPE_TYPE, ArmsStation.ID, ArmsStation.RECIPE_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ArmsStation.ID, ArmsStation.RECIPE_SERIALIZER);
    }

    public static void registerClient() {
        MenuScreens.register(ArmsStation.MENU, StationScreen<ArmsStationMenu>::new);
    }
}
