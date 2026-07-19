package com.conquestrefabricated.client.gui;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;


public abstract class AbstractContainer extends AbstractContainerMenu {

    protected AbstractContainer(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    @Override
    public Slot addSlot(Slot slot) {
        return super.addSlot(slot);
    }
}
