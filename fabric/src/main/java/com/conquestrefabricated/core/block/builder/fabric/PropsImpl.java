package com.conquestrefabricated.core.block.builder.fabric;

import com.conquestrefabricated.core.block.data.BlockData;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class PropsImpl {
    public static void registerItemByPlatform(BlockData data) {
        Registry.register(BuiltInRegistries.ITEM, data.getRegistryName(), data.getItem());
    }
}
