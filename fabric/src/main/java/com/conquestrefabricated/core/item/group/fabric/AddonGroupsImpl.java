package com.conquestrefabricated.core.item.group.fabric;

import com.conquestrefabricated.content.blocks.group.ModGroups;
import com.conquestrefabricated.core.group.fabric.FamilyGroup;

import java.util.ArrayList;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AddonGroupsImpl {

    public static CreativeModeTab createByPlatform(String namespace, String label, int order, Supplier<ItemStack> icon) {
        ResourceKey<CreativeModeTab> key = ResourceKey.create(
                Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(namespace, label));

        ModGroups.toggleRow(order);
        FamilyGroup group = new FamilyGroup(namespace, order, label, icon, ModGroups.rowValue,
                order % 5 + 1, CreativeModeTab.Type.CATEGORY, Component.nullToEmpty(label),
                (displayContext, entries) -> {});

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, group);

        // Wire the tab's contents here rather than leaving it to Core's client init, so an addon
        // can create tabs whether its initializer runs before or after ours.
        group.markSelfWired();
        CreativeModeTabEvents.modifyOutputEvent(key).register(entries -> {
            if (group.cached.isEmpty()) {
                NonNullList<ItemStack> list = NonNullList.create();
                group.populate(list);
                group.sorter.apply(list);
                group.sorter.sort(list);
                group.cached = new ArrayList<>(list);
            }
            for (ItemStack stack : group.cached) {
                if (stack.getItem() != Items.AIR) {
                    entries.accept(stack);
                }
            }
        });

        return group;
    }
}
