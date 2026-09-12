package com.conquestrefabricated.api.tags;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

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
    public static final TagKey<Block> NATURAL_CHALK = blockTag("natural_chalk");
    public static final TagKey<Block> NATURAL_CALCITE = blockTag("natural_calcite");

    /**
     * Block tags Core also publishes as item tags of the same id, whether or not a recipe asks for
     * one. Most block tags are only mirrored when something is crafted from them; these are mirrored
     * always, because other tags are built out of them - {@link #LIME_SOURCES} is the union of the
     * calcareous ones, and a tag can only include tags from its own registry.
     */
    public static final List<TagKey<Block>> MIRRORED_TO_ITEMS = List.of(
            NATURAL_MARBLE, NATURAL_LIMESTONE, NATURAL_CHALK, NATURAL_CALCITE);

    /**
     * Everything that burns down to quicklime: the calcareous stones, plus vanilla calcite.
     *
     * <p>Built by Core's item tag generation as the union of the {@link #MIRRORED_TO_ITEMS} stones,
     * so a module adding its own chalk or marble joins it by tagging the block and nothing else.</p>
     */
    public static final TagKey<Item> LIME_SOURCES = itemTag("lime_sources");

    /**
     * A material's full blocks, from vanilla and from Conquest alike.
     *
     * <p>Conquest adds every shape of a family to the vanilla <i>block</i> tag, so a Conquest log
     * slab is a member of {@code #minecraft:logs}. That is right for mining and burning and wrong for
     * an ingredient - feeding a slab back in is a way to lose material. These unions put the two
     * halves together without either problem:</p>
     *
     * <ul>
     *   <li>{@code #minecraft:logs} - the vanilla <i>item</i> tag, which Conquest never writes to,
     *       so it is exactly the vanilla logs.</li>
     *   <li>{@code #minecraft:logs/bases} - the family parents Core mirrors out of the block tag,
     *       shapes excluded. Optional, since it only exists where a module generated it.</li>
     * </ul>
     *
     * <p>Hand-written under {@code resources}, not generated, so a data generation run cannot
     * overwrite them.</p>
     */
    public static final TagKey<Item> LOG_BASES = itemTag("log_bases");
    /** @see #LOG_BASES */
    public static final TagKey<Item> PLANK_BASES = itemTag("plank_bases");
    /** @see #LOG_BASES */
    public static final TagKey<Item> DIRT_BASES = itemTag("dirt_bases");

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