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
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The loom's picker. Unlike the arms station and the crafting tools, the slots here belong to the
 * block rather than to the menu: a loom keeps its material, keeps weaving and keeps showing its
 * weave with nobody stood at it, so closing the screen must not hand anything back.
 *
 * <p>Picking an option only tells the block entity what to work on. Everything after that - the
 * ticking, the crafting, the weave - happens in {@link LoomBlockEntity}, which is what lets a loom
 * finish a job while the player walks away.</p>
 */
public class LoomMenu extends StationMenu<WeavingRecipe> {

    private final Container loom;
    private final ContainerData data;
    private final Slot inputSlot;

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
        this.addStandardInventorySlots(inventory, 8, 84);
        this.addDataSlots(data);
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
    public boolean supportsVariants() {
        return true;
    }

    /**
     * A loom works anything it can weave, plus anything it wove in the first place - otherwise you
     * could weave a bolt of canvas and then have no way to cut it into layers, because nothing takes
     * finished canvas as an ingredient.
     */
    @Override
    protected boolean worksWith(ItemStack input, List<RecipeHolder<WeavingRecipe>> direct) {
        return super.worksWith(input, direct)
                || StationRecipes.produces(this.level, this.recipeType(), this::accepts, input);
    }

    /** Picking an option just points the loom at a recipe; the block entity does the rest. */
    @Override
    protected void selectOption(int index) {
        List<Option> options = this.options();
        if (index < 0 || index >= options.size() || !(this.loom instanceof LoomBlockEntity blockEntity)) {
            return;
        }
        blockEntity.setSelectedRecipe(options.get(index).used().id());
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
