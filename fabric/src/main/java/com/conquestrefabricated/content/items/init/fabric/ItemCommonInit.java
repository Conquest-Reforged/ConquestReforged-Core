package com.conquestrefabricated.content.items.init.fabric;

import com.conquestrefabricated.content.entities.painting.ModPainting;
import com.conquestrefabricated.content.entities.painting.PaintingFactory;
import com.conquestrefabricated.content.entities.painting.PaintingItem;
import com.conquestrefabricated.content.entities.painting.art.ModArt;
import com.conquestrefabricated.content.items.init.ModItems;
import com.conquestrefabricated.content.items.item.ArmorItem;
import com.conquestrefabricated.core.util.log.Log;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Consumer;


public class ItemCommonInit {

    public static void init() {
        Log.info("Registering items");

        PaintingItem painting1 = new PaintingItem(
                "painting",
                ModPainting::fromName,
                ModArt::fromName,
                PaintingFactory.MOD
        );
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("conquest", "painting"), painting1);

//        PaintingItem painting2 = new PaintingItem(
//                "vanilla_painting",
//                VanillaPainting::fromName,
//                VanillaArt::fromName,
//                PaintingFactory.VANILLA
//        );
//        Registry.register(Registries.ITEM, Identifier.of("conquest", "vanilla_painting"), painting2);

        ModItems.malletItem = new Item(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("conquest", "mallet_item")))) {
            @Override
            public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
                builder.accept(Component.translatable("tooltip.conquest.mallet_item"));
            }
        };

        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("conquest", "mallet_item"), ModItems.malletItem);
    }
}
