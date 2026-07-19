package com.conquestrefabricated.content.items.init.neoforge;

import com.conquestrefabricated.content.entities.painting.ModPainting;
import com.conquestrefabricated.content.entities.painting.PaintingFactory;
import com.conquestrefabricated.content.entities.painting.PaintingItem;
import com.conquestrefabricated.content.entities.painting.art.ModArt;
import com.conquestrefabricated.content.items.init.ModItems;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(modid = "conquest")
public class ItemCommonInit {

    @SubscribeEvent
    public static void init(RegisterEvent event) {
        event.register(BuiltInRegistries.ITEM.key(), itemRegisterHelper -> {
            Log.info("Registering items");

            // Create the mallet item
            ModItems.malletItem = new Item(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("conquest", "mallet_item")))) {
                @Override
                public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
                    builder.accept(Component.translatable("tooltip.conquest.mallet_item"));
                }
            };

            PaintingItem painting1 = new PaintingItem(
                    "painting",
                    ModPainting::fromName,
                    ModArt::fromName,
                    PaintingFactory.MOD
            );
            Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("conquest", "painting"), painting1);

//            PaintingItem painting2 = new PaintingItem(
//                    "vanilla_painting",
//                    VanillaPainting::fromName,
//                    VanillaArt::fromName,
//                    PaintingFactory.VANILLA
//            );
//            itemRegisterHelper.register("vanilla_painting", painting2);

            Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("conquest", "mallet_item"), ModItems.malletItem);
        });

    }
}
