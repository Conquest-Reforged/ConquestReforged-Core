package com.conquestrefabricated.content.arms.neoforge;

import com.conquestrefabricated.client.gui.arms.ArmsStationScreen;
import com.conquestrefabricated.content.arms.ArmsStation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** NeoForge-side client registration for the arms station screen. */
@EventBusSubscriber(modid = "conquest", value = Dist.CLIENT)
public class ArmsStationClientInit {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ArmsStation.MENU, ArmsStationScreen::new);
    }
}
