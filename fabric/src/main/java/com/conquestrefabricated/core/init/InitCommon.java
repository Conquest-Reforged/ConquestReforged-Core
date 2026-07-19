package com.conquestrefabricated.core.init;

import com.conquestrefabricated.core.block.BlockStats;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import com.conquestrefabricated.core.data.BlockDump;
import com.conquestrefabricated.core.data.WorldPainterGenerator;
import com.conquestrefabricated.core.init.dev.Environment;
import com.conquestrefabricated.core.item.family.FamilyRegistry;
import com.conquestrefabricated.core.util.cache.Cache;
import com.conquestrefabricated.core.util.log.Log;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;


public class InitCommon {

    public static void blocks() {
        Log.info("Registering blocks");
        BlockDataRegistry.getInstance().forEach(data -> {
            //Log.info(data.getRegistryName().toString());
            Registry.register(BuiltInRegistries.BLOCK, data.getRegistryName(), data.getBlock());
        });
    }

    public static void items() {
        Log.info("Registering block items");
        BlockDataRegistry.getInstance().forEach(data -> Registry.register(BuiltInRegistries.ITEM, data.getRegistryName(), data.getItem()));
    }


    public static void common() {
            BlockDump.run();
            WorldPainterGenerator.run();
            BlockStats stats = new BlockStats();
            Log.info("Block Stats:");
            Log.info("(Total) Blocks: {}, States: {}", stats.totalBlocks, stats.totalStates);
            Log.info("(Vanilla) Blocks: {}, States: {}", stats.vanillaBlocks, stats.vanillaStates);
            Log.info("(Conquest) Blocks: {}, States: {}", stats.conquestBlocks, stats.conquestStates);
    }

    public static void complete() {
        if (Environment.isProduction()) {
            Log.info("Load complete. Clearing loading caches");
            Cache.clearAll();
            FamilyRegistry.bake();
            BlockDataRegistry.getInstance().dispose();
        }
    }
}
