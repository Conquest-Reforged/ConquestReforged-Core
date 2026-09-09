package com.conquestrefabricated.api.tags;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> PLASTER = blockTag("plaster");
    public static final TagKey<Block> VINE = blockTag("vine");
    public static final TagKey<Block> METAL = blockTag("metal");
    public static final TagKey<Block> WILLOW_LEAVES = blockTag("willow_leaves");
    public static final TagKey<Block> MOSAIC = blockTag("mosaic");
    public static final TagKey<Block> BRICKS = blockTag("bricks");
    public static final TagKey<Block> NATURALSTONES = blockTag("naturalstones");
    public static final TagKey<Block> STONE = blockTag("stone");
    public static final TagKey<Block> COBBLESTONES = blockTag("cobblestones");
    public static final TagKey<Block> GRAVELS = blockTag("gravels");

    public static final TagKey<Block> NATURAL_MARBLE = blockTag("natural_marble");
    public static final TagKey<Block> NATURAL_LIMESTONE = blockTag("natural_limestone");
    public static final TagKey<Block> NATURAL_SANDSTONE = blockTag("natural_sandstone");
    public static final TagKey<Block> NATURAL_GRANITE = blockTag("natural_granite");

    //public static final TagKey<Block> PLANT_SLOWNESS = blockTag("plant_slowness");
    public static final TagKey<Item> GARDENING_TOOLS = itemTag("gardening_tools");
    public static final TagKey<Item> CYCLING_TOOLS = itemTag("cycling_tools");

    /**
     * Creates a block tag. Accepts either a bare path, which resolves against
     * {@link Namespaces#DEFAULT}, or an explicit {@code namespace:path} for addon tags.
     */
    public static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, Namespaces.id(name));
    }

    private static TagKey<Block> fabricConventionalTag(String name) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", name));
    }

    /**
     * Creates an item tag. Accepts either a bare path, which resolves against
     * {@link Namespaces#DEFAULT}, or an explicit {@code namespace:path} for addon tags.
     */
    public static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registries.ITEM, Namespaces.id(name));
    }
}