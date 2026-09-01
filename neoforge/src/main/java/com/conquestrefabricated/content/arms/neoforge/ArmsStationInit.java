package com.conquestrefabricated.content.arms.neoforge;

import com.conquestrefabricated.content.arms.ArmsStation;
import com.conquestrefabricated.content.arms.ArmsStationNetwork;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.RegisterEvent;

/** NeoForge-side registration for the arms station block, menu and recipe type. */
@EventBusSubscriber(modid = "conquest")
public class ArmsStationInit {

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.BLOCK, helper -> helper.register(ArmsStation.ID, ArmsStation.createBlock()));
        event.register(Registries.ITEM, helper -> helper.register(ArmsStation.ID, ArmsStation.createItem()));
        event.register(Registries.MENU, helper -> helper.register(ArmsStation.ID, ArmsStation.createMenu()));
        event.register(Registries.RECIPE_TYPE, helper -> helper.register(ArmsStation.ID, ArmsStation.RECIPE_TYPE));
        event.register(Registries.RECIPE_SERIALIZER, helper -> helper.register(ArmsStation.ID, ArmsStation.RECIPE_SERIALIZER));
    }

    /** Installed from {@code RefabricatedMod} once the payload type is registered. */
    public static void installPayloadSender() {
        ArmsStationNetwork.setSender(PacketDistributor::sendToPlayer);
    }
}
