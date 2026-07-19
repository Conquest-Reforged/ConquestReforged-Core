package com.conquestrefabricated.content.blocks.init.neoforge;

import com.conquestrefabricated.content.blocks.group.ModGroups;
import com.conquestrefabricated.content.blocks.init.BlockFamilyInit;
import com.conquestrefabricated.content.blocks.init.BlockGroupInit;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.blocks.tileentity.enchantment.ModdedTileEntityEnchanterRenderer;
import com.conquestrefabricated.core.group.neoforge.FamilyGroup;
import com.conquestrefabricated.core.item.family.DeferredFamilyRegistry;
import com.conquestrefabricated.core.util.log.Log;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class BlockClientInit {

    public static void client() {
        Log.info("Initializing ModItemGroups");
        //ModGroups.initGroups();
    }

    @SubscribeEvent
    public static void clientBlockEntities(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TileEntityTypes.ENCHANTING_TABLE, ModdedTileEntityEnchanterRenderer::new);
    }

    @SubscribeEvent
    public static void common(FMLClientSetupEvent event) {
        Log.info("Initializing Block/Item families");
        BlockGroupInit.init();
        BlockFamilyInit.init();
        DeferredFamilyRegistry.BLOCKS.registerAll();
    }

    @SubscribeEvent
    public static void complete(FMLLoadCompleteEvent event) {
        Log.info("Setting up GroupManager");
        FamilyGroup.setAddRootItems();
    }
}