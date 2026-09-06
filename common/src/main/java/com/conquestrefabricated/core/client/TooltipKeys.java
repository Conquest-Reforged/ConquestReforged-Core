package com.conquestrefabricated.core.client;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;

/**
 * Modifier-key state for tooltips.
 * <p>
 * Tooltips are built in common code, but the key state only exists on the client. The lookup is
 * routed through {@link EnvExecutor} so the client class is never loaded on a dedicated server,
 * where this simply reports "not held".
 */
public final class TooltipKeys {

    private TooltipKeys() {
    }

    /**
     * @return true when the key that expands collapsed tooltip text is held
     */
    public static boolean isExpandDown() {
        return EnvExecutor.getInEnv(Env.CLIENT, () -> Client::isExpandDown).orElse(false);
    }

    /**
     * Loaded only on the client — see {@link TooltipKeys#isExpandDown()}.
     */
    private static final class Client {

        private static boolean isExpandDown() {
            // 26.1 moved this off Screen and onto the Minecraft instance
            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            return minecraft != null && minecraft.hasShiftDown();
        }
    }
}
