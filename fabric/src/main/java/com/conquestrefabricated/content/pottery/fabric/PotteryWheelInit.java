package com.conquestrefabricated.content.pottery.fabric;

import com.conquestrefabricated.client.gui.station.PotteryWheelScreen;
import com.conquestrefabricated.content.pottery.PotteryWheelStation;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/** Fabric-side registration for the pottery wheel's menu and recipe type. */
public final class PotteryWheelInit {

    private PotteryWheelInit() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.MENU, PotteryWheelStation.MENU_ID, PotteryWheelStation.createMenu());
        Registry.register(BuiltInRegistries.RECIPE_TYPE, PotteryWheelStation.ID, PotteryWheelStation.RECIPE_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, PotteryWheelStation.ID,
                PotteryWheelStation.RECIPE_SERIALIZER);
    }

    public static void registerClient() {
        MenuScreens.register(PotteryWheelStation.MENU, PotteryWheelScreen::new);
    }
}
