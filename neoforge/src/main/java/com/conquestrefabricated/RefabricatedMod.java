package com.conquestrefabricated;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.client.gui.arms.ArmsStationClient;
import com.conquestrefabricated.client.gui.config.neoforge.ConfigSyncPacket;
import com.conquestrefabricated.content.arms.ArmsStationOptionsPayload;
import com.conquestrefabricated.content.arms.neoforge.ArmsStationInit;
import com.conquestrefabricated.content.blocks.group.neoforge.ModGroupsEvent;
import com.conquestrefabricated.content.blocks.tileentity.neoforge.TileEntityTypesImpl;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod("conquest")
public class RefabricatedMod {

    public static Boolean localPlantSlowness;
    public static Boolean localPlantBreaking;
    public static Boolean localPassThroughLeaves;

    public RefabricatedMod(IEventBus modEventBus) {
        ConquestConfig.INSTANCE.getClass();

        modEventBus.addListener(this::registerPayloads);
        //modEventBus.register(ModGroupsEvent.class);

        NeoForge.EVENT_BUS.register(this);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0").optional();
        registrar.playToClient(
                ConfigSyncPacket.TYPE,
                ConfigSyncPacket.STREAM_CODEC,
                ConfigSyncPacket::handle
        );
        registrar.playToClient(
                ArmsStationOptionsPayload.ID,
                ArmsStationOptionsPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> ArmsStationClient.applyOptions(payload))
        );
        ArmsStationInit.installPayloadSender();
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ConfigSyncPacket packet = new ConfigSyncPacket(
                    ConquestConfig.INSTANCE.plantSlowness.get(),
                    ConquestConfig.INSTANCE.plantBreaking.get(),
                    ConquestConfig.INSTANCE.passThroughLeaves.get()
            );
            PacketDistributor.sendToPlayer(player, packet);
        }
    }
}