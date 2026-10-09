package com.conquestrefabricated.client.events;

import com.conquestrefabricated.core.item.ItemUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Ctrl + pick block in creative copies the block's state onto the item.
 *
 * <p>Fabric's pick event runs on the server, not the client thread that holds the keyboard, so Ctrl
 * comes from the event's own {@code includeData} flag (vanilla sends it for Ctrl) and Alt is sampled
 * once per client tick into a flag the server thread can read.</p>
 */
@Environment(EnvType.CLIENT)
public class BlockPicker {

    private static volatile boolean altDown;

    /** Starts sampling Alt; call once from client init. */
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var window = client.getWindow();
            altDown = InputConstants.isKeyDown(window, InputConstants.KEY_LALT)
                    || InputConstants.isKeyDown(window, InputConstants.KEY_RALT);
        });
    }

    public static ItemStack onPick(ServerPlayer player, BlockPos pos, BlockState state, boolean includeData) {
        if (!includeData || player == null || !player.getAbilities().instabuild) {
            return null;
        }

        // Fabric hands this to the server's own pick, which selects or fills the hotbar slot and syncs it.
        // Alt keeps the facing too; plain Ctrl leaves it to be set when placing.
        return altDown ? ItemUtils.fromState(state) : ItemUtils.fromStateNoFacing(state);
    }
}
