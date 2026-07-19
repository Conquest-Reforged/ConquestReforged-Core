package com.conquestrefabricated.core.item.family.block;

import com.conquestrefabricated.core.block.factory.TypeList;
import com.conquestrefabricated.core.item.family.Family;
import com.conquestrefabricated.core.util.OptimizedList;
import java.util.Collections;
import java.util.Comparator;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BlockFamily extends Family<Block> {

    public static final BlockFamily EMPTY = new BlockFamily();

    private BlockFamily() {
        super(BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.SEARCH).get().value(), BlockFamily.BY_NAME, Collections.emptyList());
    }

    public BlockFamily(CreativeModeTab group, TypeList order) {
        super(group, order, new OptimizedList<>());
    }

    @Override
    protected Block emptyValue() {
        return Blocks.AIR;
    }

    @Override
    protected void addItem(CreativeModeTab group, NonNullList<ItemStack> list, Block block) {
        list.add(block.asItem().getDefaultInstance());
    }

    @Override
    public boolean isAbsent() {
        return this == EMPTY;
    }

    private static final Comparator<Block> BY_NAME = (b1, b2) -> {
        String name1 = BuiltInRegistries.ITEM.getKey(b1.asItem()).getPath();
        String name2 = BuiltInRegistries.ITEM.getKey(b2.asItem()).getPath();
        return name1.compareTo(name2);
    };
}
