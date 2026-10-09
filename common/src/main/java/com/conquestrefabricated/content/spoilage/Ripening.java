package com.conquestrefabricated.content.spoilage;

import com.conquestrefabricated.core.Namespaces;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Food that gets better with time: a jar of salted cabbage turns into sauerkraut. The reverse of spoilage.
 *
 * <p>An item that ripens carries {@link #INTO} as a default component - what it becomes and how many days
 * that takes at the normal pace. Each stack carries {@link #RIPENESS}, how far along it is as a percentage
 * (absent is just begun). It ages by the same scan, and at the same pace, as spoilage, so a warm kitchen
 * ferments quicker than a cellar. A stack that rots first is simply lost; ripening does not save it.</p>
 */
public final class Ripening {

    public static final Identifier INTO_ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "ripens_into");
    public static final Identifier RIPENESS_ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "ripeness");

    /** What an item ripens into, and how long it takes. Set on the item, not on stacks. */
    public record Into(Identifier item, int days) {
        public static final Codec<Into> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("item").forGetter(Into::item),
                Codec.intRange(1, 365).fieldOf("days").forGetter(Into::days)
        ).apply(instance, Into::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Into> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, Into::item,
                ByteBufCodecs.VAR_INT, Into::days,
                Into::new).cast();
    }

    /** How far along a stack is, 1 to 99. */
    public record Ripeness(int percent) {
        public static final Codec<Ripeness> CODEC = Codec.intRange(1, 99).xmap(Ripeness::new, Ripeness::percent);
        public static final StreamCodec<RegistryFriendlyByteBuf, Ripeness> STREAM_CODEC =
                ByteBufCodecs.VAR_INT.map(Ripeness::new, Ripeness::percent).cast();
    }

    public static final DataComponentType<Into> INTO = DataComponentType.<Into>builder()
            .persistent(Into.CODEC).networkSynchronized(Into.STREAM_CODEC).build();
    public static final DataComponentType<Ripeness> RIPENESS = DataComponentType.<Ripeness>builder()
            .persistent(Ripeness.CODEC).networkSynchronized(Ripeness.STREAM_CODEC).build();

    private Ripening() {
    }

    public static int percent(ItemStack stack) {
        Ripeness ripeness = stack.get(RIPENESS);
        return ripeness == null ? 0 : ripeness.percent();
    }

    /** Ticks, at the normal pace, from just begun to ripe. */
    public static long duration(Into into) {
        return into.days() * SpoilClass.DAY;
    }

    /**
     * Ripens a stack by {@code rate} times the normal pace over {@code elapsed} ticks.
     *
     * @return the stack, or - once it is ripe - the same number of what it ripened into, as fresh as it was
     */
    public static ItemStack advance(ItemStack stack, double rate, long gameTime, long elapsed) {
        Into into = stack.get(INTO);
        if (into == null || rate <= 0.0) {
            return stack;
        }
        int percent = percent(stack);
        double steps = rate * elapsed / (duration(into) / 100.0);
        int whole = (int) steps;
        int salt = BuiltInRegistries.ITEM.getKey(stack.getItem()).hashCode() ^ 0x5BD1E995;
        if (Spoilage.hash(gameTime / Spoilage.INTERVAL, salt, percent) < steps - whole) {
            whole++;
        }
        if (whole == 0) {
            return stack;
        }
        if (percent + whole >= 100) {
            Item ripe = BuiltInRegistries.ITEM.getOptional(into.item()).orElse(null);
            if (ripe == null) {
                return stack;
            }
            ItemStack done = new ItemStack(ripe, stack.getCount());
            Spoilage.setStage(done, Spoilage.stage(stack));
            return done;
        }
        stack.set(RIPENESS, new Ripeness(percent + whole));
        return stack;
    }
}
