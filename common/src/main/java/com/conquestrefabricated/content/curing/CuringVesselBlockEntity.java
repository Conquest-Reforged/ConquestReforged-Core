package com.conquestrefabricated.content.curing;

import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.spoilage.Spoilage;
import com.conquestrefabricated.content.spoilage.SpoilageHold;
import com.conquestrefabricated.content.station.StationRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A packing crate: a chest that also cures. Put meat or fish in with salt anywhere in it and the meat is
 * packed down - the salt is used up and, when the time is up, the meat in that slot is replaced by its
 * cured form, right where it lies. A crate with nothing to cure is just a crate.
 *
 * <p>Each slot is its own batch, and the screen shows how far along it is. Curing counts the world's clock,
 * so it carries on while the chunk is unloaded. Meat under salt does not go off while it cures; meat
 * that had already begun to turn comes out as stale as it went in, and meat too far gone is not taken at
 * all.</p>
 *
 * @see CuringRecipe
 */
public class CuringVesselBlockEntity extends BlockEntity implements MenuProvider, Container, SpoilageHold {

    public static final int SLOTS = 27;

    /** What the screen is told about each slot: 0 is nothing, 1 to 100 is how far a cure has got, and then why not. */
    public static final int STATE_NONE = 0;
    public static final int STATE_NEEDS_SALT = 101;
    public static final int STATE_TOO_FAR_GONE = 102;
    public static final int STATE_NO_ROOM = 103;

