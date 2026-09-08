package com.conquestrefabricated.content.station;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import java.util.List;

/**
 * A crafting station that works exactly as the stonecutter does: picking an option puts a preview in
 * the result slot, and taking that preview is what spends the input.
 *
 * <p>The container belongs to the menu rather than to any block, so whatever is in the input slot is
 * handed back when the screen closes. That suits both the arms station, where the station is a
 * workbench you stand at, and the crafting tools, which are held.</p>
 *
 * @param <R> the recipe type this station crafts with
 */
public abstract class PreviewStationMenu<R extends Recipe<SingleRecipeInput>> extends StationMenu<R> {

    protected final Slot inputSlot;
    protected final Slot resultSlot;

    public final Container container = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            PreviewStationMenu.this.slotsChanged(this);
            PreviewStationMenu.this.notifyScreen();
        }
    };

    private final ResultContainer resultContainer = new ResultContainer();

    protected PreviewStationMenu(MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(menuType, containerId, inventory, access);
        this.inputSlot = this.addSlot(new Slot(this.container, INPUT_SLOT, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, RESULT_SLOT, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player, stack.getCount());
                PreviewStationMenu.this.resultContainer.awardUsedRecipes(player, this.getRelevantItems());
                ItemStack remaining = PreviewStationMenu.this.inputSlot.remove(1);
                if (!remaining.isEmpty()) {
                    PreviewStationMenu.this.setupResultSlot(PreviewStationMenu.this.getSelectedRecipeIndex());
                }

                PreviewStationMenu.this.playTakeSound(player);
                super.onTake(player, stack);
            }

            private List<ItemStack> getRelevantItems() {
                return List.of(PreviewStationMenu.this.inputSlot.getItem());
            }
        });
        this.addStandardInventorySlots(inventory, 8, 84);
    }

    @Override
    protected ItemStack stationInput() {
        return this.inputSlot.getItem();
    }

    @Override
    protected void clearSelection() {
        this.resultSlot.set(ItemStack.EMPTY);
        this.resultContainer.setRecipeUsed(null);
    }

    @Override
    protected void selectOption(int index) {
        this.setupResultSlot(index);
    }

    private void setupResultSlot(int index) {
        if (this.level.isClientSide()) {
            return;
        }

        List<Option> options = this.options();
        if (this.isValidRecipeIndex(index) && index < options.size()) {
            Option option = options.get(index);
            this.resultContainer.setRecipeUsed(option.used());
            this.resultSlot.set(option.result().copy());
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
            this.resultContainer.setRecipeUsed(null);
        }

        this.broadcastChanges();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(RESULT_SLOT);
        if (this.access == ContainerLevelAccess.NULL) {
            this.clearContainer(player, this.container);
        } else {
            this.access.execute((level, pos) -> this.clearContainer(player, this.container));
        }
    }
}
