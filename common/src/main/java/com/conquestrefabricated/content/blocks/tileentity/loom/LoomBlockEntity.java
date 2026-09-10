package com.conquestrefabricated.content.blocks.tileentity.loom;

import com.conquestrefabricated.content.blocks.block.decor.Loom;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.loom.LoomMenu;
import com.conquestrefabricated.content.loom.LoomWeaves;
import com.conquestrefabricated.content.station.WorkstationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * A loom: a workstation that also wears what it is working on.
 *
 * <p>Everything about the slots and the weaving itself is {@link WorkstationBlockEntity}. What is
 * left here is the weave the block draws, derived from those slots - the finished cloth in the output
 * if there is one, otherwise whatever is going in - and stored under the same {@code product} key
 * looms have always used, so the models and any loom placed before this existed carry on unchanged.
 * See {@link LoomWeaves}.</p>
 */
public class LoomBlockEntity extends WorkstationBlockEntity {

    /** The item id the block draws its weave from. Empty means bare. */
    private String product = "";

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

    public LoomBlockEntity(BlockPos pos, BlockState state) {
        super(TileEntityTypes.LOOM, pos, state);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new LoomMenu(containerId, inventory, this, this.getDataAccess());
    }

    // ------------------------------------------------------------------------------------- weave

    @Override
    protected void beforeCraftTick() {
        this.updateWeave();
    }

    /**
     * Works out which weave the block should be drawing: the finished cloth in the output slot if
     * there is one, otherwise whatever is going in, otherwise nothing at all.
     */
    private void updateWeave() {
        ItemStack output = this.getItem(OUTPUT_SLOT);
        ItemStack input = this.getItem(INPUT_SLOT);
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

    // ------------------------------------------------------------------------------------ saving

    @Override
    protected void loadExtra(ValueInput input, boolean legacy) {
        this.product = input.getStringOr("product", "");
        this.legacyWeave = input.getBooleanOr("legacy_weave", false);
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
                || !this.getItem(INPUT_SLOT).isEmpty()
                || !this.getItem(OUTPUT_SLOT).isEmpty()) {
            return;
        }
        LoomWeaves.itemOf(this.product).ifPresentOrElse(
                item -> this.getItems().set(OUTPUT_SLOT, new ItemStack(item)),
                () -> this.legacyWeave = true);
    }

    @Override
    protected void saveExtra(ValueOutput output) {
        output.putString("product", this.product);
        if (this.legacyWeave) {
            output.putBoolean("legacy_weave", true);
        }
    }
}
