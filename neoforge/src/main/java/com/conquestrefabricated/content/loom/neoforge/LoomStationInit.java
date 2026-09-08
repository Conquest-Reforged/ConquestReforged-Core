package com.conquestrefabricated.content.loom.neoforge;

import com.conquestrefabricated.content.loom.LoomStation;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

/** NeoForge-side registration for the loom's menu and weaving recipe type. */
@EventBusSubscriber(modid = "conquest")
public class LoomStationInit {

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.MENU, helper -> helper.register(LoomStation.MENU_ID, LoomStation.createMenu()));
        event.register(Registries.RECIPE_TYPE, helper -> helper.register(LoomStation.ID, LoomStation.RECIPE_TYPE));
        event.register(Registries.RECIPE_SERIALIZER, helper -> helper.register(LoomStation.ID, LoomStation.RECIPE_SERIALIZER));
    }
}
