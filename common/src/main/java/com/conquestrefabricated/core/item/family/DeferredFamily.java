package com.conquestrefabricated.core.item.family;

import com.conquestrefabricated.core.util.OptimizedList;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class DeferredFamily<T extends ItemLike> extends Family<T> {

    private final T empty;
    private final Identifier name;
    private final FamilyRegistry<T> registry;

    DeferredFamily(Identifier name, T empty, FamilyRegistry<T> registry) {
        super(CreativeModeTabs.searchTab(), new OptimizedList<>());
        this.name = name;
        this.empty = empty;
        this.registry = registry;
    }

    @Override
    protected T emptyValue() {
        return empty;
    }

    @Override
    protected void addItem(CreativeModeTab group, NonNullList<ItemStack> list, T item) {

    }

    @Override
    public boolean isAbsent() {
        return true;
    }

    public void register() {
        if (name != null) {
            Family<T> family = registry.getFamily(name);
            for (T block : getMembers()) {
                family.add(block);
                registry.register(block, family);
            }
        }
    }
}
