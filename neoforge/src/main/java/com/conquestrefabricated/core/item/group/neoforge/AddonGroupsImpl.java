package com.conquestrefabricated.core.item.group.neoforge;

import com.conquestrefabricated.content.blocks.group.ModGroups;
import com.conquestrefabricated.core.group.neoforge.FamilyGroup;

import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class AddonGroupsImpl {

    public static CreativeModeTab createByPlatform(String namespace, String label, int order, Supplier<ItemStack> icon) {
        ModGroups.toggleRow(order);

        // ConquestItemGroup builds its own display-items generator, so the tab fills itself.
        // Registration happens in ModGroupsEvent, which walks FAMILY_GROUPS and uses each
        // group's own namespace.
        return new FamilyGroup(namespace, order, label, icon, ModGroups.rowValue,
                order % 5 + 1, CreativeModeTab.Type.CATEGORY, Component.nullToEmpty(label),
                (displayContext, entries) -> {});
    }
}
