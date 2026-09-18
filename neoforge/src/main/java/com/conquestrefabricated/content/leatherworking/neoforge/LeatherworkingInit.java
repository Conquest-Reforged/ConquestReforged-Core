package com.conquestrefabricated.content.leatherworking.neoforge;

import com.conquestrefabricated.content.leatherworking.LeatherworkingStations;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

/** NeoForge-side registration for the soaking and stretching recipe types. */
@EventBusSubscriber(modid = "conquest")
public class LeatherworkingInit {

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.RECIPE_TYPE, helper -> {
            helper.register(LeatherworkingStations.SOAKING_ID, LeatherworkingStations.SOAKING_TYPE);
            helper.register(LeatherworkingStations.STRETCHING_ID, LeatherworkingStations.STRETCHING_TYPE);
        });
        event.register(Registries.RECIPE_SERIALIZER, helper -> {
            helper.register(LeatherworkingStations.SOAKING_ID, LeatherworkingStations.SOAKING_SERIALIZER);
            helper.register(LeatherworkingStations.STRETCHING_ID, LeatherworkingStations.STRETCHING_SERIALIZER);
        });
    }
}
