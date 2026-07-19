package com.conquestrefabricated.api.painting;

import com.conquestrefabricated.api.painting.art.Art;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;


public interface Painting {

    String getName();

    String getTranslationKey();

    Identifier getRegistryName();

    default Identifier getItemName() {
        return getRegistryName();
    }

    default ItemStack createStack(Art art) {
        return createStack(art, 1);
    }

    default ItemStack createStack(Art art, int count) {
        Item item = BuiltInRegistries.ITEM.get(getItemName()).get().value();

        CompoundTag painting = new CompoundTag();
        painting.putString(Art.TYPE_TAG, getName());
        painting.putString(Art.ART_TAG, art.getName());

        CompoundTag data = new CompoundTag();
        data.put(Art.DATA_TAG, painting);

        ItemStack stack = new ItemStack(item, count);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        return stack;
    }
}
