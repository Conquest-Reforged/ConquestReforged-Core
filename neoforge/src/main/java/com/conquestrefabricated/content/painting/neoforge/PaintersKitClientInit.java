package com.conquestrefabricated.content.painting.neoforge;

import com.conquestrefabricated.client.gui.station.PaintersKitScreen;
import com.conquestrefabricated.content.painting.PaintersKit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** NeoForge-side client registration for the painter's kit screen. */
@EventBusSubscriber(modid = "conquest", value = Dist.CLIENT)
public class PaintersKitClientInit {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PaintersKit.MENU, PaintersKitScreen::new);
    }
}
