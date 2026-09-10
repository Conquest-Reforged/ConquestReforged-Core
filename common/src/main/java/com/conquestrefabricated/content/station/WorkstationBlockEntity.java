package com.conquestrefabricated.content.station;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A block you leave working: two slots, a job it remembers, and a craft that takes time.
 *
 * <p>The recipe the player picked is kept here rather than in the menu, so the block carries on with
 * nobody watching it and picks up where it left off across a reload. A recipe with no time at all is
 * worked through in one go, which is what the picker's family shapes are - reshaping something is not
 * making it.</p>
 *
 * <p>Subclasses supply their block entity type and their menu, and may hook
 * {@link #beforeCraftTick()} for anything the block shows about its own contents - a loom's weave.</p>
 *
 * @see WorkstationMenu
 * @see TimedStationRecipe
 */
public abstract class WorkstationBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int SLOT_COUNT = 2;

    public static final int DATA_PROGRESS = 0;
    public static final int DATA_DURATION = 1;
    public static final int DATA_COUNT = 2;

    /** How many crafts an instant recipe may do in one tick, so a bad recipe cannot hang the tick. */
    private static final int INSTANT_CRAFT_LIMIT = 64;

    /**
     * Bumped whenever saved blocks need translating on load. Version 0 is one from before this
     * existed - see {@link #loadExtra}, which is where a subclass acts on that.
     */
    protected static final int DATA_VERSION = 1;
    private static final String DATA_VERSION_KEY = "data_version";

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    private @Nullable ResourceKey<Recipe<?>> selectedRecipe;
    private int progress;
    private int duration;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_PROGRESS -> WorkstationBlockEntity.this.progress;
                case DATA_DURATION -> WorkstationBlockEntity.this.duration;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_PROGRESS -> WorkstationBlockEntity.this.progress = value;
                case DATA_DURATION -> WorkstationBlockEntity.this.duration = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    protected WorkstationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ----------------------------------------------------------------------------------- working

    public void tick(Level world, BlockPos pos) {
        if (world.isClientSide()) {
            return;
        }

        this.beforeCraftTick();

        SingleItemRecipe recipe = this.resolveSelected(world);
        ItemStack input = this.items.get(INPUT_SLOT);
        if (recipe == null || input.isEmpty() || !recipe.matches(new SingleRecipeInput(input), world)) {
            this.stall();
            return;
        }

        int time = durationOf(recipe);
        this.duration = time;

        ItemStack result = recipe.assemble(new SingleRecipeInput(input));
        if (!this.canAccept(result)) {
            // Output full. Hold the progress made so far rather than throwing it away.
            return;
        }

        if (time <= 0) {
            this.craftInstantly(world, recipe);
            return;
        }

        this.progress++;
        if (this.progress >= time) {
            this.progress = 0;
            this.craftOnce(result);
            this.setChanged();
        }
    }

    /** Called once a tick before any crafting, for whatever the block shows about its contents. */
    protected void beforeCraftTick() {
    }

    /**
     * How long a craft takes. A station's own recipes say; the stonecutting recipes behind the
     * picker's family shapes do not, and are instant.
     */
    protected static int durationOf(SingleItemRecipe recipe) {
        return recipe instanceof TimedStationRecipe timed ? timed.time() : 0;
    }

    /** Nothing to work on: forget any part-done craft, but keep the recipe in case the input returns. */
    private void stall() {
        this.duration = 0;
        if (this.progress != 0) {
            this.progress = 0;
            this.setChanged();
        }
    }

    private void craftInstantly(Level world, SingleItemRecipe recipe) {
        int crafted = 0;
        while (crafted < INSTANT_CRAFT_LIMIT) {
            ItemStack input = this.items.get(INPUT_SLOT);
            if (input.isEmpty() || !recipe.matches(new SingleRecipeInput(input), world)) {
                break;
            }
            ItemStack result = recipe.assemble(new SingleRecipeInput(input));
            if (!this.canAccept(result)) {
                break;
            }
            this.craftOnce(result);
            crafted++;
        }
        if (crafted > 0) {
            this.setChanged();
        }
    }

    private void craftOnce(ItemStack result) {
        ItemStack output = this.items.get(OUTPUT_SLOT);
        if (output.isEmpty()) {
            this.items.set(OUTPUT_SLOT, result.copy());
        } else {
            output.grow(result.getCount());
        }

        ItemStack input = this.items.get(INPUT_SLOT);
        input.shrink(1);
        if (input.isEmpty()) {
            this.items.set(INPUT_SLOT, ItemStack.EMPTY);
        }
    }

    /** Whether {@code result} would fit in the output slot as it stands. */
    private boolean canAccept(ItemStack result) {
        if (result.isEmpty()) {
            return false;
        }
        ItemStack output = this.items.get(OUTPUT_SLOT);
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(output, result)) {
            return false;
        }
        int limit = Math.min(this.getMaxStackSize(), output.getMaxStackSize());
        return output.getCount() + result.getCount() <= limit;
    }

    /**
     * The recipe this block is working on, if it is still one it can work.
     *
     * <p>Both a station's own recipes and the stonecutting recipes behind the picker's family shapes
     * are single-item recipes, which is all this needs them to be. A recipe that has since been
     * removed from the datapack simply resolves to nothing.</p>
     */
    private @Nullable SingleItemRecipe resolveSelected(Level world) {
        if (this.selectedRecipe == null || !(world.recipeAccess() instanceof RecipeManager recipes)) {
            return null;
        }
        RecipeHolder<?> holder = recipes.byKey(this.selectedRecipe).orElse(null);
        return holder != null && holder.value() instanceof SingleItemRecipe recipe ? recipe : null;
    }

    /** What this is working on, or null if nothing has been picked. */
    public @Nullable ResourceKey<Recipe<?>> getSelectedRecipe() {
        return this.selectedRecipe;
    }

    /** Points it at a recipe, starting its progress over. */
    public void setSelectedRecipe(@Nullable ResourceKey<Recipe<?>> recipe) {
        if (Objects.equals(this.selectedRecipe, recipe)) {
            return;
        }
        this.selectedRecipe = recipe;
        this.progress = 0;
        this.duration = 0;
        this.setChanged();
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    // --------------------------------------------------------------------------------- container

    @Override
    protected Component getDefaultName() {
        return this.getBlockState().getBlock().getName();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        // The output is the block's to fill, so anything feeding it can only reach the input.
        return slot == INPUT_SLOT;
    }

    /**
     * Automation reaches a workstation the way it reaches a furnace: material in from above or the
     * side, finished work out of the bottom. Without this a hopper underneath would happily drain
     * the material back out of the input slot.
     */
    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? new int[]{OUTPUT_SLOT} : new int[]{INPUT_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == OUTPUT_SLOT;
    }

    // ------------------------------------------------------------------------------------ saving

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        boolean legacy = input.getIntOr(DATA_VERSION_KEY, 0) < DATA_VERSION;

        this.items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.progress = input.getIntOr("progress", 0);
        this.duration = input.getIntOr("duration", 0);
        this.selectedRecipe = input.getString("recipe")
                .map(Identifier::tryParse)
                .map(id -> ResourceKey.create(Registries.RECIPE, id))
                .orElse(null);

        this.loadExtra(input, legacy);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(DATA_VERSION_KEY, DATA_VERSION);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("progress", this.progress);
        output.putInt("duration", this.duration);
        if (this.selectedRecipe != null) {
            output.putString("recipe", this.selectedRecipe.identifier().toString());
        }
        this.saveExtra(output);
    }

    /**
     * Reads whatever the subclass keeps beyond the slots.
     *
     * @param legacy whether this was saved before the block had slots, so needs translating
     */
    protected void loadExtra(ValueInput input, boolean legacy) {
    }

    protected void saveExtra(ValueOutput output) {
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registryLookup) {
        return this.saveWithoutMetadata(registryLookup);
    }
}
