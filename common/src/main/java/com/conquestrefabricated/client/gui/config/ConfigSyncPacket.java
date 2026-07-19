package com.conquestrefabricated.client.gui.config;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ConfigSyncPacket(boolean plantSlowness, boolean plantBreaking, boolean passThroughLeaves) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ConfigSyncPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("conquest", "config_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ConfigSyncPacket::plantSlowness,
            ByteBufCodecs.BOOL, ConfigSyncPacket::plantBreaking,
            ByteBufCodecs.BOOL, ConfigSyncPacket::passThroughLeaves,
            ConfigSyncPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}