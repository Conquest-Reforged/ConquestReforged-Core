package com.conquestrefabricated.content.arms;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * The results the open arms station can currently produce, pushed to the client whenever the server
 * recalculates them.
 *
 * <p>Vanilla gets away without this for the stonecutter because stonecutting recipes are part of the
 * data the server syncs to every client. Modded recipe types are not synced at all, so the arms
 * station ships its own already-assembled preview stacks for the recipe picker to draw.</p>
 */
public record ArmsStationOptionsPayload(int containerId, List<ItemStack> options) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ArmsStationOptionsPayload> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ArmsStation.NAMESPACE, "arms_station_options"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArmsStationOptionsPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ArmsStationOptionsPayload::containerId,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), ArmsStationOptionsPayload::options,
            ArmsStationOptionsPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }

}
