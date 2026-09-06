package com.conquestrefabricated.core.init;

import com.conquestrefabricated.core.item.group.sort.GroupFiles;
import com.conquestrefabricated.core.item.group.sort.Sorter;
import com.conquestrefabricated.core.util.Provider;
import com.conquestrefabricated.mixin.CreativeModeTabAccessor;
import com.conquestrefabricated.mixin.CreativeModeTabsAccessor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;


@EventBusSubscriber(modid = "conquest", value = Dist.CLIENT)
public class VanillaTabReorganizer {

    private static boolean initialized = false;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            setIcon(CreativeModeTabs.BUILDING_BLOCKS, "conquest:lime_mortar_masonry");
            setIcon(CreativeModeTabs.COLORED_BLOCKS, "conquest:schist_dragon_head");
            setIcon(CreativeModeTabs.NATURAL_BLOCKS, "minecraft:chiseled_stone_bricks");
            setIcon(CreativeModeTabs.FUNCTIONAL_BLOCKS, "conquest:andalusian_mosaic");
            setIcon(CreativeModeTabs.REDSTONE_BLOCKS, "conquest:etruscan_wall_design_1");
            setIcon(CreativeModeTabs.TOOLS_AND_UTILITIES, "conquest:tudor_cross_frame");
            setIcon(CreativeModeTabs.COMBAT, "conquest:terracotta_imbrices_and_tegulae");
            setIcon(CreativeModeTabs.FOOD_AND_DRINKS, "minecraft:oak_planks");
            setIcon(CreativeModeTabs.INGREDIENTS, "conquest:carved_oak_wood");
            setIcon(CreativeModeTabs.SPAWN_EGGS, "minecraft:iron_block");

            replaceVanillaTab(CreativeModeTabs.BUILDING_BLOCKS, "a_cobble_and_brick");
            replaceVanillaTab(CreativeModeTabs.COLORED_BLOCKS, "aa_advanced_masonry");
            replaceVanillaTab(CreativeModeTabs.NATURAL_BLOCKS, "bb_columns");
            replaceVanillaTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, "c_mosaics_tiles_and_floors");
            replaceVanillaTab(CreativeModeTabs.REDSTONE_BLOCKS, "cc_plaster_stucco_and_paint");
            replaceVanillaTab(CreativeModeTabs.TOOLS_AND_UTILITIES, "d_half_timbered_walls");
            replaceVanillaTab(CreativeModeTabs.COMBAT, "dd_roofing");
            replaceVanillaTab(CreativeModeTabs.FOOD_AND_DRINKS, "e_planks_and_beams");
            replaceVanillaTab(CreativeModeTabs.INGREDIENTS, "ee_advanced_carpentry");
            replaceVanillaTab(CreativeModeTabs.SPAWN_EGGS, "f_metal");

            CreativeModeTabsAccessor.conquest$setCachedParameters(null);
            initialized = true;
        });
    }

    private static void setIcon(ResourceKey<CreativeModeTab> tabKey, String blockId) {
        CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(tabKey);
        CreativeModeTabAccessor accessor = (CreativeModeTabAccessor) tab;
        accessor.conquest$setIcon(() -> Provider.block(blockId).toStack().get());
    }


    private static final java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger(0);

    private static void replaceVanillaTab(ResourceKey<CreativeModeTab> tabKey, String sortFileLabel) {
        CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(tabKey);
        CreativeModeTabAccessor accessor = (CreativeModeTabAccessor) tab;

        Sorter<ItemStack> sorter = GroupFiles.loadSorter(sortFileLabel);

        accessor.conquest$setDisplayItemsGenerator((parameters, output) -> {
            int count = counter.incrementAndGet();
            if (count % 20 == 0) {
                System.out.println("Generator called " + count + " times, latest tab: " + tabKey);
            }
            NonNullList<ItemStack> list = NonNullList.create();
            sorter.apply(list);
            sorter.sort(list);
            list.forEach(stack -> output.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
        });
    }


}
