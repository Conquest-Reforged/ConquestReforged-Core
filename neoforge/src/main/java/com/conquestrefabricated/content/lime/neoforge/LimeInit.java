package com.conquestrefabricated.content.lime.neoforge;

import com.conquestrefabricated.content.lime.Lime;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

/** NeoForge-side registration for the lime items. */
@EventBusSubscriber(modid = "conquest")
public class LimeInit {

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.ITEM, helper -> {
            helper.register(Lime.QUICKLIME_ID, Lime.createQuicklime());
            helper.register(Lime.SLAKED_LIME_ID, Lime.createSlakedLime());
            helper.register(Lime.LIME_PLASTER_ID, Lime.createLimePlaster());
        });
    }
}
