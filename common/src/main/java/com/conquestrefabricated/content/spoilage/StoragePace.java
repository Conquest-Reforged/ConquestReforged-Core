package com.conquestrefabricated.content.spoilage;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * How fast food ages in the container a player has just opened, so tooltips can say so.
 *
 * <p>The client cannot work this out: a chest's screen does not know where the chest is. The server does,
 * and sends the pace for each {@link SpoilClass} - in the order of {@link SpoilClass#values()} - along
 * with the kind of place it is. A {@code place} of -1 means no container is open, and clears it.</p>
 *
 * @param place {@link SpoilageEnvironment.Place#ordinal()}, or -1
 * @param rates the pace for each class, 1.0 being plains, in the open, in an ordinary container
 */
public record StoragePace(int place, List<Double> rates) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StoragePace> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "storage_pace"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StoragePace> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, StoragePace::place,
            ByteBufCodecs.DOUBLE.apply(ByteBufCodecs.list()), StoragePace::rates,
            StoragePace::new);

    /** Nothing open. */
    public static final StoragePace NONE = new StoragePace(-1, List.of());

    public boolean isNone() {
        return this.place < 0;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
