package com.conquestrefabricated.content.blocks.init.neoforge;

import com.conquestrefabricated.content.blocks.block.plants.LilyPad;
import com.conquestrefabricated.content.blocks.block.plants.LilyPadToggle2;
import com.conquestrefabricated.content.items.item.LilypadItem;
import com.conquestrefabricated.core.init.Context;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LiliesInit {

    public static final Block WHITE_WATER_LILIES = createBlock("white_water_lilies", SoundType.GRASS, false);
    public static final Block BIG_WATER_LILIES = createBlock("big_water_lilies", SoundType.GRASS, false);
    public static final Block DUCKWEED = createLilyToggle2Block("duckweed", SoundType.GRASS, false);
    public static final Block FLOATING_ICE = createBlock("floating_ice", SoundType.GLASS, false);

    public static void registerBlocks() {
        registerBlock("white_water_lilies", WHITE_WATER_LILIES);
        registerBlock("big_water_lilies", BIG_WATER_LILIES);
        registerBlock("duckweed", DUCKWEED);
        registerBlock("floating_ice", FLOATING_ICE);
    }

    public static void registerItems() {
        registerItem("white_water_lilies", createItem(WHITE_WATER_LILIES, "white_water_lilies"));
        registerItem("big_water_lilies", createItem(BIG_WATER_LILIES, "big_water_lilies"));
        registerItem("duckweed", createItem(DUCKWEED, "duckweed"));
        registerItem("floating_ice", createItem(FLOATING_ICE, "floating_ice"));
    }

    private static void registerBlock(String name, Block block) {
        Identifier id = Identifier.fromNamespaceAndPath(Context.getInstance().getNamespace(), name);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    private static void registerItem(String name, Item item) {
        Identifier id = Identifier.fromNamespaceAndPath(Context.getInstance().getNamespace(), name);
        Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    private static LilyPadToggle2 createLilyToggle2Block(String name, SoundType soundType, boolean hasCollision) {
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of()
                .strength(0.5F)
                .sound(soundType)
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("conquest", name)));
        if (!hasCollision) {
            props = props.noCollision();
        }

        LilyPadToggle2 block = new LilyPadToggle2(props);
        //  block.setRegistryName(Context.getInstance().getNamespace(), name);
        return block;
    }


    private static LilyPad createBlock(String name, SoundType soundType, boolean hasCollision) {
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of()
                .strength(0.5F)
                .sound(soundType)
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("conquest", name)));
        if (!hasCollision) {
            props = props.noCollision();
        }

        LilyPad block = new LilyPad(props);
        //  block.setRegistryName(Context.getInstance().getNamespace(), name);
        return block;
    }

    private static Item createItem(Block block, String name) {
        Item item = new LilypadItem(block, (new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("conquest", name)))));
        return item;
    }
}
