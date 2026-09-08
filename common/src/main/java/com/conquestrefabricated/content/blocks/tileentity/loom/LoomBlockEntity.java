package com.conquestrefabricated.content.blocks.tileentity.loom;

import com.conquestrefabricated.content.blocks.block.decor.Loom;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.loom.LoomMenu;
import com.conquestrefabricated.content.loom.LoomWeaves;
import com.conquestrefabricated.content.loom.WeavingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Direction;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A loom: two slots, a weave, and whatever it is part way through making.
 *
 * <p>The weave the block draws is derived from those slots - the finished cloth in the output if
 * there is one, otherwise whatever is going in - and stored under the same {@code product} key looms
 * have always used, so the models and any loom placed before this existed carry on unchanged. Looms
 * saved before the slots existed are translated on load: their stored cloth is put back into the
 * output slot, which leaves the weave identical while making it something the player can now take
 * out. See {@link LoomWeaves}.</p>
 *
 * <p>Weaving takes time. The recipe the player picked in {@link LoomMenu} is remembered here rather
 * than in the menu, so a loom keeps working with nobody watching it, and picks up where it left off
 * across a reload. Family shapes come through as recipes with no time at all, which is what makes
 * them instant.</p>
 */
public class LoomBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int SLOT_COUNT = 2;

    public static final int DATA_PROGRESS = 0;
    public static final int DATA_DURATION = 1;
    public static final int DATA_COUNT = 2;

    /** How many crafts an instant recipe may do in one tick, so a bad recipe cannot hang the tick. */
    private static final int INSTANT_CRAFT_LIMIT = 64;

    /**
     * Bumped whenever saved looms need translating on load. Version 0 is a loom from before the
     * weaving station, which had a weave but no slots to keep it in.
     */
    private static final int DATA_VERSION = 1;
    private static final String DATA_VERSION_KEY = "data_version";

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    /** The item id the block draws its weave from. Empty means bare. */
    private String product = "";

    private @Nullable ResourceKey<Recipe<?>> selectedRecipe;
    private int progress;
    private int duration;

    /**
     * Set for an old loom whose weave names an item this installation does not have, so it could not
     * be put back into a slot. Its weave is then left exactly as it was rather than being derived
     * away, because deriving it away would mean silently stripping the loom.
     */
    private boolean legacyWeave;

    /**
     * The item the weave was last worked out from, so the tick can skip the work when nothing in the
     * slots has changed. Not saved - {@code weaveResolved} starts false, so the first tick after a
     * load always recomputes.
     */
    private @Nullable Item weaveSource;
    private boolean weaveResolved;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_PROGRESS -> LoomBlockEntity.this.progress;
                case DATA_DURATION -> LoomBlockEntity.this.duration;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_PROGRESS -> LoomBlockEntity.this.progress = value;
                case DATA_DURATION -> LoomBlockEntity.this.duration = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public LoomBlockEntity(BlockPos pos, BlockState state) {
        super(TileEntityTypes.LOOM, pos, state);
    }

    // ----------------------------------------------------------------------------------- weaving

    public void tick(Level world, BlockPos pos) {
        if (world.isClientSide()) {
            return;
        }

        // Every way a slot can change - the player, a hopper, a craft - ends up here a tick later,
        // so the weave is worked out in one place rather than at each of those call sites.
        this.updateWeave();

        SingleItemRecipe recipe = this.resolveSelected(world);
        ItemStack input = this.items.get(INPUT_SLOT);
        if (recipe == null || input.isEmpty() || !recipe.matches(new SingleRecipeInput(input), world)) {
            this.stall();
            return;
        }

        int time = recipe instanceof WeavingRecipe weaving ? weaving.time() : 0;
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

    /** Nothing to weave: forget any part-done work, but keep the recipe in case the input comes back. */
    private void stall() {
        this.duration = 0;
        if (this.progress != 0) {
            this.progress = 0;
            this.setChanged();
        }
    }

    /**
     * A recipe with no time is worked through in one go, which is how the picker's family shapes
     * behave: cutting a woven cloth into layers is shaping, not weaving, so it costs nothing.
     */
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
     * The recipe this loom is working on, if it is still one that can be worked on a loom.
     *
     * <p>Both the loom's own weaving recipes and the stonecutting recipes behind the picker's family
     * shapes are single-item recipes, which is all this needs them to be. A recipe that has since
     * been removed from the datapack simply resolves to nothing.</p>
     */
    private @Nullable SingleItemRecipe resolveSelected(Level world) {
        if (this.selectedRecipe == null || !(world.recipeAccess() instanceof RecipeManager recipes)) {
            return null;
        }
        RecipeHolder<?> holder = recipes.byKey(this.selectedRecipe).orElse(null);
        return holder != null && holder.value() instanceof SingleItemRecipe recipe ? recipe : null;
    }

    /** What the loom is working on, or null if nothing has been picked. */
    public @Nullable ResourceKey<Recipe<?>> getSelectedRecipe() {
        return this.selectedRecipe;
    }

    /** Points the loom at a recipe, starting its progress over. */
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

    // ------------------------------------------------------------------------------------- weave

    /**
     * Works out which weave the block should be drawing: the finished cloth in the output slot if
     * there is one, otherwise whatever is going in, otherwise nothing at all.
     */
    private void updateWeave() {
        ItemStack output = this.items.get(OUTPUT_SLOT);
        ItemStack input = this.items.get(INPUT_SLOT);
        Item source = !output.isEmpty() ? output.getItem() : (!input.isEmpty() ? input.getItem() : null);

        // Runs every tick, so it stops here rather than building an id string each time.
        if (this.weaveResolved && source == this.weaveSource) {
            return;
        }
        this.weaveSource = source;
        this.weaveResolved = true;

        String derived = source == null ? "" : LoomWeaves.productOf(source);

        if (derived.isEmpty() && this.legacyWeave) {
            // Nothing on the loom and nothing we could have put there: leave the old weave be.
            return;
        }
        if (!derived.isEmpty()) {
            // Something is on the loom now, so it is the slots that say what it looks like.
            this.legacyWeave = false;
        }
        this.setProduct(derived);
    }

    public String getProduct() {
        return this.product;
    }

    /**
     * Sets the weave directly and brings {@code HAS_THREAD} into line with it.
     *
     * <p>A loom holding something with no weave of its own - the wool going in, say - is drawn bare
     * rather than in the fallback white, which is what the property is for.</p>
     */
    public void setProduct(String value) {
        if (this.product.equals(value)) {
            return;
        }
        this.product = value;

        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        BlockState state = this.getBlockState();
        boolean threaded = LoomWeaves.isKnown(value);
        if (state.hasProperty(Loom.HAS_THREAD) && state.getValue(Loom.HAS_THREAD) != threaded) {
            this.level.setBlock(this.worldPosition, state.setValue(Loom.HAS_THREAD, threaded), 3);
        }
        this.setChanged();
        BlockState updated = this.getBlockState();
        this.level.sendBlockUpdated(this.worldPosition, updated, updated, 3);
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
        // The output is the loom's to fill, so anything feeding the loom can only reach the input.
        return slot == INPUT_SLOT;
    }

    /**
     * Automation reaches a loom the way it reaches a furnace: material goes in from above or the
     * side, finished cloth comes out of the bottom. Without this a hopper underneath would happily
     * drain the material back out of the input slot.
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

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new LoomMenu(containerId, inventory, this, this.dataAccess);
    }

    // ------------------------------------------------------------------------------------ saving

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        boolean legacy = input.getIntOr(DATA_VERSION_KEY, 0) < DATA_VERSION;

        this.items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.product = input.getStringOr("product", "");
        this.legacyWeave = input.getBooleanOr("legacy_weave", false);
        this.progress = input.getIntOr("progress", 0);
        this.duration = input.getIntOr("duration", 0);
        this.selectedRecipe = input.getString("recipe")
                .map(Identifier::tryParse)
                .map(id -> ResourceKey.create(Registries.RECIPE, id))
                .orElse(null);

        if (legacy) {
            this.migrateLegacyWeave();
        }
    }

    /**
     * Puts an old loom's cloth back where the block can now show it from.
     *
     * <p>Before the loom was a station its weave was set by right-clicking the cloth onto it, which
     * consumed the item, and shift-clicking gave it back. Moving that same cloth into the output slot
     * leaves the weave exactly as it was while making it something the player can take out - which
     * matters now that taking it out is the only way to clear the weave.</p>
     *
     * <p>If the cloth is not a registered item - a rug from a module that is not loaded - the stored
     * product is left alone, so the loom still draws what it always drew.</p>
     */
    private void migrateLegacyWeave() {
        if (!LoomWeaves.isKnown(this.product)
                || !this.items.get(INPUT_SLOT).isEmpty()
                || !this.items.get(OUTPUT_SLOT).isEmpty()) {
            return;
        }
        LoomWeaves.itemOf(this.product).ifPresentOrElse(
                item -> this.items.set(OUTPUT_SLOT, new ItemStack(item)),
                () -> this.legacyWeave = true);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(DATA_VERSION_KEY, DATA_VERSION);
        ContainerHelper.saveAllItems(output, this.items);
        output.putString("product", this.product);
        if (this.legacyWeave) {
            output.putBoolean("legacy_weave", true);
        }
        output.putInt("progress", this.progress);
        output.putInt("duration", this.duration);
        if (this.selectedRecipe != null) {
            output.putString("recipe", this.selectedRecipe.identifier().toString());
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }
}
