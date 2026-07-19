package com.conquestrefabricated.client.gui.config.neoforge;

import com.conquestrefabricated.RefabricatedMod;
import com.conquestrefabricated.client.gui.config.ConquestConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ConfigSyncPacket(boolean plantSlowness, boolean plantBreaking, boolean passThroughLeaves) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ConfigSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("conquest", "config_sync"));

    public static final StreamCodec<FriendlyByteBuf, ConfigSyncPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeBoolean(packet.plantSlowness);
                buf.writeBoolean(packet.plantBreaking);
                buf.writeBoolean(packet.passThroughLeaves);
            },
            buf -> new ConfigSyncPacket(buf.readBoolean(), buf.readBoolean(), buf.readBoolean())
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ConfigSyncPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().isSingleplayer()) {
                return;
            }

            RefabricatedMod.localPlantSlowness = ConquestConfig.INSTANCE.plantSlowness.get();
            RefabricatedMod.localPlantBreaking = ConquestConfig.INSTANCE.plantBreaking.get();
            RefabricatedMod.localPassThroughLeaves = ConquestConfig.INSTANCE.passThroughLeaves.get();

            ConquestConfig.INSTANCE.plantSlowness.set(packet.plantSlowness);
            ConquestConfig.INSTANCE.plantBreaking.set(packet.plantBreaking);
            ConquestConfig.INSTANCE.passThroughLeaves.set(packet.passThroughLeaves);
        });
    }
}