package com.conquestrefabricated.content.lime.fabric;

import com.conquestrefabricated.content.lime.Lime;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/** Fabric-side registration for the lime items. */
public final class LimeInit {

    private LimeInit() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, Lime.QUICKLIME_KEY, Lime.createQuicklime());
        Registry.register(BuiltInRegistries.ITEM, Lime.SLAKED_LIME_KEY, Lime.createSlakedLime());
        Registry.register(BuiltInRegistries.ITEM, Lime.LIME_PLASTER_KEY, Lime.createLimePlaster());
    }
}
