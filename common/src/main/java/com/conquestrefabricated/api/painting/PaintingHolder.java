package com.conquestrefabricated.api.painting;

import com.conquestrefabricated.api.painting.art.Art;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public interface PaintingHolder {

    Art<?> getArt(ItemStack stack);

    Painting getType(ItemStack stack);

//    static String getArtData(ItemStack stack) {
//        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
//        if (customData == null) return null;
//        CompoundTag nbt = customData.copyTag();
//        return nbt.getCompound(Art.DATA_TAG).getString(Art.ART_TAG);
//    }
//
//    static String getTypeData(ItemStack stack) {
//        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
//        if (customData == null) return null;
//        CompoundTag nbt = customData.copyTag();
//        return nbt.getCompound(Art.DATA_TAG).getString(Art.TYPE_TAG);
//    }
}
