package com.conquestrefabricated.client.events;

import com.conquestrefabricated.core.item.ItemUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
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

    public static ItemStack onPick(Player player, BlockPos pos, BlockState state) {
        if (!isControlDown()) {
            return null;
        }

        if (player == null || !player.getAbilities().instabuild) {
            return null;
        }

        HitResult result = Minecraft.getInstance().hitResult;
        if (result == null) {
            return null;
        }

        if (result.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        //if (state.hasBlockEntity()) {
        //    return ItemStack.EMPTY;
        //}

        ItemStack stack;
        if (isAltDown()) {
            stack = ItemUtils.fromState(state);
        } else {
            stack = ItemUtils.fromStateNoFacing(state);
        }

        player.getInventory().addAndPickItem(stack);
        Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(player.getItemInHand(InteractionHand.MAIN_HAND), 36 + player.getInventory().getSelectedSlot());
        return stack;
    }
}
