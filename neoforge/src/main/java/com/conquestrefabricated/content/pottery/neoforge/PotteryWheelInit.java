package com.conquestrefabricated.content.pottery.neoforge;

import com.conquestrefabricated.content.pottery.PotteryWheelStation;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

/** NeoForge-side registration for the pottery wheel's menu and recipe type. */
@EventBusSubscriber(modid = "conquest")
public class PotteryWheelInit {

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.MENU, helper ->
                helper.register(PotteryWheelStation.MENU_ID, PotteryWheelStation.createMenu()));
        event.register(Registries.RECIPE_TYPE, helper ->
                helper.register(PotteryWheelStation.ID, PotteryWheelStation.RECIPE_TYPE));
        event.register(Registries.RECIPE_SERIALIZER, helper ->
                helper.register(PotteryWheelStation.ID, PotteryWheelStation.RECIPE_SERIALIZER));
    }
}
