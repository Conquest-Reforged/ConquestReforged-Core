package com.conquestrefabricated.client.gui.arms;

import com.conquestrefabricated.content.arms.ArmsStationMenu;
import com.conquestrefabricated.content.arms.ArmsStationOptionsPayload;
import net.minecraft.client.Minecraft;

/** Client half of the arms station: applies option payloads to the menu the player has open. */
public final class ArmsStationClient {

    private ArmsStationClient() {
    }

    /**
     * Hands {@code payload} to the open arms station menu. No-ops if the player has already closed
     * or replaced it, which happens routinely when a payload is in flight as the screen closes.
     *
     * <p>Must be called on the client thread - both loaders give their payload handlers a way to
     * defer onto it.</p>
     */
    public static void applyOptions(ArmsStationOptionsPayload payload) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }
        if (client.player.containerMenu instanceof ArmsStationMenu menu && menu.containerId == payload.containerId()) {
            menu.setClientOptions(payload.options());
        }
    }
}