    /** One slot's cure under way: {@code count} of {@code item} began at {@code started} and are done at {@code ready}. */
    private record Cure(int slot, String item, int count, long started, long ready) {
        static final Codec<Cure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("slot").forGetter(Cure::slot),
                Codec.STRING.fieldOf("item").forGetter(Cure::item),
                Codec.INT.fieldOf("count").forGetter(Cure::count),
                Codec.LONG.fieldOf("started").forGetter(Cure::started),
                Codec.LONG.fieldOf("ready").forGetter(Cure::ready)
        ).apply(instance, Cure::new));
    }

    private final SimpleContainer slots = new SimpleContainer(SLOTS) {
        @Override
        public void setChanged() {
            super.setChanged();
            CuringVesselBlockEntity.this.setChanged();
        }
    };

    private final Cure[] cures = new Cure[SLOTS];
    /** What each slot's screen value is. Worked out by the tick; not saved. */
    private final int[] states = new int[SLOTS];

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index >= 0 && index < SLOTS ? states[index] : 0;
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return SLOTS;
        }
    };

    public CuringVesselBlockEntity(BlockPos pos, BlockState state) {
        super(TileEntityTypes.CURING_VESSEL, pos, state);
    }

    // ---------------------------------------------------------------------------------- working

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide() && level.getGameTime() % 10 == 0) {
            this.work(level);
        }
    }

    private void work(Level level) {
        long now = level.getGameTime();
        for (int slot = 0; slot < SLOTS; slot++) {
            ItemStack stack = this.slots.getItem(slot);
            CuringRecipe recipe = stack.isEmpty() ? null : this.recipeFor(level, stack);
            if (recipe == null) {
                this.cures[slot] = null;
                this.states[slot] = STATE_NONE;
                continue;
            }

            Cure cure = this.cures[slot];
            if (cure != null && !cure.item().equals(key(stack))) {
                cure = null;
            }
            if (cure != null && cure.count() > stack.getCount()) {
                cure = new Cure(slot, cure.item(), stack.getCount(), cure.started(), cure.ready());
            }
            if (cure == null) {
                if (Spoilage.enabled() && Spoilage.stage(stack) > Spoilage.CURABLE_UP_TO) {
                    this.cures[slot] = null;
                    this.states[slot] = STATE_TOO_FAR_GONE;
                    continue;
                }
                cure = this.begin(slot, stack, recipe, now);
                if (cure == null) {
                    this.cures[slot] = null;
                    this.states[slot] = STATE_NEEDS_SALT;
                    continue;
                }
            }
            this.cures[slot] = cure;

            if (now >= cure.ready()) {
                if (!this.finish(slot, stack, recipe, cure)) {
                    this.states[slot] = STATE_NO_ROOM;
                    continue;
                }
                this.cures[slot] = null;
                this.states[slot] = STATE_NONE;
            } else {
                long total = Math.max(1, cure.ready() - cure.started());
                this.states[slot] = (int) Math.clamp((now - cure.started()) * 100 / total, 1, 99);
            }
        }
    }

    private @Nullable CuringRecipe recipeFor(Level level, ItemStack stack) {
        for (RecipeHolder<CuringRecipe> holder : StationRecipes.<SingleRecipeInput, CuringRecipe>allOf(level,
                CuringRecipe.TYPE, recipe -> true)) {
            if (holder.value().input().test(stack)) {
                return holder.value();
            }
        }
        return null;
    }

    private static String key(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    /** Packs down as much of the stack as there is salt for. Null if there is none to spare. */
    private @Nullable Cure begin(int slot, ItemStack stack, CuringRecipe recipe, long now) {
        int available = 0;
        for (int other = 0; other < SLOTS; other++) {
            ItemStack candidate = this.slots.getItem(other);
            if (other != slot && recipe.acceptsMedium(candidate)) {
                available += candidate.getCount();
            }
        }
        int batch = Math.min(stack.getCount(), available * recipe.itemsPerMedium());
        if (batch <= 0) {
            return null;
        }
        int need = recipe.mediumFor(batch);
        for (int other = 0; other < SLOTS && need > 0; other++) {
            ItemStack candidate = this.slots.getItem(other);
            if (other != slot && recipe.acceptsMedium(candidate)) {
                int taken = Math.min(need, candidate.getCount());
                candidate.shrink(taken);
                need -= taken;
            }
        }
        level.playSound(null, this.worldPosition, SoundEvents.SAND_PLACE, SoundSource.BLOCKS, 0.8F, 0.8F);
        this.slots.setChanged();
        return new Cure(slot, key(stack), batch, now, now + Math.max(1, recipe.time()));
    }

    /** Swaps the cured goods for their cured form. False if there is nowhere to put it. */
    private boolean finish(int slot, ItemStack stack, CuringRecipe recipe, Cure cure) {
        int count = Math.min(cure.count(), stack.getCount());
        ItemStack made = recipe.resultFor(count);
        int stage = Spoilage.stage(stack);
        if (stage > 0 && Spoilage.enabled() && Spoilage.classOf(made) != null) {
            Spoilage.setStage(made, stage);
        }

        boolean wholeSlot = count == stack.getCount();
        if (wholeSlot && made.getCount() <= made.getMaxStackSize()) {
            this.slots.setItem(slot, made);
            return true;
        }
        int room = wholeSlot ? made.getMaxStackSize() : 0;
        for (int other = 0; other < SLOTS; other++) {
            ItemStack candidate = this.slots.getItem(other);
            if (other == slot) {
                continue;
            }
            if (candidate.isEmpty()) {
                room += made.getMaxStackSize();
            } else if (ItemStack.isSameItemSameComponents(candidate, made)) {
                room += candidate.getMaxStackSize() - candidate.getCount();
            }
        }
        if (room < made.getCount()) {
            return false;
        }
        stack.shrink(count);
        if (stack.isEmpty()) {
            this.slots.setItem(slot, ItemStack.EMPTY);
        }
        // Into matching stacks first, then into empty slots - the one just emptied included.
        for (int pass = 0; pass < 2 && !made.isEmpty(); pass++) {
            for (int other = 0; other < SLOTS && !made.isEmpty(); other++) {
                ItemStack candidate = this.slots.getItem(other);
                if (pass == 0 && !candidate.isEmpty() && ItemStack.isSameItemSameComponents(candidate, made)) {
                    int moved = Math.min(made.getCount(), candidate.getMaxStackSize() - candidate.getCount());
                    candidate.grow(moved);
                    made.shrink(moved);
                } else if (pass == 1 && candidate.isEmpty()) {
                    this.slots.setItem(other, made.split(Math.min(made.getCount(), made.getMaxStackSize())));
                }
            }
        }
        this.slots.setChanged();
        return true;
    }

    /** Meat under salt does not go off while it cures. */
    @Override
    public boolean holds(int slot) {
        return slot >= 0 && slot < SLOTS && this.cures[slot] != null;
    }

    // ------------------------------------------------------------------------------- container

    @Override
    public int getContainerSize() {
        return SLOTS;
    }

    @Override
    public boolean isEmpty() {
        return this.slots.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.slots.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        return this.slots.removeItem(slot, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return this.slots.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.slots.setItem(slot, stack);
    }

    @Override
    public void clearContent() {
        this.slots.clearContent();
    }

    @Override
    public boolean stillValid(Player player) {
        return !this.isRemoved() && this.level != null
                && player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    // ------------------------------------------------------------------------------------- menu

    @Override
    public Component getDisplayName() {
        return this.getBlockState().getBlock().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new PackingBoxMenu(containerId, inventory, this.slots, this.data, this);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        Level level = this.level;
        if (level != null && !level.isClientSide()) {
            for (int slot = 0; slot < SLOTS; slot++) {
                ItemStack rest = this.slots.getItem(slot).copy();
                while (!rest.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            rest.split(Math.min(rest.getCount(), rest.getMaxStackSize())));
                }
            }
        }
    }

    // ------------------------------------------------------------------------------------ saving

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        for (int slot = 0; slot < SLOTS; slot++) {
            output.store("slot_" + slot, ItemStack.OPTIONAL_CODEC, this.slots.getItem(slot));
        }
        List<Cure> running = new java.util.ArrayList<>();
        for (Cure cure : this.cures) {
            if (cure != null) {
                running.add(cure);
            }
        }
        output.store("cures", Cure.CODEC.listOf(), running);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (int slot = 0; slot < SLOTS; slot++) {
            this.slots.setItem(slot, input.read("slot_" + slot, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
        }
        java.util.Arrays.fill(this.cures, null);
        for (Cure cure : input.read("cures", Cure.CODEC.listOf()).orElse(List.of())) {
            if (cure.slot() >= 0 && cure.slot() < SLOTS) {
                this.cures[cure.slot()] = cure;
            }
        }
    }
}
