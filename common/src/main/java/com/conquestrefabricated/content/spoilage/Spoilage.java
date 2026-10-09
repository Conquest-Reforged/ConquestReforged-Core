package com.conquestrefabricated.content.spoilage;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.core.Namespaces;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import com.conquestrefabricated.content.salt.Salt;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

/**
 * Food that goes off. See {@link Freshness} for what is stored and {@link SpoilageEnvironment} for how
 * fast it goes; this is the part that ages one stack.
 *
 * <p>Ageing is lazy and coarse: a world scan every {@link #INTERVAL} ticks moves each perishable stack
 * a whole number of {@link #STAGES} on, and the chance of the odd extra stage is decided by a hash of the
 * time, item and stage - not by chance per stack - so two stacks of the same food in the same conditions
 * always age together and keep stacking.</p>
 */
public final class Spoilage {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "freshness");

    public static final DataComponentType<Freshness> FRESHNESS = DataComponentType.<Freshness>builder()
            .persistent(Freshness.CODEC)
            .networkSynchronized(Freshness.STREAM_CODEC)
            .build();

    /** What food rots into: the first item of this tag, or rotten flesh if no module has one. */
    public static final TagKey<Item> ROT = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "rot"));

    /**
     * The stored value is how far gone a stack is, as a percentage: 1 to 99 (absent is fresh, 100 is rot).
     * Stacks that differ only in this still merge - see {@link SpoilageMerge} - so it can be fine-grained.
     */
    public static final int STAGES = 100;

    /** Food this far gone, as a percentage, is too far gone to cure or pickle. */
    public static final int CURABLE_UP_TO = 40;

    /** Ticks between scans. */
    public static final int INTERVAL = 200;

    private Spoilage() {
    }

    public static boolean enabled() {
        return ConquestConfig.INSTANCE.spoilage.get();
    }

    /** The class an item belongs to, or null if it does not go off. */
    public static @Nullable SpoilClass classOf(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        for (SpoilClass kind : SpoilClass.values()) {
            if (stack.is(kind.tag())) {
                return kind;
            }
        }
        return null;
    }

    /** 0 for fresh, up to {@code STAGES - 1}. */
    public static int stage(ItemStack stack) {
        Freshness freshness = stack.get(FRESHNESS);
        return freshness == null ? 0 : freshness.stage();
    }

    public static void setStage(ItemStack stack, int stage) {
        if (stage <= 0) {
            stack.remove(FRESHNESS);
        } else {
            stack.set(FRESHNESS, new Freshness(Math.min(stage, STAGES - 1)));
        }
    }

    /**
     * Ages a stack in place by {@code rate} times the normal pace over {@code elapsed} ticks - one scan,
     * or the whole time a container sat in an unloaded chunk.
     *
     * @return the stack, aged - or, if it has rotted, what it rotted into, in the same number
     */
    public static ItemStack age(ItemStack stack, double rate, long gameTime, long elapsed) {
        stack = Ripening.advance(stack, rate, gameTime, elapsed);
        SpoilClass kind = classOf(stack);
        if (kind == null || rate <= 0.0) {
            return stack;
        }
        int stage = stage(stack);
        double perStage = (double) kind.shelfLife() / STAGES;
        double steps = rate * elapsed / perStage;
        int whole = (int) steps;
        if (hash(gameTime / INTERVAL, BuiltInRegistries.ITEM.getKey(stack.getItem()).hashCode(), stage) < steps - whole) {
            whole++;
        }
        if (whole == 0) {
            return stack;
        }
        if (stage + whole >= STAGES) {
            return rotten(stack.getCount());
        }
        setStage(stack, stage + whole);
        return stack;
    }

    /** What {@code count} of anything becomes when it has completely rotted. */
    public static ItemStack rotten(int count) {
        ItemStack rot = Salt.first(ROT);
        return rot.isEmpty() ? new ItemStack(Items.ROTTEN_FLESH, count) : rot.copyWithCount(count);
    }

    /** A value in [0, 1) that is the same for the same three inputs. */
    static double hash(long a, int b, int c) {
        long h = a * 0x9E3779B97F4A7C15L ^ (long) b * 0xC2B2AE3D27D4EB4FL ^ (long) c * 0x165667B19E3779F9L;
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        return (h >>> 11) * 0x1.0p-53;
    }

    /** The tooltip line for an aged stack. */
    public static Component describe(int stage) {
        int left = 100 - stage * 100 / STAGES;
        String key = stage * 4 < STAGES ? "fresh" : stage * 2 < STAGES ? "ageing" : stage * 4 < STAGES * 3 ? "stale" : "nearly_rotten";
        return Component.translatable("tooltip.conquest.freshness." + key, left);
    }
}
