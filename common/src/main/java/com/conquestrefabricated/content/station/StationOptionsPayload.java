package com.conquestrefabricated.content.station;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * The results the open crafting station can currently produce, pushed to the client whenever the
 * server recalculates them.
 *
 * <p>Vanilla gets away without this for the stonecutter because stonecutting recipes are part of the
 * data the server syncs to every client. Modded recipe types are not synced at all, so the stations
 * ship their own already-assembled preview stacks for the recipe picker to draw.</p>
 */
public record StationOptionsPayload(int containerId, List<ItemStack> options) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StationOptionsPayload> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "station_options"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StationOptionsPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, StationOptionsPayload::containerId,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), StationOptionsPayload::options,
            StationOptionsPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
