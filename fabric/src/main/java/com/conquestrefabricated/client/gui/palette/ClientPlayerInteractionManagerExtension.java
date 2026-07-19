package com.conquestrefabricated.client.gui.palette;

import net.minecraft.world.item.ItemStack;

public interface  ClientPlayerInteractionManagerExtension {
    void clickStack(ItemStack stack, int slotId);
    void dropStack(ItemStack stack, Action action);
    void decrementStack(ItemStack stack, int slotId);

    enum Action {
        DROP,           // Drop item on ground
        TO_HOTBAR,      // Place in hotbar
        TO_INVENTORY,    // Place in inventory
        DECREMENT    // Place in inventory
    }
}
