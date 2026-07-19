package com.conquestrefabricated.core.block.data.fabric;

import com.conquestrefabricated.core.block.data.BlockData;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class BlockDataImpl {
    public static void registerBlock(BlockData blockData) {
        Registry.register(BuiltInRegistries.BLOCK, blockData.registryName, blockData.block);
    }
}
