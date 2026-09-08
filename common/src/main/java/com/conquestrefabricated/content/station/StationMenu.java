package com.conquestrefabricated.content.station;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The recipe picker shared by Conquest's crafting stations: a list of everything one recipe type can
 * make from whatever is in the input slot, and the selection the player made from it.
 *
 * <p>Modelled on {@code StonecutterMenu}, with one structural difference. The stonecutter can
 * recompute its option list on both sides because vanilla syncs stonecutting recipes to clients;
 * modded recipe types are never synced, so here the server owns the list and pushes the assembled
 * preview stacks over with {@link StationOptionsPayload}. The client therefore never derives options
 * itself, it only renders what it was told.</p>
 *
 * <p>A station may also offer <i>variants</i>: the rest of the block family the input belongs to,
 * found by walking one stonecutting step from the input itself. The toggle under the input slot
 * switches between the two - base results or family shapes, never both at once - see
 * {@link #supportsVariants()}.</p>
 *
 * <p>This class deliberately owns no slots. What a station does with a selection differs too much
 * for one implementation: the arms station and the crafting tools show a preview you pull out of the
 * result slot ({@link PreviewStationMenu}), while a loom starts a craft that takes time and lands in
 * a slot belonging to the block. Subclasses add their own slots and implement {@link #selectOption};
 * everything above the slots is shared.</p>
 *
 * @param <R> the recipe type this station crafts with
 */
public abstract class StationMenu<R extends Recipe<SingleRecipeInput>> extends AbstractContainerMenu {

    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    protected static final int INV_SLOT_START = 2;
    protected static final int INV_SLOT_END = 29;
    protected static final int USE_ROW_SLOT_START = 29;
    protected static final int USE_ROW_SLOT_END = 38;

    /**
     * Button id the picker's variant toggle sends. Far above any option index, and positive so it
     * passes the server's usual sanity checks on a menu button.
     */
    public static final int TOGGLE_VARIANTS_BUTTON = 1_000_000;

    protected final ContainerLevelAccess access;
    protected final Level level;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final DataSlot showVariants = DataSlot.standalone();
    private final @Nullable ServerPlayer owner;

    /** Server side: what the picker can currently make. Always empty on the client. */
    private List<Option> options = List.of();
    /** The assembled previews for the picker. Filled in locally on the server, by payload on the client. */
    private List<ItemStack> optionIcons = List.of();

    private ItemStack input = ItemStack.EMPTY;
    private long lastSoundTime;
    private Runnable slotUpdateListener = () -> {
    };

    protected StationMenu(MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(menuType, containerId);
        this.access = access;
        this.level = inventory.player.level();
        this.owner = inventory.player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        // Added before the subclass adds any of its own so both sides agree on the indices. Item
        // slots live in a separate list, so a subclass adding those later changes nothing here.
        this.addDataSlot(this.selectedRecipeIndex);
        this.addDataSlot(this.showVariants);
    }

    /** The recipe type this station draws its options from. */
    protected abstract RecipeType<R> recipeType();

    /** Whatever is currently in the input slot. */
    protected abstract ItemStack stationInput();

    /**
     * Act on the player picking option {@code index}. Only ever called on the server, and only with
     * an index the picker actually offers.
     */
    protected abstract void selectOption(int index);

    /** Narrows {@link #recipeType()} further, for stations that share a type. */
    protected boolean accepts(R recipe) {
        return true;
    }

    /** Sound played when a result is taken. Stonecutter by default. */
    protected SoundEvent takeResultSound() {
        return SoundEvents.UI_STONECUTTER_TAKE_RESULT;
    }

    /**
     * Whether this station can also shape the input into the rest of its own block family.
     *
     * <p>Core generates stonecutting recipes from a family's parent to its slabs, stairs and walls,
     * so a station can offer those by walking one step along the same graph from whatever is in the
     * input slot - no recipe file per shape. Off by default: the arms station turns iron into a
     * breastplate, and nothing is cut from iron.</p>
     */
    public boolean supportsVariants() {
        return false;
    }

    /** Whether the picker is currently listing variants as well as base results. */
    public boolean showingVariants() {
        return this.showVariants.get() != 0;
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
        return !this.stationInput().isEmpty() && !this.optionIcons.isEmpty();
    }

    /** The options behind {@link #getOptionIcons()}. Server side only; empty on the client. */
    protected List<Option> options() {
        return this.options;
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
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == TOGGLE_VARIANTS_BUTTON) {
            if (!this.supportsVariants()) {
                return false;
            }
            // Flipped on both sides: the client redraws the toggle straight away, the server
            // rebuilds the list and pushes it. The slot re-syncs either way.
            this.showVariants.set(this.showingVariants() ? 0 : 1);
            this.setupRecipeList(this.stationInput());
            return true;
        }

        if (this.selectedRecipeIndex.get() == buttonId) {
            return false;
        }

        if (this.isValidRecipeIndex(buttonId)) {
            this.selectedRecipeIndex.set(buttonId);
            if (!this.level.isClientSide()) {
                this.selectOption(buttonId);
            }
        }

        return true;
    }

    protected boolean isValidRecipeIndex(int buttonId) {
        return buttonId >= 0 && buttonId < this.optionIcons.size();
    }

    @Override
    public void slotsChanged(Container container) {
        this.refreshOptions();
    }

    /**
     * Rebuilds the option list if the input has become a different item since last time.
     *
     * <p>A station whose container tells the menu when it changed gets this through
     * {@link #slotsChanged}; one backed by a block entity, which does not, calls it itself.</p>
     */
    protected void refreshOptions() {
        ItemStack stack = this.stationInput();
        if (!stack.is(this.input.getItem())) {
            this.input = stack.copy();
            this.setupRecipeList(stack);
        }
    }

    protected void setupRecipeList(ItemStack stack) {
        if (this.level.isClientSide()) {
            // Only the server can resolve modded recipes, and it pushes a fresh option list right
            // after this runs, so leave the current one on screen until that arrives.
            return;
        }

        this.selectedRecipeIndex.set(-1);
        this.clearSelection();
        this.options = this.buildOptions(stack);

        List<ItemStack> icons = new ArrayList<>(this.options.size());
        for (Option option : this.options) {
            icons.add(option.preview());
        }
        this.optionIcons = List.copyOf(icons);

        int restored = this.preferredSelection(this.options);
        if (restored >= 0 && restored < this.options.size()) {
            this.selectedRecipeIndex.set(restored);
        }

        if (this.owner != null) {
            StationNetwork.send(this.owner, new StationOptionsPayload(this.containerId, this.optionIcons));
        }
    }

    /** Drops whatever the last selection left behind, just before a new option list is built. */
    protected void clearSelection() {
    }

    /**
     * Which option should come up already highlighted, or -1 for none.
     *
     * <p>Only a station that remembers a selection of its own has anything to say here: a loom
     * carries on weaving with its screen shut, so reopening it should show what it is working on.</p>
     */
    protected int preferredSelection(List<Option> options) {
        return -1;
    }

    /**
     * What the picker shows for {@code stack}: either the station's own recipes for it, or the rest
     * of its block family. One or the other, never both, so the two lists stay legible.
     */
    private List<Option> buildOptions(ItemStack stack) {
        SingleRecipeInput recipeInput = new SingleRecipeInput(stack);
        List<RecipeHolder<R>> direct = StationRecipes.recipesFor(this.level, this.recipeType(), this::accepts, stack);

        if (!this.supportsVariants() || !this.showingVariants()) {
            List<Option> built = new ArrayList<>(direct.size());
            for (RecipeHolder<R> holder : direct) {
                built.add(new Option(holder.value().assemble(recipeInput), holder, false));
            }
            return dedupe(built);
        }

        if (!this.worksWith(stack, direct)) {
            return List.of();
        }

        List<Option> built = new ArrayList<>();
        for (StationRecipes.Cut cut : StationRecipes.cutsFrom(this.level, List.of(stack))) {
            built.add(new Option(cut.recipe().assemble(recipeInput), cut.holder(), true));
        }
        return dedupe(built);
    }

    /**
     * Whether this station is willing to shape {@code input} at all.
     *
     * <p>Family shapes come from the stonecutting graph, which knows nothing about which station is
     * open, so without this a set of woodworking tools would happily cut granite. A station works a
     * material it has a recipe for; subclasses widen that where they should.</p>
     *
     * @param direct the station's own recipes for this input, already resolved
     */
    protected boolean worksWith(ItemStack input, List<RecipeHolder<R>> direct) {
        return !direct.isEmpty();
    }

    /** Keeps the first option offering each distinct result, so base blocks win over cuts of them. */
    private static List<Option> dedupe(List<Option> options) {
        List<Option> unique = new ArrayList<>(options.size());
        for (Option option : options) {
            boolean seen = false;
            for (Option kept : unique) {
                if (ItemStack.isSameItemSameComponents(kept.result(), option.result())) {
                    seen = true;
                    break;
                }
            }
            if (!seen) {
                unique.add(option);
            }
        }
        return List.copyOf(unique);
    }

    /**
     * One entry in the picker.
     *
     * <p>The result is worked out once when the list is built rather than on each craft: a variant's
     * output depends on the station's own result rather than on what is in the input slot, so there
     * is nothing left to recompute.</p>
     *
     * @param result  what a craft yields, and what the picker draws
     * @param used    the recipe to credit the player with, for the recipe book and statistics
     * @param variant whether this was reached by cutting one of the station's own results
     */
    protected record Option(ItemStack result, RecipeHolder<?> used, boolean variant) {

        ItemStack preview() {
            return this.result.copy();
        }
    }

    protected void playTakeSound(Player player) {
        SoundEvent sound = this.takeResultSound();
        long gameTime = this.level.getGameTime();
        if (this.lastSoundTime == gameTime) {
            return;
        }
        this.lastSoundTime = gameTime;
        this.access.execute((level, pos) -> level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F));
        if (this.access == ContainerLevelAccess.NULL && !this.level.isClientSide()) {
            // Handheld stations have no block to play from, so it comes from the player instead.
            this.level.playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    public void registerUpdateListener(Runnable slotUpdateListener) {
        this.slotUpdateListener = slotUpdateListener;
    }

    /** Lets a subclass tell the screen something changed without a slot having moved. */
    protected void notifyScreen() {
        this.slotUpdateListener.run();
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack carried, Slot target) {
        return target != this.slots.get(RESULT_SLOT) && super.canTakeItemForPickAll(carried, target);
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
            } else if (StationRecipes.isValidInput(this.level, this.recipeType(), this::accepts, stack)) {
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
}
