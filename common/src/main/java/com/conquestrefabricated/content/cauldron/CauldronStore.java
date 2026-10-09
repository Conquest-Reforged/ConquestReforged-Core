package com.conquestrefabricated.content.cauldron;

import com.conquestrefabricated.core.Namespaces;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The cauldrons of one dimension that have anything in them, and the cooking that goes on there.
 *
 * <p>An entry exists from the first time something is put in a cauldron or sea water poured into it, and
 * is forgotten once it is empty again. Each tick the cauldrons that are loaded are cooked; one whose
 * block has gone - broken, blown up, replaced - drops what it held and is forgotten.</p>
 */
public class CauldronStore extends SavedData {

    public static final SavedDataType<CauldronStore> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "cauldrons"),
            CauldronStore::new,
            CauldronData.CODEC.listOf().xmap(CauldronStore::new, CauldronStore::entries),
            null);

    private final Map<Long, CauldronData> cauldrons = new HashMap<>();

    public CauldronStore() {
    }

    private CauldronStore(List<CauldronData> entries) {
        for (CauldronData data : entries) {
            this.cauldrons.put(data.pos, data);
            data.listen(this::setDirty);
        }
    }

    private List<CauldronData> entries() {
        return new ArrayList<>(this.cauldrons.values());
    }

    public static CauldronStore of(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    /** What the cauldron at {@code pos} holds, or null if there is nothing remembered. */
    public @Nullable CauldronData peek(BlockPos pos) {
        return this.cauldrons.get(pos.asLong());
    }

    /** What the cauldron at {@code pos} holds, starting an empty entry if there was none. */
    public CauldronData data(BlockPos pos) {
        return this.cauldrons.computeIfAbsent(pos.asLong(), key -> {
            CauldronData data = new CauldronData(key);
            data.listen(this::setDirty);
            this.setDirty();
            return data;
        });
    }

    /** One tick of every cauldron that is loaded. */
    public void tick(ServerLevel level) {
        if (this.cauldrons.isEmpty()) {
            return;
        }
        Iterator<CauldronData> iterator = this.cauldrons.values().iterator();
        while (iterator.hasNext()) {
            CauldronData data = iterator.next();
            BlockPos pos = BlockPos.of(data.pos);
            if (!level.hasChunkAt(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (!CauldronBlocks.cookable(state)) {
                spill(level, pos, data);
                iterator.remove();
                this.setDirty();
            } else if (data.isIdle() && ++data.idleFor > 1200) {
                // Empty and untouched for a minute: forget it. Anyone with it open keeps touching it.
                iterator.remove();
                this.setDirty();
            } else if (CauldronCooking.step(level, pos, state, data)) {
                this.setDirty();
            }
        }
    }

    private static void spill(ServerLevel level, BlockPos pos, CauldronData data) {
        for (ItemStack stack : data.items) {
            ItemStack rest = stack.copy();
            while (!rest.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        rest.split(Math.min(rest.getCount(), rest.getMaxStackSize())));
            }
        }
    }
}
