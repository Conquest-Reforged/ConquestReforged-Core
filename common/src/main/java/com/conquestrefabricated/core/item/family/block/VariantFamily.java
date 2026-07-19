package com.conquestrefabricated.core.item.family.block;

import com.conquestrefabricated.core.block.factory.TypeList;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

public class VariantFamily extends BlockFamily {

    private static final VariantFamily EMPTY = new VariantFamily();

    private VariantFamily() {
        super(CreativeModeTabs.searchTab(), TypeList.EMPTY);
    }

    public VariantFamily(CreativeModeTab group, TypeList type) {
        super(group, type);
    }

    @Override
    public void addRootItem(CreativeModeTab group, NonNullList<ItemStack> list) {
        if (group == CreativeModeTabs.searchTab() || group == getGroup()) {
            list.add(new ItemStack(getRoot()));
        }
    }

    @Override
    public boolean isAbsent() {
        return this == EMPTY;
    }
}
