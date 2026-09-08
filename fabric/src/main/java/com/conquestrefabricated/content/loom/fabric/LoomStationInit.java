package com.conquestrefabricated.content.loom.fabric;

import com.conquestrefabricated.client.gui.station.LoomScreen;
import com.conquestrefabricated.content.loom.LoomStation;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/** Fabric-side registration for the loom's menu and weaving recipe type. */
public final class LoomStationInit {

    private LoomStationInit() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.MENU, LoomStation.MENU_ID, LoomStation.createMenu());
        Registry.register(BuiltInRegistries.RECIPE_TYPE, LoomStation.ID, LoomStation.RECIPE_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, LoomStation.ID, LoomStation.RECIPE_SERIALIZER);
    }

    public static void registerClient() {
        MenuScreens.register(LoomStation.MENU, LoomScreen::new);
    }
}
