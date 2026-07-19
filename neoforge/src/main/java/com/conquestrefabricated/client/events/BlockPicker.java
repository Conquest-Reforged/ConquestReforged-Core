package com.conquestrefabricated.client.events;

import com.conquestrefabricated.core.item.ItemUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class BlockPicker {

    private static boolean isControlDown() {
        var window = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(window, InputConstants.KEY_LCONTROL)
                || InputConstants.isKeyDown(window, InputConstants.KEY_RCONTROL);
    }

    private static boolean isAltDown() {
        var window = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(window, InputConstants.KEY_LALT)
                || InputConstants.isKeyDown(window, InputConstants.KEY_RALT);
    }

    @SubscribeEvent
    public static void onPick(InputEvent.MouseButton.Pre event) {
        // Only trigger on middle mouse button (button 2)
        if (event.getButton() != 2) {
            return;
        }

        // Only trigger on button press, not release
        if (event.getAction() != 1) { // 1 = GLFW_PRESS
            return;
        }

        if (!isControlDown()) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null || !player.getAbilities().instabuild) {
            return;
        }

        HitResult result = Minecraft.getInstance().hitResult;
        if (result == null) {
            return;
        }

        if (result.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = ((BlockHitResult) result).getBlockPos();
        BlockState state = player.level().getBlockState(pos);
        if (state.hasBlockEntity()) {
            return;
        }

        ItemStack stack;
        if (isAltDown()) {
            stack = ItemUtils.fromState(state);
        } else {
            stack = ItemUtils.fromStateNoFacing(state);
        }

        Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(stack, 36 + player.getInventory().getSelectedSlot());
        event.setCanceled(true);
    }
}