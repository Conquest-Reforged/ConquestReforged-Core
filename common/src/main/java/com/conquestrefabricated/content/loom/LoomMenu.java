package com.conquestrefabricated.content.loom;

import com.conquestrefabricated.content.blocks.tileentity.loom.LoomBlockEntity;
import com.conquestrefabricated.content.station.StationMenu;
import com.conquestrefabricated.content.station.StationRecipes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The loom's picker. Unlike the arms station and the crafting tools, the slots here belong to the
 * block rather than to the menu: a loom keeps its material, keeps weaving and keeps showing its
 * weave with nobody stood at it, so closing the screen must not hand anything back.
 *
 * <p>Picking an option only highlights it; the confirm control is what tells the block entity to
 * start. Everything after that - the ticking, the crafting, the weave - happens in
 * {@link LoomBlockEntity}, which is what lets a loom finish a job while the player walks away.</p>
 */
public class LoomMenu extends StationMenu<WeavingRecipe, SingleRecipeInput> {

    /**
     * Button id the confirm control sends. Sits just past the picker's variant toggle, and far above
     * any option index.
     */
    public static final int CONFIRM_BUTTON = StationMenu.TOGGLE_VARIANTS_BUTTON + 1;

    private final Container loom;
    private final ContainerData data;
    private final Slot inputSlot;

    /**
     * Whether the highlighted option is the job the loom is actually running, so the screen can draw
     * the confirm control as a stop. The client cannot work this out for itself - it never learns
     * which recipe the block entity holds - so the server keeps it in a slot of its own.
     */
    private final DataSlot activeSelection = DataSlot.standalone();

    /** Client-side constructor: an empty stand-in the server then fills in over the wire. */
    public LoomMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null, new SimpleContainerData(LoomBlockEntity.DATA_COUNT));
    }

    public LoomMenu(int containerId, Inventory inventory, @Nullable Container loom, ContainerData data) {
        super(LoomStation.MENU, containerId, inventory, accessOf(loom));
        checkContainerDataCount(data, LoomBlockEntity.DATA_COUNT);

        // With no block behind it the menu still needs somewhere to hold the slots the server sends,
        // and that stand-in is what tells the screen a slot arrived - the block entity, being the
        // server's, has no reason to.
        Container container = loom != null ? loom : new SimpleContainer(LoomBlockEntity.SLOT_COUNT) {
            @Override
            public void setChanged() {
                super.setChanged();
                LoomMenu.this.slotsChanged(this);
                LoomMenu.this.notifyScreen();
            }
        };
        checkContainerSize(container, LoomBlockEntity.SLOT_COUNT);

        this.loom = container;
        this.data = data;
        this.inputSlot = this.addSlot(new Slot(container, LoomBlockEntity.INPUT_SLOT, 20, 33));
        this.addSlot(new Slot(container, LoomBlockEntity.OUTPUT_SLOT, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player, stack.getCount());
                LoomMenu.this.playTakeSound(player);
                super.onTake(player, stack);
            }
        });
        this.addPlayerInventory(inventory);
        this.addDataSlots(data);
        this.addDataSlot(this.activeSelection);
    }

    private static ContainerLevelAccess accessOf(@Nullable Container loom) {
        return loom instanceof BlockEntity blockEntity && blockEntity.getLevel() != null
                ? ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos())
                : ContainerLevelAccess.NULL;
    }

    @Override
    protected RecipeType<WeavingRecipe> recipeType() {
        return LoomStation.RECIPE_TYPE;
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
     * A loom works anything it can weave, plus anything it wove in the first place - otherwise you
     * could weave a bolt of canvas and then have no way to cut it into layers, because nothing takes
     * finished canvas as an ingredient.
     */
    @Override
    protected boolean worksWith(ItemStack input) {
        return super.worksWith(input)
                || StationRecipes.produces(this.level, this.recipeType(), this::accepts, input,
                        this.emptyRecipeInput());
    }

    /**
     * Picking an option only highlights it. Unlike the other stations, where the result slot is a
     * preview you can ignore, a loom picking up a new job the moment you brush past an icon would
     * throw away the one it is part way through - so starting one is a separate, deliberate click.
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

    /** Whether the highlighted option is the job the loom is running, so confirm reads as a stop. */
    public boolean isSelectionActive() {
        return this.activeSelection.get() != 0;
    }

    /**
     * Starts the loom on whatever is highlighted, or stops it if that is already what it is doing.
     * Server side.
     *
     * <p>Stopping throws away the part-done craft rather than banking it, which is the same bargain
     * a furnace makes when you pull its input back out.</p>
     */
    private void confirmSelection() {
        List<Option> options = this.options();
        int index = this.getSelectedRecipeIndex();
        if (index < 0 || index >= options.size() || !(this.loom instanceof LoomBlockEntity blockEntity)) {
            return;
        }
        ResourceKey<Recipe<?>> picked = options.get(index).used().id();
        blockEntity.setSelectedRecipe(picked.equals(blockEntity.getSelectedRecipe()) ? null : picked);
    }

    /** Server side: whether the block entity is working on the option the picker has highlighted. */
    private boolean selectionIsRunning() {
        if (!(this.loom instanceof LoomBlockEntity blockEntity)) {
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

    /** Reopening a loom should show what it is part way through, not an empty picker. */
    @Override
    protected int preferredSelection(List<Option> options) {
        if (!(this.loom instanceof LoomBlockEntity blockEntity)) {
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
    public int getWeaveProgress() {
        return this.data.get(LoomBlockEntity.DATA_PROGRESS);
    }

    /** Ticks the current craft takes in total, or 0 when the loom is not weaving anything. */
    public int getWeaveDuration() {
        return this.data.get(LoomBlockEntity.DATA_DURATION);
    }

    @Override
    protected SoundEvent takeResultSound() {
        return SoundEvents.UI_LOOM_TAKE_RESULT;
    }

    @Override
    public MenuType<?> getType() {
        return LoomStation.MENU;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.loom.stillValid(player);
    }
}
