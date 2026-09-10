package com.conquestrefabricated.content.station;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The picker for a workstation you leave running.
 *
 * <p>Unlike the arms station and the crafting tools, the slots here belong to the block rather than
 * to the menu: the block keeps its material, keeps working and keeps showing what it is doing with
 * nobody stood at it, so closing the screen must not hand anything back.</p>
 *
 * <p>Picking an option only highlights it; the confirm control is what tells the block to start.
 * Everything after that happens in {@link WorkstationBlockEntity}, which is what lets a block finish
 * a job while the player walks away.</p>
 *
 * @param <R> the recipe type this station crafts with
 */
public abstract class WorkstationMenu<R extends TimedStationRecipe> extends StationMenu<R, SingleRecipeInput> {

    /**
     * Button id the confirm control sends. Sits just past the picker's variant toggle, and far above
     * any option index.
     */
    public static final int CONFIRM_BUTTON = StationMenu.TOGGLE_VARIANTS_BUTTON + 1;

    private final MenuType<?> menuType;
    private final Container station;
    private final ContainerData data;
    private final Slot inputSlot;

    /**
     * Whether the highlighted option is the job the block is actually running, so the screen can draw
     * the confirm control as a stop. The client cannot work this out for itself - it never learns
     * which recipe the block entity holds - so the server keeps it in a slot of its own.
     */
    private final DataSlot activeSelection = DataSlot.standalone();

