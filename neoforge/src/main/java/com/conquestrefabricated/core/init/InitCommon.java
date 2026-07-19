package com.conquestrefabricated.core.init;

import com.conquestrefabricated.core.block.BlockStats;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import com.conquestrefabricated.core.item.family.FamilyRegistry;
import com.conquestrefabricated.core.util.cache.Cache;
import com.conquestrefabricated.core.util.log.Log;
import com.conquestrefabricated.core.init.dev.Environment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

@EventBusSubscriber(modid = "conquest")
public class InitCommon {
/*
    @SubscribeEvent
    public static void blocks(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BLOCKS, blockRegisterHelper -> {
            Log.info("Registering blocks");
            BlockDataRegistry.getInstance().forEach(data -> {
                blockRegisterHelper.register(data.getRegistryName(), data.getBlock());
            });
        });

    }

    @SubscribeEvent
    public static void items(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, itemRegisterHelper -> {
            Log.info("Registering block items");
            BlockDataRegistry.getInstance().forEach(data -> {
                System.out.println(data.getBlockName());
                itemRegisterHelper.register(data.getRegistryName(), data.getItem());
            });
        });

    }
*/
    @SubscribeEvent
    public static void common(FMLCommonSetupEvent event) {
        //BlockDump.run();
        //WorldPainterGenerator.run();
        BlockStats stats = new BlockStats();
        Log.info("Block Stats:");
        Log.info("(Total) Blocks: {}, States: {}", stats.totalBlocks, stats.totalStates);
        Log.info("(Vanilla) Blocks: {}, States: {}", stats.vanillaBlocks, stats.vanillaStates);
        Log.info("(Conquest) Blocks: {}, States: {}", stats.conquestBlocks, stats.conquestStates);
    }

    @SubscribeEvent
    public static void complete(FMLLoadCompleteEvent event) {
        if (Environment.isProduction()) {
            Log.info("Load complete. Clearing loading caches");
            Cache.clearAll();
            FamilyRegistry.bake();
            BlockDataRegistry.getInstance().dispose();
        }
    }
}
