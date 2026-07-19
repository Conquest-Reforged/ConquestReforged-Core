package com.conquestrefabricated.client.gui.palette;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record PaletteItemPacket(int slot, ItemStack stack, ClientPlayerInteractionManagerExtension.Action action) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PaletteItemPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("conquest", "palette_item"));

    private static final StreamCodec<RegistryFriendlyByteBuf, ClientPlayerInteractionManagerExtension.Action> ACTION_CODEC =
            ByteBufCodecs.VAR_INT.<RegistryFriendlyByteBuf>cast().map(
                    i -> ClientPlayerInteractionManagerExtension.Action.values()[i],
                    Enum::ordinal
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, PaletteItemPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PaletteItemPacket::slot,
            ItemStack.STREAM_CODEC, PaletteItemPacket::stack,
            ACTION_CODEC, PaletteItemPacket::action,
            PaletteItemPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}