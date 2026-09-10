package com.conquestrefabricated.content.pottery.neoforge;

import com.conquestrefabricated.client.gui.station.PotteryWheelScreen;
import com.conquestrefabricated.content.pottery.PotteryWheelStation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** NeoForge-side client registration for the pottery wheel screen. */
@EventBusSubscriber(modid = "conquest", value = Dist.CLIENT)
public class PotteryWheelClientInit {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PotteryWheelStation.MENU, PotteryWheelScreen::new);
    }
}
