package com.conquestrefabricated.content.items.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;


@Environment(EnvType.CLIENT)
public class ItemClientInit {


//TODO make sure that painting items are alright, I'm not sure if this class is even necessary at this point.

//    public static void setup(Minecraft client) {
//        registerModel("conquest:painting", "conquest:painting", client);
//        registerModel("conquest:vanilla_painting", "minecraft:painting", client);
//    }
//
//    private static void registerModel(String item, String model, Minecraft client) {
//        registerModel(Identifier.parse(item), model, client);
//    }
//
//    private static void registerModel(Identifier item, String model, Minecraft client) {
//        registerModel(BuiltInRegistries.ITEM.get(item).get().value(), model,client);
//    }
//
//    private static void registerModel(Item item, String model, Minecraft client) {
//        ModelIdentifier modelLocation = new ModelIdentifier(Identifier.parse(model), "inventory");
//        client.getItemRenderer().getItemModelShaper().register(item, modelLocation);
//    }
}
