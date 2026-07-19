package com.conquestrefabricated.core.item.group.sort;

import net.minecraft.core.NonNullList;

public interface Sorter<T> {

    Sorter NONE = new Sorter() {
        @Override
        public void apply(NonNullList list) {
            // Method implementation
        }

        @Override
        public void sort(NonNullList list) {
            // Missing method implementation
        }
    };

    void apply(NonNullList<T> list);

    void sort(NonNullList<T> list);

    @SuppressWarnings("unchecked")
    static <T> Sorter<T> none() {
        return (Sorter<T>) NONE;
    }
}
