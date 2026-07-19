package com.conquestrefabricated.content.blocks.init.fabric;

import com.conquestrefabricated.content.blocks.group.ModGroups;
import com.conquestrefabricated.content.blocks.init.BlockFamilyInit;
import com.conquestrefabricated.content.blocks.init.BlockGroupInit;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.blocks.tileentity.enchantment.ModdedTileEntityEnchanterRenderer;
import com.conquestrefabricated.core.client.color.BlockColors;
import com.conquestrefabricated.core.group.fabric.FamilyGroup;
import com.conquestrefabricated.core.item.family.DeferredFamilyRegistry;
import com.conquestrefabricated.core.util.log.Log;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

import java.util.List;

import static net.minecraft.world.level.block.Blocks.*;

@Environment(EnvType.CLIENT)
public class BlockClientInit {

    public static void client() {
        Log.info("Initializing ModItemGroups");
        ModGroups.initGroups();
    }

    //@SubscribeEvent
    public static void  clientBlockEntities() {
        BlockEntityRendererRegistry.register(TileEntityTypes.ENCHANTING_TABLE, ModdedTileEntityEnchanterRenderer::new);
    }

    //@SubscribeEvent
    public static void common() {
        Log.info("Initializing Block/Item families");
        BlockGroupInit.init();
        BlockFamilyInit.init();
        DeferredFamilyRegistry.BLOCKS.registerAll();
    }

    //@SubscribeEvent
    public static void complete() {
        Log.info("Setting up GroupManager");
        FamilyGroup.setAddRootItems();
        //ItemGroupManager.getInstance().setVisibleItemGroups(GroupType.CONQUEST, GroupType.OTHER);
    }

    //@SubscribeEvent
    public static void blockColors() {
        Log.info("Registering additional vanilla block colors");
        BlockColorRegistry.register(List.of(BlockColors.GRASS), INFESTED_MOSSY_STONE_BRICKS, MOSSY_STONE_BRICKS, MOSSY_STONE_BRICK_SLAB, MOSSY_STONE_BRICK_STAIRS);
    }

    //@SubscribeEvent
//    public static void items(net.minecraft.client.color.block.BlockColors blockColors) {
//        Log.info("Registering additional vanilla item colors");
//       ColorProviderRegistry.ITEM.register(BlockColors.toItemColor(blockColors), INFESTED_MOSSY_STONE_BRICKS, MOSSY_STONE_BRICKS, MOSSY_STONE_BRICK_SLAB, MOSSY_STONE_BRICK_STAIRS);
//    }
}
