package com.conquestrefabricated.content.painting.fabric;

import com.conquestrefabricated.client.gui.station.PaintersKitScreen;
import com.conquestrefabricated.content.painting.PaintersKit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/** Fabric-side registration for the painter's kit item, its menu and the painting recipe type. */
public final class PaintersKitInit {

    private PaintersKitInit() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, PaintersKit.ITEM_KEY, PaintersKit.createItem());
        Registry.register(BuiltInRegistries.MENU, PaintersKit.ID, PaintersKit.createMenu());
        Registry.register(BuiltInRegistries.RECIPE_TYPE, PaintersKit.RECIPE_ID, PaintersKit.RECIPE_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, PaintersKit.RECIPE_ID, PaintersKit.RECIPE_SERIALIZER);
    }

    public static void registerClient() {
        MenuScreens.register(PaintersKit.MENU, PaintersKitScreen::new);
    }
}
