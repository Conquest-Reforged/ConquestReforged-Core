package com.conquestrefabricated.content.station;

import net.minecraft.server.level.ServerPlayer;

/**
 * Loader-agnostic seam for pushing {@link StationOptionsPayload}s. Each platform installs its own
 * sender during mod init; until then sends are dropped, which is what we want for data generation
 * and unit-test style environments where there is no network at all.
 */
public final class StationNetwork {

    @FunctionalInterface
    public interface Sender {
        void send(ServerPlayer player, StationOptionsPayload payload);
    }

    private static Sender sender = (player, payload) -> {
    };

    private StationNetwork() {
    }

    public static void setSender(Sender sender) {
        StationNetwork.sender = sender;
    }

    public static void send(ServerPlayer player, StationOptionsPayload payload) {
        sender.send(player, payload);
    }
}
