package com.conquestrefabricated.content.leatherworking.fabric;

import com.conquestrefabricated.content.leatherworking.LeatherworkingStations;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/** Fabric-side registration for the soaking and stretching recipe types. */
public final class LeatherworkingInit {

    private LeatherworkingInit() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, LeatherworkingStations.SOAKING_ID,
                LeatherworkingStations.SOAKING_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, LeatherworkingStations.SOAKING_ID,
                LeatherworkingStations.SOAKING_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, LeatherworkingStations.STRETCHING_ID,
                LeatherworkingStations.STRETCHING_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, LeatherworkingStations.STRETCHING_ID,
                LeatherworkingStations.STRETCHING_SERIALIZER);
    }
}
