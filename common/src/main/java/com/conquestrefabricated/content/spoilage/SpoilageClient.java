package com.conquestrefabricated.content.spoilage;

import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Client side of spoilage: what a perishable stack's tooltip says.
 *
 * <p>Every perishable item gets a line - fresh ones too, which carry no data at all - then how long it has
 * left before it rots, where it is kept and how fast it ages there, and a quieter line saying what kind of
 * food it is and how long it keeps. Item tags are synced to the client, so the client can tell the kind
 * without being told.</p>
 *
 * <p>Where it is kept matters. A stack in the player's own inventory ages at the pace of the biome and any
 * fire or ice beside them. A stack in an open container ages at the pace there - a cellar, an attic, a
 * barrel - which only the server can work out; it sends that when the container opens (see
 * {@link SpoilageStorage}) and it is kept here until it closes.</p>
 */
public final class SpoilageClient {

    private static final long HOUR = SpoilClass.DAY / 24;

    /** The pace in the container that is open, or {@link StoragePace#NONE}. */
    private static volatile StoragePace storage = StoragePace.NONE;

    private SpoilageClient() {
    }

    /** Called when the server sends the pace of a container that has just opened or closed. */
    public static void setStorage(StoragePace pace) {
        storage = pace;
    }

    public static void register() {
        ClientTooltipEvent.ITEM.register((stack, lines, context, flag) -> {
            if (!Spoilage.enabled()) {
                return;
            }
            SpoilClass kind = Spoilage.classOf(stack);
            if (kind == null) {
                return;
            }
            int stage = Spoilage.stage(stack);
            boolean stored = isInOpenContainer(stack);

            double rate;
            Component where;
            if (stored) {
                rate = Math.max(0.05, storage.rates().get(kind.ordinal()));
                where = Component.translatable("tooltip.conquest.spoilage.place." + place(storage.place()));
            } else {
                rate = Math.max(0.05, carriedRate());
                where = Component.translatable("tooltip.conquest.spoilage.place.carried");
            }

            lines.add(Spoilage.describe(stage));
            Ripening.Into into = stack.get(Ripening.INTO);
            if (into != null) {
                int ripe = Ripening.percent(stack);
                long left = Math.round(Ripening.duration(into) * (100 - ripe) / 100.0 / rate);
                lines.add(Component.translatable("tooltip.conquest.ripening", ripe, duration(left))
                        .withStyle(ChatFormatting.GREEN));
            }
            lines.add(Component.translatable("tooltip.conquest.spoilage.rots_in", duration(ticksLeft(kind, stage, rate))));
            lines.add(Component.translatable("tooltip.conquest.spoilage.where", where, Math.round(rate * 100))
                    .withStyle(ChatFormatting.AQUA));
            lines.add(Component.translatable("tooltip.conquest.spoilage.keeps",
                    Component.translatable("tooltip.conquest.spoilage.class." + kind.name().toLowerCase()),
                    kind.shelfLife() / SpoilClass.DAY).withStyle(ChatFormatting.DARK_GRAY));
        });
    }

    private static String place(int ordinal) {
        return SpoilageEnvironment.Place.values()[Math.max(0, Math.min(ordinal, SpoilageEnvironment.Place.values().length - 1))]
                .name().toLowerCase();
    }

    /** Whether this very stack is in a slot of the open container rather than the player's own inventory. */
    private static boolean isInOpenContainer(ItemStack stack) {
        if (storage.isNone() || storage.rates().size() != SpoilClass.values().length) {
            return false;
        }
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
            for (Slot slot : screen.getMenu().slots) {
                if (slot.getItem() == stack) {
                    return !(slot.container instanceof Inventory);
                }
            }
        }
        return false;
    }

    private static double carriedRate() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player == null ? 1.0 : SpoilageEnvironment.carried(player.level(), player.blockPosition());
    }

    /** Ticks until a stack at {@code stage} rots, at {@code rate} times the normal pace. */
    private static long ticksLeft(SpoilClass kind, int stage, double rate) {
        double remaining = kind.shelfLife() * (Spoilage.STAGES - stage) / (double) Spoilage.STAGES;
        return Math.max(0L, Math.round(remaining / rate));
    }

    /** "3 days 4 hours", or just hours, or "less than an hour" - game time - and the real minutes it is. */
    private static Component duration(long ticks) {
        long days = ticks / SpoilClass.DAY;
        long hours = (ticks % SpoilClass.DAY) / HOUR;
        Component game;
        if (days > 0) {
            Component d = Component.translatable(days == 1 ? "tooltip.conquest.time.day" : "tooltip.conquest.time.days", days);
            game = hours > 0
                    ? Component.translatable("tooltip.conquest.time.and", d,
                    Component.translatable(hours == 1 ? "tooltip.conquest.time.hour" : "tooltip.conquest.time.hours", hours))
                    : d;
        } else if (hours > 0) {
            game = Component.translatable(hours == 1 ? "tooltip.conquest.time.hour" : "tooltip.conquest.time.hours", hours);
        } else {
            game = Component.translatable("tooltip.conquest.time.under_an_hour");
        }
        return Component.translatable("tooltip.conquest.time.with_real", game, Math.max(1, ticks / 1200));
    }
}
