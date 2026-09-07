package com.conquestrefabricated.client.gui.station;

import com.conquestrefabricated.content.station.StationMenu;
import com.conquestrefabricated.content.station.StationOptionsPayload;
import net.minecraft.client.Minecraft;

/** Client half of the crafting stations: applies option payloads to the menu the player has open. */
public final class StationClient {

    private StationClient() {
    }

    /**
     * Hands {@code payload} to the open station menu. No-ops if the player has already closed
     * or replaced it, which happens routinely when a payload is in flight as the screen closes.
     *
     * <p>Must be called on the client thread - both loaders give their payload handlers a way to
     * defer onto it.</p>
     */
    public static void applyOptions(StationOptionsPayload payload) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }
        if (client.player.containerMenu instanceof StationMenu<?> menu && menu.containerId == payload.containerId()) {
            menu.setClientOptions(payload.options());
        }
    }
}
