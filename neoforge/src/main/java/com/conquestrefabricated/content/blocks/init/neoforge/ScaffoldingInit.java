package com.conquestrefabricated.content.blocks.init.neoforge;

import com.conquestrefabricated.content.blocks.block.decor.Scaffolding;
import com.conquestrefabricated.content.items.item.ScaffoldingItem;
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

public class ScaffoldingInit {

    public static final Block METAL_SCAFFOLDING = createBlock("metal_scaffolding", SoundType.METAL);
    public static final Block WOOD_SCAFFOLDING = createBlock("wood_scaffolding", SoundType.WOOD);

    public static void registerBlocks() {
        registerBlock("metal_scaffolding", METAL_SCAFFOLDING);
        registerBlock("wood_scaffolding", WOOD_SCAFFOLDING);
    }

    public static void registerItems() {
        registerItem("metal_scaffolding", createItem(METAL_SCAFFOLDING, "metal_scaffolding"));
        registerItem("wood_scaffolding", createItem(WOOD_SCAFFOLDING, "wood_scaffolding"));
    }

    private static void registerBlock(String name, Block block) {
        Identifier id = Identifier.fromNamespaceAndPath(Context.getInstance().getNamespace(), name);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    private static void registerItem(String name, Item item) {
        Identifier id = Identifier.fromNamespaceAndPath(Context.getInstance().getNamespace(), name);
        Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    private static Scaffolding createBlock(String name, SoundType soundType) {
        Scaffolding block = new Scaffolding(
                BlockBehaviour.Properties.of()
                        .strength(0.5F)
                        .sound(soundType)
                        .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("conquest", name)))
        );
        // block.setRegistryName(Context.getInstance().getNamespace(), name);
        return block;
    }

    private static Item createItem(Block block, String name) {
        Item item = new ScaffoldingItem(block, (new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("conquest", name)))));
        return item;
    }
}
