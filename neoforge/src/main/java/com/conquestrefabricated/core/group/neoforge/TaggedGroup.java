package com.conquestrefabricated.core.group.neoforge;


import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public abstract class TaggedGroup<T extends TaggedGroup> extends ConquestItemGroup {

    private final List<TagKey<Block>> blocks = new LinkedList<>();
    private final List<TagKey<Item>> items = new LinkedList<>();

    public TaggedGroup(int index, String label, Row row, int column, Type type, Component text, Supplier<ItemStack> icon, DisplayItemsGenerator entryCollector) {
        super(index, label, row, column, type, text, icon, entryCollector);
    }

    public abstract T self();

    @SafeVarargs
    public final T blocks(TagKey<Block>... blocks) {
        Collections.addAll(this.blocks, blocks);
        return self();
    }

    @SafeVarargs
    public final T items(TagKey<Item>... items) {
        Collections.addAll(this.items, items);
        return self();
    }

    public void addTaggedBlocks(NonNullList<ItemStack> items) {
        for (TagKey<Block> tag : this.blocks) {
            //tag.values().forEach(block -> block.appendStacks(this, items));
        }
    }

    public void addTaggedItems(NonNullList<ItemStack> items) {
        for (TagKey<Item> tag : this.items) {
            //tag.values().forEach(item -> item.appendStacks(this, items));
        }
    }
}
