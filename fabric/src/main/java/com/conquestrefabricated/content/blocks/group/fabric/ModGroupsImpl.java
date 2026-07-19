package com.conquestrefabricated.content.blocks.group.fabric;

import com.conquestrefabricated.content.blocks.group.ModGroups;
import com.conquestrefabricated.core.group.fabric.FamilyGroup;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModGroupsImpl {
    public static CreativeModeTab createFamilyGroup(int order, String label, Supplier<ItemStack> icon) {
        ResourceKey<CreativeModeTab> groupKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath("conquest", label));

        ModGroups.toggleRow(order);
        FamilyGroup group = new FamilyGroup(order, label, icon, ModGroups.rowValue, (order) % 5 + 1, CreativeModeTab.Type.CATEGORY, Component.nullToEmpty(label), (displayContext, entries) -> {});


        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, groupKey, group); //Line of code unique to Fabric
        return group;
    }
}
