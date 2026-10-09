package com.conquestrefabricated.content.cauldron;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * What one cauldron holds besides its water: three ingredient slots, a fuel slot and an output slot,
 * how long the fire has left, how far the cooking has got, and whether the water in it is sea water.
 *
 * <p>This is kept by position in {@link CauldronStore}, not on a block entity. A vanilla cauldron is a
 * different block when it holds water than when it does not, and a block entity would be thrown away,
 * contents and all, each time the last bucket was boiled off. The position outlives that.</p>
 */
public class CauldronData implements Container {

    public static final int INGREDIENT_SLOTS = CauldronRecipe.MAX_INGREDIENTS;
    public static final int FUEL_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;
    public static final int SIZE = 5;

    public static final Codec<CauldronData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("pos").forGetter(data -> data.pos),
            ItemStack.OPTIONAL_CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(data -> new ArrayList<>(data.items)),
            Codec.INT.optionalFieldOf("lit_time", 0).forGetter(data -> data.litTime),
            Codec.INT.optionalFieldOf("lit_duration", 0).forGetter(data -> data.litDuration),
            Codec.INT.optionalFieldOf("progress", 0).forGetter(data -> data.progress),
            Codec.INT.optionalFieldOf("total", 0).forGetter(data -> data.total),
            Codec.BOOL.optionalFieldOf("brine", false).forGetter(data -> data.brine)
    ).apply(instance, CauldronData::new));

    final long pos;
    final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    int litTime;
    int litDuration;
    int progress;
    int total;
    boolean brine;
    /** Ticks since anything happened to it; an empty one is forgotten once this is long enough that nobody has it open. */
    int idleFor;

    /** Told when anything changes, so the store is saved. */
    private Runnable onChanged = () -> {
    };

    CauldronData(long pos) {
        this.pos = pos;
    }

    private CauldronData(long pos, List<ItemStack> items, int litTime, int litDuration, int progress, int total,
                         boolean brine) {
        this.pos = pos;
        for (int slot = 0; slot < Math.min(SIZE, items.size()); slot++) {
            this.items.set(slot, items.get(slot));
        }
        this.litTime = litTime;
        this.litDuration = litDuration;
        this.progress = progress;
        this.total = total;
        this.brine = brine;
    }

    void listen(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    public boolean isBrine() {
        return this.brine;
    }

    public void setBrine(boolean brine) {
        if (this.brine != brine) {
            this.brine = brine;
            this.onChanged.run();
        }
    }

    public int litTime() {
        return this.litTime;
    }

    public int litDuration() {
        return this.litDuration;
    }

    public int progress() {
        return this.progress;
    }

    public int total() {
        return this.total;
    }

    /** Nothing in it, nothing burning, and no brine to remember: safe to forget. */
    boolean isIdle() {
        return this.isEmpty() && this.litTime <= 0 && this.progress <= 0 && !this.brine;
    }

    boolean hasIngredients() {
        for (int slot = 0; slot < INGREDIENT_SLOTS; slot++) {
            if (!this.items.get(slot).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // ----------------------------------------------------------------------------------- container

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack taken = ContainerHelper.removeItem(this.items, slot, count);
        if (!taken.isEmpty()) {
            this.setChanged();
        }
        return taken;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        this.setChanged();
    }

    @Override
    public void setChanged() {
        this.idleFor = 0;
        this.onChanged.run();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }
}
