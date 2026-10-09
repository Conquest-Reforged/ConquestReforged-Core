package com.conquestrefabricated.content.spoilage;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * The world scan that ages food: every {@link Spoilage#INTERVAL} ticks, the inventories of players, every
 * container block entity in a loaded chunk, and items lying on the ground.
 *
 * <p>Containers are not looked up by walking the chunks; the loader calls {@link #track} and
 * {@link #untrack} as block entities enter and leave the world. Nothing ages in a chunk that is not
 * loaded - a larder left behind keeps what it had.</p>
 */
public final class SpoilageTicker {

    private static final Map<Level, Set<BlockEntity>> TRACKED = new WeakHashMap<>();

    private SpoilageTicker() {
    }

    public static void register() {
        TickEvent.SERVER_LEVEL_POST.register(SpoilageTicker::tick);
    }

    public static void track(BlockEntity entity) {
        Level level = entity.getLevel();
        if (level != null && !level.isClientSide() && entity instanceof Container) {
            TRACKED.computeIfAbsent(level, key -> Collections.newSetFromMap(new IdentityHashMap<>())).add(entity);
        }
    }

    public static void untrack(BlockEntity entity) {
        Set<BlockEntity> set = TRACKED.get(entity.getLevel());
        if (set != null) {
            set.remove(entity);
        }
    }

    private static void tick(ServerLevel level) {
        SpoilageMerge.forget();
        long time = level.getGameTime();
        if (time % Spoilage.INTERVAL != 0 || !Spoilage.enabled()) {
            return;
        }

        for (ServerPlayer player : level.players()) {
            double rate = SpoilageEnvironment.carried(level, player.blockPosition());
            long elapsed = SpoilageStamps.elapsed(player, time);
            SpoilageStamps.set(player, time);
            Container inventory = player.getInventory();
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                ItemStack stack = inventory.getItem(slot);
                ItemStack aged = Spoilage.age(stack, rate, time, elapsed);
                if (aged != stack) {
                    inventory.setItem(slot, aged);
                }
            }
        }

        Set<BlockEntity> tracked = TRACKED.get(level);
        if (tracked != null) {
            for (BlockEntity entity : new ArrayList<>(tracked)) {
                if (entity.isRemoved() || !level.isLoaded(entity.getBlockPos())) {
                    continue;
                }
                scan(level, entity, time);
            }
        }

        List<ItemEntity> dropped = new ArrayList<>();
        level.getAllEntities().forEach(entity -> {
            if (entity instanceof ItemEntity item && Spoilage.classOf(item.getItem()) != null) {
                dropped.add(item);
            }
        });
        for (ItemEntity item : dropped) {
            ItemStack stack = item.getItem();
            SpoilClass kind = Spoilage.classOf(stack);
            double rate = SpoilageEnvironment.stored(level, item.blockPosition(), kind, level.getBlockState(item.blockPosition()));
            int before = Spoilage.stage(stack);
            int ripe = Ripening.percent(stack);
            ItemStack aged = Spoilage.age(stack.copy(), rate, time, Spoilage.INTERVAL);
            if (aged.getItem() != stack.getItem() || Spoilage.stage(aged) != before || Ripening.percent(aged) != ripe) {
                item.setItem(aged);
            }
        }
    }

    private static void scan(ServerLevel level, BlockEntity entity, long time) {
        Container container = (Container) entity;
        BlockPos pos = entity.getBlockPos();
        Double[] rates = new Double[SpoilClass.values().length];
        boolean changed = false;
        boolean perishable = false;
        // Whatever the container missed while its chunk was unloaded is made up now, at today's pace.
        long elapsed = SpoilageStamps.elapsed(entity, time);
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            SpoilClass kind = Spoilage.classOf(stack);
            if (kind == null) {
                continue;
            }
            perishable = true;
            if (container instanceof SpoilageHold hold && hold.holds(slot)) {
                continue;
            }
            if (rates[kind.ordinal()] == null) {
                rates[kind.ordinal()] = SpoilageEnvironment.stored(level, pos, kind, entity.getBlockState());
            }
            int before = Spoilage.stage(stack);
            int ripe = Ripening.percent(stack);
            ItemStack aged = Spoilage.age(stack, rates[kind.ordinal()], time, elapsed);
            if (aged != stack) {
                container.setItem(slot, aged);
                changed = true;
            } else if (Spoilage.stage(stack) != before || Ripening.percent(stack) != ripe) {
                changed = true;
            }
        }
        if (perishable) {
            SpoilageStamps.set(entity, time);
        } else {
            // Nothing to age: forget the stamp, or food put in later would be aged for time it was not there.
            SpoilageStamps.clear(entity);
        }
        if (changed) {
            entity.setChanged();
        }
    }
}
