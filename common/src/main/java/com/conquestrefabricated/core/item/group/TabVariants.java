package com.conquestrefabricated.core.item.group;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

/**
 * Vanilla items that are only useful with a component set (potions, enchanted books, goat horns...).
 * Vanilla's own tabs generate one stack per variant; a tab file can only name the bare item, so the
 * tab builders hand those items here instead.
 */
public final class TabVariants {

    private TabVariants() {
    }

    /**
     * @return true if the item was expanded into {@code out}, false if it should be added as-is
     */
    public static boolean expand(Item item, HolderLookup.Provider holders, Consumer<ItemStack> out) {
        if (item instanceof TippedArrowItem) {
            BuiltInRegistries.POTION.listElements()
                    .filter(potion -> !potion.value().getEffects().isEmpty())
                    .forEach(potion -> out.accept(PotionContents.createItemStack(item, potion)));
            return true;
        }
        if (item instanceof PotionItem) {
            // potion, splash and lingering; water and the base potions are kept, as in vanilla
            BuiltInRegistries.POTION.listElements()
                    .forEach(potion -> out.accept(PotionContents.createItemStack(item, potion)));
            return true;
        }
        if (item == Items.ENCHANTED_BOOK) {
            holders.lookup(Registries.ENCHANTMENT).ifPresent(enchantments -> enchantments.listElements()
                    .forEach(enchantment -> out.accept(EnchantmentHelper.createBook(
                            new EnchantmentInstance(enchantment, enchantment.value().getMaxLevel())))));
            return true;
        }
        if (item == Items.GOAT_HORN) {
            holders.lookup(Registries.INSTRUMENT)
                    .flatMap(instruments -> instruments.get(InstrumentTags.GOAT_HORNS))
                    .ifPresent(horns -> horns.forEach(horn -> out.accept(InstrumentItem.create(item, horn))));
            return true;
        }
        if (item == Items.OMINOUS_BOTTLE) {
            for (int amplifier = 0; amplifier <= 4; amplifier++) {
                ItemStack bottle = new ItemStack(item);
                bottle.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifier(amplifier));
                out.accept(bottle);
            }
            return true;
        }
        if (item == Items.FIREWORK_ROCKET) {
            for (byte duration : FireworkRocketItem.CRAFTABLE_DURATIONS) {
                ItemStack rocket = new ItemStack(item);
                rocket.set(DataComponents.FIREWORKS, new Fireworks(duration, List.of()));
                out.accept(rocket);
            }
            return true;
        }
        return false;
    }
}
