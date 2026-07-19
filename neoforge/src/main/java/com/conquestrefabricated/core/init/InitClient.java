package com.conquestrefabricated.core.init;

import com.conquestrefabricated.api.painting.PaintingHolder;
import com.conquestrefabricated.api.painting.art.Art;
import com.conquestrefabricated.content.entities.painting.ModPainting;
import com.conquestrefabricated.content.entities.painting.art.ArtType;
import com.conquestrefabricated.content.entities.painting.art.ModArt;
import com.conquestrefabricated.core.block.data.BlockData;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import com.conquestrefabricated.core.block.data.ColorType;
import com.conquestrefabricated.core.client.color.BlockColors;
import com.conquestrefabricated.core.group.neoforge.FamilyGroup;
import com.conquestrefabricated.core.item.group.sort.ItemList;
import com.conquestrefabricated.core.item.group.sort.Sorter;
import com.conquestrefabricated.core.util.Provider;
import com.conquestrefabricated.core.util.log.Log;
import com.conquestrefabricated.mixin.CreativeModeTabAccessor;
import com.conquestrefabricated.mixin.CreativeModeTabsAccessor;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@EventBusSubscriber(value = Dist.CLIENT)
public class InitClient {

    @SubscribeEvent
    public static void blockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        Log.debug("Registering block colors");
        for (BlockData data : BlockDataRegistry.getInstance()) {
            if (data.getProps().getColorType() == ColorType.GRASS) {
                event.register(List.of(BlockColors.GRASS), data.getBlock());
            } else if (data.getProps().getColorType() == ColorType.FOLIAGE) {
                event.register(List.of(BlockColors.FOLIAGE), data.getBlock());
            } else if (data.getProps().getColorType() == ColorType.WATER) {
                event.register(List.of(BlockColors.WATER), data.getBlock());
            }
        }
    }

/*    @SubscribeEvent
    public static void itemColors(RegisterColorHandlersEvent.ItemTintSources event) {
        Log.debug("Registering item colors");

        for (BlockData data : BlockDataRegistry.getInstance()) {
            if (data.getProps().getColorType() == ColorType.GRASS || data.getProps().getColorType() == ColorType.FOLIAGE) {
                event.register((stack, tintIndex) -> 0x6c994b, data.getItem());
            }
        }
    }*/

//    @SubscribeEvent
//    public static void init(FMLClientSetupEvent event) {
//        Log.debug("Registering block render layers");
//        BlockDataRegistry.getInstance().forEach(BlockData::addRenders);
//    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        FamilyGroup.FAMILY_GROUPS.forEach(familyGroup -> {
            if (familyGroup.cached.isEmpty()) {
                NonNullList<ItemStack> list = NonNullList.create();
                familyGroup.populate(list);
                familyGroup.sorter.apply(list);
                if (event.getTab().equals(CreativeModeTabs.SEARCH)) {
                    event.acceptAll(list);
                }
                familyGroup.sorter.sort(list);
                familyGroup.cached = new ArrayList<>(list);
            }

            if (event.getTabKey().equals(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(familyGroup))) {
                for (ItemStack item : familyGroup.cached) {
                    if (item.getItem() != Items.AIR) {
                        if (item.getItem() instanceof PaintingHolder) {
                            Art<?> art = ModArt.of(ArtType.A1x1_0);
                            ModPainting.getIds().distinct().sorted().forEach(name -> {
                                ModPainting type = ModPainting.fromName(name);
                                ItemStack stack = type.createStack(art);
                                event.accept(stack);
                            });
                            continue;
                        }

                        if (item.getItem() instanceof TippedArrowItem) {
                            BuiltInRegistries.POTION.listElements().forEach(potionEntry -> {
                                Potion potion = potionEntry.value();
                                if (!potion.getEffects().isEmpty()) {
                                    ItemStack arrow = new ItemStack(item.getItem());
                                    arrow.set(DataComponents.POTION_CONTENTS, new PotionContents(potionEntry));
                                    event.accept(arrow);
                                }
                            });
                            continue;
                        }

                        event.accept(item.getItem());
                    }
                }
            }

        });
    }
}
