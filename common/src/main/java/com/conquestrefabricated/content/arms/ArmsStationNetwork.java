package com.conquestrefabricated.content.arms;

import net.minecraft.server.level.ServerPlayer;

/**
 * Loader-agnostic seam for pushing {@link ArmsStationOptionsPayload}s. Each platform installs its
 * own sender during mod init; until then sends are dropped, which is what we want for
 * data generation and unit-test style environments where there is no network at all.
 */
public final class ArmsStationNetwork {

    @FunctionalInterface
    public interface Sender {
        void send(ServerPlayer player, ArmsStationOptionsPayload payload);
    }

    private static Sender sender = (player, payload) -> {
    };

    private ArmsStationNetwork() {
    }

    public static void setSender(Sender sender) {
        ArmsStationNetwork.sender = sender;
    }

    public static void send(ServerPlayer player, ArmsStationOptionsPayload payload) {
        sender.send(player, payload);
    }
}
