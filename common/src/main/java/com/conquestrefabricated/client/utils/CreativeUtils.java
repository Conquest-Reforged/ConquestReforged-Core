package com.conquestrefabricated.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeInventoryListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public class CreativeUtils {

    public static boolean replaceItemStack(ItemStack original, ItemStack stack) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.getAbilities().instabuild) {
            return false;
        }

//        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.level().registryAccess());
//        try {
//            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
//            ItemStack decoded = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
//            System.out.println("Round-trip OK: " + decoded);
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }

        int slot = player.getInventory().findSlotMatchingItem(original);
        CreativeInventoryListener listener = new CreativeInventoryListener(Minecraft.getInstance());
        player.inventoryMenu.addSlotListener(listener);
        player.getInventory().setItem(slot, stack);

//        System.out.println("matched inventory slot = " + slot);
//
//        ServerboundSetCreativeModeSlotPacket packet = new ServerboundSetCreativeModeSlotPacket(slot, stack);
//        try {
//            ServerboundSetCreativeModeSlotPacket.STREAM_CODEC.encode(buf, packet);
//            ServerboundSetCreativeModeSlotPacket decoded = ServerboundSetCreativeModeSlotPacket.STREAM_CODEC.decode(buf);
//            System.out.println("Packet round-trip OK, slot=" + decoded.slotNum());
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }

        player.inventoryMenu.broadcastChanges();
        player.inventoryMenu.removeSlotListener(listener);
        return true;
    }

    public static boolean addItemStack(ItemStack stack) {
        return addItemStack(stack, false);
    }

    public static boolean addItemStack(ItemStack stack, boolean pick) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.getAbilities().instabuild) {
            return false;
        }

        int slot = player.getInventory().getSuitableHotbarSlot();
        CreativeInventoryListener listener = new CreativeInventoryListener(Minecraft.getInstance());
        player.inventoryMenu.addSlotListener(listener);
        player.getInventory().setItem(slot, stack);
        player.inventoryMenu.broadcastChanges();
        player.inventoryMenu.removeSlotListener(listener);
        if (pick) {
            player.getInventory().setSelectedSlot(slot);
        }
        return true;
    }
}
