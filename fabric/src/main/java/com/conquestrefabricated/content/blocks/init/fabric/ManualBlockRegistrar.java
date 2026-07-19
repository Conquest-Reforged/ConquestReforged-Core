package com.conquestrefabricated.content.blocks.init.fabric;


public class ManualBlockRegistrar {

    //@SubscribeEvent
    public static void manualBlocks() {
        com.conquestrefabricated.core.util.log.Log.info("Registering Lilies + Scaffolding blocks");
        // lilly blocks and scaffolding are registered manually
        //LiliesInit.registerBlocks();
        //ScaffoldingInit.registerBlocks();
    }

    //@SubscribeEvent
    public static void manualItems() {
        com.conquestrefabricated.core.util.log.Log.info("Registering Lilies + Scaffolding block items");
        // lilly and scaffolding  items are registered manually
        //LiliesInit.registerItems();
        //ScaffoldingInit.registerItems();
    }
}