package com.conquestrefabricated.content.cauldron;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * The cooking screen of a cauldron: three ingredient slots, fuel, and what it made, with a flame, an
 * arrow, and a gauge of what is in the pot.
 *
 * <p>Like a furnace's, the slots belong to the cauldron rather than the menu, so closing it hands
 * nothing back and the pot carries on cooking on its own. What the screen needs besides the slots
 * - the fire, the progress, the water - comes across as data slots.</p>
 */
public class CauldronMenu extends AbstractContainerMenu {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "cauldron");

    /** Set by {@link #create()} during menu registration. */
    public static MenuType<CauldronMenu> TYPE;

    public static final int DATA_LIT_TIME = 0;
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_PROGRESS = 2;
    public static final int DATA_TOTAL = 3;
    public static final int DATA_FLUID_LEVEL = 4;
    public static final int DATA_BRINE = 5;
    public static final int DATA_COUNT = 6;

    private static final int INVENTORY_START = CauldronData.SIZE;
    private static final int HOTBAR_START = INVENTORY_START + 27;
    private static final int END = HOTBAR_START + 9;

    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final @Nullable CauldronData backing;
    private final Inventory inventory;

    /** Builds the menu type. Call only while the menu registry is open, then register the result. */
    public static MenuType<CauldronMenu> create() {
        TYPE = new MenuType<>(CauldronMenu::new, FeatureFlags.DEFAULT_FLAGS);
        return TYPE;
    }

    /** The client's: it is handed the slots and the data over the wire. */
    public CauldronMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(CauldronData.SIZE), new SimpleContainerData(DATA_COUNT),
                ContainerLevelAccess.NULL, null);
    }

    /** The server's, over the cauldron at {@code pos}. */
    public CauldronMenu(int containerId, Inventory inventory, ServerLevel level, BlockPos pos, CauldronData backing) {
        this(containerId, inventory, backing, live(level, pos, backing), ContainerLevelAccess.create(level, pos), backing);
    }

    private CauldronMenu(int containerId, Inventory inventory, Container container, ContainerData data,
                         ContainerLevelAccess access, @Nullable CauldronData backing) {
        super(TYPE, containerId);
        checkContainerSize(container, CauldronData.SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;
        this.access = access;
        this.backing = backing;
        this.inventory = inventory;

        for (int column = 0; column < CauldronData.INGREDIENT_SLOTS; column++) {
            this.addSlot(new Slot(container, column, 30 + column * 18, 17));
        }
        this.addSlot(new Slot(container, CauldronData.FUEL_SLOT, 48, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CauldronMenu.this.isFuel(stack);
            }
        });
        this.addSlot(new Slot(container, CauldronData.OUTPUT_SLOT, 136, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
        this.addDataSlots(data);
    }

    /** The data slots, read live from the cauldron and the block it is. */
    private static ContainerData live(ServerLevel level, BlockPos pos, CauldronData backing) {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case DATA_LIT_TIME -> backing.litTime;
                    case DATA_LIT_DURATION -> backing.litDuration;
                    case DATA_PROGRESS -> backing.progress;
                    case DATA_TOTAL -> backing.total;
                    case DATA_FLUID_LEVEL -> CauldronBlocks.fluidLevel(level.getBlockState(pos));
                    case DATA_BRINE -> backing.brine ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }

    private boolean isFuel(ItemStack stack) {
        return this.inventory.player.level().fuelValues().isFuel(stack);
    }

    public boolean isLit() {
        return this.data.get(DATA_LIT_TIME) > 0;
    }

    /** How much of the flame is left, 0 to 1. */
    public float litProgress() {
        int duration = this.data.get(DATA_LIT_DURATION);
        return duration <= 0 ? 0.0F : Math.clamp(this.data.get(DATA_LIT_TIME) / (float) duration, 0.0F, 1.0F);
    }

    /** How far the cooking has got, 0 to 1. */
    public float burnProgress() {
        int total = this.data.get(DATA_TOTAL);
        int progress = this.data.get(DATA_PROGRESS);
        return total <= 0 || progress <= 0 ? 0.0F : Math.clamp(progress / (float) total, 0.0F, 1.0F);
    }

    /** Levels of water in the pot, 0 to 3. */
    public int fluidLevel() {
        return this.data.get(DATA_FLUID_LEVEL);
    }

    public boolean isBrine() {
        return this.data.get(DATA_BRINE) != 0;
    }

    @Override
    public void broadcastChanges() {
        if (this.backing != null) {
            // Someone has it open: it is not to be forgotten as untouched.
            this.backing.idleFor = 0;
        }
        super.broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.access.evaluate((level, pos) -> CauldronBlocks.cookable(level.getBlockState(pos))
                && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0, true);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack before = stack.copy();

        if (index < INVENTORY_START) {
            if (!this.moveItemStackTo(stack, INVENTORY_START, END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (this.isFuel(stack) && this.moveItemStackTo(stack, CauldronData.FUEL_SLOT, CauldronData.FUEL_SLOT + 1, false)) {
            // Into the fire.
        } else if (!this.moveItemStackTo(stack, 0, CauldronData.INGREDIENT_SLOTS, false)) {
            boolean inHotbar = index >= HOTBAR_START;
            if (!(inHotbar ? this.moveItemStackTo(stack, INVENTORY_START, HOTBAR_START, false)
                    : this.moveItemStackTo(stack, HOTBAR_START, END, false))) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == before.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return before;
    }
}
