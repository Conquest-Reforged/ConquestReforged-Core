package com.conquestrefabricated.core.item.group.sort;

import java.util.Comparator;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class SimpleSorter implements Sorter<ItemStack> {

    private final Comparator<ItemStack> comparator;

    public SimpleSorter(Comparator<ItemStack> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void apply(NonNullList<ItemStack> list) {
        list.sort(comparator);
    }

    @Override
    public void sort(NonNullList<ItemStack> list) {}
}
