package com.conquestrefabricated.content.painting.neoforge;

import com.conquestrefabricated.content.painting.PaintersKit;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

/** NeoForge-side registration for the painter's kit item, its menu and the painting recipe type. */
@EventBusSubscriber(modid = "conquest")
public class PaintersKitInit {

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.ITEM, helper -> helper.register(PaintersKit.ID, PaintersKit.createItem()));
        event.register(Registries.MENU, helper -> helper.register(PaintersKit.ID, PaintersKit.createMenu()));
        event.register(Registries.RECIPE_TYPE, helper ->
                helper.register(PaintersKit.RECIPE_ID, PaintersKit.RECIPE_TYPE));
        event.register(Registries.RECIPE_SERIALIZER, helper ->
                helper.register(PaintersKit.RECIPE_ID, PaintersKit.RECIPE_SERIALIZER));
    }
}
