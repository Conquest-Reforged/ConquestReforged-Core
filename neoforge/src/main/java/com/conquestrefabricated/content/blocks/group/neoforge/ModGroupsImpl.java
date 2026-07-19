package com.conquestrefabricated.content.blocks.group.neoforge;

import com.conquestrefabricated.content.blocks.group.ModGroups;
import com.conquestrefabricated.core.group.neoforge.FamilyGroup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ModGroupsImpl {
    public static CreativeModeTab createFamilyGroup(int order, String label, Supplier<ItemStack> icon) {
        ModGroups.toggleRow(order);
        FamilyGroup group = new FamilyGroup(order, label, icon, ModGroups.rowValue, (order) % 5 + 1, CreativeModeTab.Type.CATEGORY, Component.nullToEmpty(label), (displayContext, entries) -> {
            NonNullList<ItemStack> items = NonNullList.create();
            // can't call group.populate(items) here since `group` doesn't exist yet at construction time
        });
        return group;
    }
}