    protected WorkstationMenu(MenuType<?> menuType, int containerId, Inventory inventory,
                              @Nullable Container station, ContainerData data) {
        super(menuType, containerId, inventory, accessOf(station));
        checkContainerDataCount(data, WorkstationBlockEntity.DATA_COUNT);
        this.menuType = menuType;

        // With no block behind it the menu still needs somewhere to hold the slots the server sends,
        // and that stand-in is what tells the screen a slot arrived - the block entity, being the
        // server's, has no reason to.
        Container container = station != null ? station : new SimpleContainer(WorkstationBlockEntity.SLOT_COUNT) {
            @Override
            public void setChanged() {
                super.setChanged();
                WorkstationMenu.this.slotsChanged(this);
                WorkstationMenu.this.notifyScreen();
            }
        };
        checkContainerSize(container, WorkstationBlockEntity.SLOT_COUNT);

        this.station = container;
        this.data = data;
        this.inputSlot = this.addSlot(new Slot(container, WorkstationBlockEntity.INPUT_SLOT, 20, 33));
        this.addSlot(new Slot(container, WorkstationBlockEntity.OUTPUT_SLOT, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player, stack.getCount());
                WorkstationMenu.this.playTakeSound(player);
                super.onTake(player, stack);
            }
        });
        this.addPlayerInventory(inventory);
        this.addDataSlots(data);
        this.addDataSlot(this.activeSelection);
    }

    /** A stand-in container and data for the client, which is handed the real contents over the wire. */
    protected static ContainerData clientData() {
        return new SimpleContainerData(WorkstationBlockEntity.DATA_COUNT);
    }

    private static ContainerLevelAccess accessOf(@Nullable Container station) {
        return station instanceof BlockEntity blockEntity && blockEntity.getLevel() != null
                ? ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos())
                : ContainerLevelAccess.NULL;
    }

    @Override
    protected ItemStack stationInput() {
        return this.inputSlot.getItem();
    }

    @Override
    protected SingleRecipeInput recipeInput() {
        return new SingleRecipeInput(this.inputSlot.getItem());
    }

    @Override
    protected SingleRecipeInput emptyRecipeInput() {
        return new SingleRecipeInput(ItemStack.EMPTY);
    }

    @Override
    protected SingleRecipeInput inputWith(ItemStack stack) {
        return new SingleRecipeInput(stack);
    }

    @Override
    public boolean supportsVariants() {
        return true;
    }

    /**
     * A workstation shapes anything it can work, plus anything it made in the first place - otherwise
     * you could weave a bolt of canvas and then have no way to cut it into layers, because nothing
     * takes finished canvas as an ingredient.
     */
    @Override
    protected boolean worksWith(ItemStack input) {
        return super.worksWith(input)
                || StationRecipes.produces(this.level, this.recipeType(), this::accepts, input,
                        this.emptyRecipeInput());
    }

    /**
     * Picking an option only highlights it. Unlike the other stations, where the result slot is a
     * preview you can ignore, a workstation picking up a new job the moment you brush past an icon
     * would throw away the one it is part way through - so starting one is a separate, deliberate
     * click.
     *
     * @see #confirmSelection()
     */
    @Override
    protected void selectOption(int index) {
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == CONFIRM_BUTTON) {
            if (!this.level.isClientSide()) {
                this.confirmSelection();
            }
            return true;
        }
        return super.clickMenuButton(player, buttonId);
    }

    /** Whether there is a highlighted option for the confirm control to start. */
    public boolean canConfirm() {
        return this.getSelectedRecipeIndex() >= 0 && this.getNumberOfVisibleRecipes() > 0;
    }

    /** Whether the highlighted option is the job it is running, so confirm reads as a stop. */
    public boolean isSelectionActive() {
        return this.activeSelection.get() != 0;
    }

    /**
     * Starts the block on whatever is highlighted, or stops it if that is already what it is doing.
     * Server side.
     *
     * <p>Stopping throws away the part-done craft rather than banking it, which is the same bargain
     * a furnace makes when you pull its input back out.</p>
     */
    private void confirmSelection() {
        List<Option> options = this.options();
        int index = this.getSelectedRecipeIndex();
        if (index < 0 || index >= options.size() || !(this.station instanceof WorkstationBlockEntity blockEntity)) {
            return;
        }
        ResourceKey<Recipe<?>> picked = options.get(index).used().id();
        blockEntity.setSelectedRecipe(picked.equals(blockEntity.getSelectedRecipe()) ? null : picked);
    }

    /** Server side: whether the block entity is working on the option the picker has highlighted. */
    private boolean selectionIsRunning() {
        if (!(this.station instanceof WorkstationBlockEntity blockEntity)) {
            return false;
        }
        ResourceKey<Recipe<?>> running = blockEntity.getSelectedRecipe();
        if (running == null) {
            return false;
        }
        List<Option> options = this.options();
        int index = this.getSelectedRecipeIndex();
        return index >= 0 && index < options.size() && options.get(index).used().id().equals(running);
    }

    /** Reopening one should show what it is part way through, not an empty picker. */
    @Override
    protected int preferredSelection(List<Option> options) {
        if (!(this.station instanceof WorkstationBlockEntity blockEntity)) {
            return -1;
        }
        ResourceKey<Recipe<?>> selected = blockEntity.getSelectedRecipe();
        if (selected == null) {
            return -1;
        }
        for (int index = 0; index < options.size(); index++) {
            if (options.get(index).used().id().equals(selected)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * The block entity's container has no way to tell the menu a slot moved - a hopper or a finished
     * craft changes it without anyone clicking - so the input is checked once a tick instead. The
     * option list is only actually rebuilt when the input has become a different item.
     */
    @Override
    public void broadcastChanges() {
        this.refreshOptions();
        if (!this.level.isClientSide()) {
            this.activeSelection.set(this.selectionIsRunning() ? 1 : 0);
        }
        super.broadcastChanges();
    }

    /** Ticks of the current craft that are done. */
    public int getCraftProgress() {
        return this.data.get(WorkstationBlockEntity.DATA_PROGRESS);
    }

    /** Ticks the current craft takes in total, or 0 when nothing is being made. */
    public int getCraftDuration() {
        return this.data.get(WorkstationBlockEntity.DATA_DURATION);
    }

    @Override
    public MenuType<?> getType() {
        return this.menuType;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.station.stillValid(player);
    }
}
