package com.conquestrefabricated.content.arms;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The arms station container: an input slot, a result slot and a picker listing every
 * {@link ArmsStationRecipe} that accepts the current input.
 *
 * <p>Modelled on {@code StonecutterMenu}, with one structural difference. The stonecutter can
 * recompute its option list on both sides because vanilla syncs stonecutting recipes to clients;
 * modded recipe types are never synced, so here the server owns the list and pushes the assembled
 * preview stacks over with {@link ArmsStationOptionsPayload}. The client therefore never derives
 * options itself, it only renders what it was told.</p>
 */
public class ArmsStationMenu extends AbstractContainerMenu {

    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    private static final int INV_SLOT_START = 2;
    private static final int INV_SLOT_END = 29;
    private static final int USE_ROW_SLOT_START = 29;
    private static final int USE_ROW_SLOT_END = 38;

    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    private final @Nullable ServerPlayer owner;

    /** Server side: the matching recipes. Always empty on the client. */
    private List<RecipeHolder<ArmsStationRecipe>> recipesForInput = List.of();
    /** The assembled previews for the picker. Filled in locally on the server, by payload on the client. */
    private List<ItemStack> optionIcons = List.of();

    private ItemStack input = ItemStack.EMPTY;
    private long lastSoundTime;
    final Slot inputSlot;
    final Slot resultSlot;
    private Runnable slotUpdateListener = () -> {
    };

    public final Container container = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            ArmsStationMenu.this.slotsChanged(this);
            ArmsStationMenu.this.slotUpdateListener.run();
        }
    };

    private final ResultContainer resultContainer = new ResultContainer();

    public ArmsStationMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public ArmsStationMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ArmsStation.MENU, containerId);
        this.access = access;
        this.level = inventory.player.level();
        this.owner = inventory.player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        this.inputSlot = this.addSlot(new Slot(this.container, INPUT_SLOT, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, RESULT_SLOT, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player, stack.getCount());
                ArmsStationMenu.this.resultContainer.awardUsedRecipes(player, this.getRelevantItems());
                ItemStack remaining = ArmsStationMenu.this.inputSlot.remove(1);
                if (!remaining.isEmpty()) {
                    ArmsStationMenu.this.setupResultSlot(ArmsStationMenu.this.selectedRecipeIndex.get());
                }

                access.execute((level, pos) -> {
                    long gameTime = level.getGameTime();
                    if (ArmsStationMenu.this.lastSoundTime != gameTime) {
                        level.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        ArmsStationMenu.this.lastSoundTime = gameTime;
                    }
                });
                super.onTake(player, stack);
            }

            private List<ItemStack> getRelevantItems() {
                return List.of(ArmsStationMenu.this.inputSlot.getItem());
            }
        });
        this.addStandardInventorySlots(inventory, 8, 84);
        this.addDataSlot(this.selectedRecipeIndex);
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    /** The stacks the recipe picker should draw, in selection order. */
    public List<ItemStack> getOptionIcons() {
        return this.optionIcons;
    }

    public int getNumberOfVisibleRecipes() {
        return this.optionIcons.size();
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.optionIcons.isEmpty();
    }

    /** Called by the client payload handler when the server sends a new option list. */
    public void setClientOptions(List<ItemStack> options) {
        this.optionIcons = List.copyOf(options);
        if (!this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            this.selectedRecipeIndex.set(-1);
        }
        this.slotUpdateListener.run();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ArmsStation.BLOCK);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (this.selectedRecipeIndex.get() == buttonId) {
            return false;
        }

        if (this.isValidRecipeIndex(buttonId)) {
            this.selectedRecipeIndex.set(buttonId);
            this.setupResultSlot(buttonId);
        }

        return true;
    }

    private boolean isValidRecipeIndex(int buttonId) {
        return buttonId >= 0 && buttonId < this.optionIcons.size();
    }

    @Override
    public void slotsChanged(Container container) {
        ItemStack stack = this.inputSlot.getItem();
        if (!stack.is(this.input.getItem())) {
            this.input = stack.copy();
            this.setupRecipeList(stack);
        }
    }

    private void setupRecipeList(ItemStack stack) {
        if (this.level.isClientSide()) {
            // Only the server can resolve modded recipes, and it pushes a fresh option list right
            // after this runs, so leave the current one on screen until that arrives.
            return;
        }

        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        this.resultContainer.setRecipeUsed(null);
        this.recipesForInput = ArmsStation.recipesFor(this.level, stack);

        SingleRecipeInput recipeInput = new SingleRecipeInput(stack);
        List<ItemStack> icons = new ArrayList<>(this.recipesForInput.size());
        for (RecipeHolder<ArmsStationRecipe> holder : this.recipesForInput) {
            icons.add(holder.value().assemble(recipeInput));
        }
        this.optionIcons = List.copyOf(icons);

        if (this.owner != null) {
            ArmsStationNetwork.send(this.owner, new ArmsStationOptionsPayload(this.containerId, this.optionIcons));
        }
    }

    private void setupResultSlot(int index) {
        if (this.level.isClientSide()) {
            return;
        }

        if (this.isValidRecipeIndex(index) && index < this.recipesForInput.size()) {
            RecipeHolder<ArmsStationRecipe> holder = this.recipesForInput.get(index);
            this.resultContainer.setRecipeUsed(holder);
            this.resultSlot.set(holder.value().assemble(new SingleRecipeInput(this.container.getItem(INPUT_SLOT))));
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
            this.resultContainer.setRecipeUsed(null);
        }

        this.broadcastChanges();
    }

    @Override
    public MenuType<?> getType() {
        return ArmsStation.MENU;
    }

    public void registerUpdateListener(Runnable slotUpdateListener) {
        this.slotUpdateListener = slotUpdateListener;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack carried, Slot target) {
        return target.container != this.resultContainer && super.canTakeItemForPickAll(carried, target);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            Item item = stack.getItem();
            clicked = stack.copy();
            if (slotIndex == RESULT_SLOT) {
                item.onCraftedBy(stack, player);
                if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, clicked);
            } else if (slotIndex == INPUT_SLOT) {
                if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.isClientSide()) {
                // Whether this item is a valid input is only knowable on the server, so don't
                // predict a move the server may well undo - wait for its authoritative update.
                return ItemStack.EMPTY;
            } else if (ArmsStation.isValidInput(this.level, stack)) {
                if (!this.moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= INV_SLOT_START && slotIndex < INV_SLOT_END) {
                if (!this.moveItemStackTo(stack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= USE_ROW_SLOT_START && slotIndex < USE_ROW_SLOT_END
                    && !this.moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (stack.getCount() == clicked.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
            if (slotIndex == RESULT_SLOT) {
                player.drop(stack, false);
            }

            this.broadcastChanges();
        }

        return clicked;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(RESULT_SLOT);
        this.access.execute((level, pos) -> this.clearContainer(player, this.container));
    }
}
