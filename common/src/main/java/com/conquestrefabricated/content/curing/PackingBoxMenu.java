package com.conquestrefabricated.content.curing;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * The screen of a packing crate: three rows of storage, laid out like a chest. Anything can go in; meat
 * and fish sitting next to salt are cured in place, and the screen is told how far each slot has got.
 */
public class PackingBoxMenu extends AbstractContainerMenu {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "packing_box");

    /** Set by {@link #create()} during menu registration. */
    public static MenuType<PackingBoxMenu> TYPE;

    private static final int SLOTS = CuringVesselBlockEntity.SLOTS;
    private static final int END = SLOTS + 36;

    private final ContainerData data;
    private final @Nullable CuringVesselBlockEntity box;

    /** Builds the menu type. Call only while the menu registry is open, then register the result. */
    public static MenuType<PackingBoxMenu> create() {
        TYPE = new MenuType<>(PackingBoxMenu::new, FeatureFlags.DEFAULT_FLAGS);
        return TYPE;
    }

    /** The client's: it is handed the slots and the data over the wire. */
    public PackingBoxMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(SLOTS), new SimpleContainerData(SLOTS), null);
    }

    public PackingBoxMenu(int containerId, Inventory inventory, Container container, ContainerData data,
                          @Nullable CuringVesselBlockEntity box) {
        super(TYPE, containerId);
        checkContainerSize(container, SLOTS);
        checkContainerDataCount(data, SLOTS);
        this.data = data;
        this.box = box;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(container, column + row * 9, 8 + column * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 85 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 143));
        }
        this.addDataSlots(data);
    }

    /** What a crate slot is doing: see the {@code STATE_} values on {@link CuringVesselBlockEntity}. */
    public int state(int slot) {
        return slot >= 0 && slot < SLOTS ? this.data.get(slot) : CuringVesselBlockEntity.STATE_NONE;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.box == null || this.box.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack before = stack.copy();

        boolean moved = index < SLOTS
                ? this.moveItemStackTo(stack, SLOTS, END, true)
                : this.moveItemStackTo(stack, 0, SLOTS, false);
        if (!moved) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return stack.getCount() == before.getCount() ? ItemStack.EMPTY : before;
    }
}
