package com.conquestrefabricated.core.item.family.item;

import com.conquestrefabricated.core.item.family.Family;
import com.conquestrefabricated.core.util.OptimizedList;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;

public class ItemFamily extends Family<Item> {

    public static final ItemFamily EMPTY = new ItemFamily(CreativeModeTabs.searchTab());

    public ItemFamily(CreativeModeTab group) {
        super(group, new OptimizedList<>());
    }

    @Override
    protected Item emptyValue() {
        return Items.AIR;
    }

    @Override
    protected void addItem(CreativeModeTab group, NonNullList<ItemStack> list, Item item) {
            list.add(new ItemStack(item));
    }

    @Override
    public boolean isAbsent() {
        return this == EMPTY;
    }
}
