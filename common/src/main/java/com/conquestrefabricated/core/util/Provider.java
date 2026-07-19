package com.conquestrefabricated.core.util;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

/**
 * Used to provide references to objects that can be used during initialization
 * with out creating circular dependency issues (ie Block constructors that rely on
 * Items and vice-versa
 */
public class Provider<T extends ItemLike> implements ItemLike {

    private final String name;
    private final Supplier<T> supplier;
    private final Supplier<T> defaultValue;

    private T value;

    private Provider(String name, Supplier<T> supplier, Supplier<T> defaultValue) {
        this.name = name;
        this.supplier = supplier;
        this.defaultValue = defaultValue;
    }

    public T get() {
        if (value == null) {
            value = supplier.get();
            if (value == null) {
                //new NullPointerException("Invalid item: " + name).printStackTrace();
                value = defaultValue.get();
            }
        }
        return value;
    }

    public Stack toStack() {
        return new Stack(this);
    }

    @Override
    public net.minecraft.world.item.Item asItem() {
        T t = get();
        if (t == null) {
            throw new NullPointerException("Invalid item: " + name);
        }
        return t.asItem();
    }

    public static Block block(String name) {
        return block(Identifier.parse(name));
    }

    public static Block block(Identifier name) {
        return block("" + name, () -> BuiltInRegistries.BLOCK.get(name)
                .map(Holder.Reference::value)
                .orElse(null));
    }

    public static Block block(String name, Supplier<net.minecraft.world.level.block.Block> getter) {
        return new Block(name, getter);
    }

    public static Item item(String name) {
        return item(Identifier.parse(name));
    }

    public static Item item(Identifier name) {
        return item("" + name, () -> BuiltInRegistries.ITEM.get(name)
                .map(Holder.Reference::value)
                .orElse(null));
    }

    public static Item item(String name, Supplier<net.minecraft.world.item.Item> getter) {
        return new Item(name, getter);
    }

    public static class Block extends Provider<net.minecraft.world.level.block.Block> {

        public Block(String name, Supplier<net.minecraft.world.level.block.Block> supplier) {
            super(name, supplier, () -> Blocks.AIR);
        }
    }

    public static class Item extends Provider<net.minecraft.world.item.Item> {

        public Item(String name, Supplier<net.minecraft.world.item.Item> supplier) {
            super(name, supplier, () -> Items.AIR);
        }
    }

    public static class Stack implements Supplier<ItemStack> {

        private final ItemLike provider;

        public Stack(ItemLike provider) {
            this.provider = provider;
        }

        @Override
        public ItemStack get() {
            return new ItemStack(provider.asItem());
        }

        public Optional<ItemStack> getSafely() {
            net.minecraft.world.item.Item item = provider.asItem();
            if (item == Items.AIR) {
                return Optional.empty();
            }
            return Optional.of(new ItemStack(item));
        }
    }
}
