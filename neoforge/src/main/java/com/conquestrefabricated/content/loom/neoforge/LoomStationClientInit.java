package com.conquestrefabricated.content.loom.neoforge;

import com.conquestrefabricated.client.gui.station.LoomScreen;
import com.conquestrefabricated.content.loom.LoomStation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** NeoForge-side client registration for the loom's weaving screen. */
@EventBusSubscriber(modid = "conquest", value = Dist.CLIENT)
public class LoomStationClientInit {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(LoomStation.MENU, LoomScreen::new);
    }
}
