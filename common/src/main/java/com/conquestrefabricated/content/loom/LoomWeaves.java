package com.conquestrefabricated.content.loom;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;

/**
 * Which weave a loom draws for a given product, and the sprites those weaves are cut from.
 *
 * <p>A loom's block entity stores the product as an item id string, and the block models pick a
 * sprite out of {@link #TEXTURE_NAMES} by index. Both halves of that table used to be copied into
 * the fabric and neoforge model classes; they live here instead so the two loaders, the block entity
 * and the block itself all agree on what a given loom looks like.</p>
 *
 * <p>The ids and their indices are exactly the ones looms have been saving since 1.20, so a loom
 * placed before the weaving station existed still renders the weave it was set to.</p>
 */
public final class LoomWeaves {

    /** Sprite for a loom that is threaded but whose product has no weave of its own. */
    public static final int DEFAULT = 0;

    /**
     * Weave sprites, indexed by {@link #spriteIndex}. Resolved under
     * {@code conquest:block/7_tools/3_utility/loom/weaves/<size>/}.
     *
     * <p>Index 0 and index 5 are both plain white: 0 is the fallback for an unknown product, 5 is
     * white canvas proper. The models also use index 5 as the reference sprite they remap from.</p>
     */
    public static final String[] TEXTURE_NAMES = {
            "loom_weave_white", "loom_weave_red", "loom_weave_black", "loom_weave_gray",
            "loom_weave_light_gray", "loom_weave_white", "loom_weave_brown", "loom_weave_yellow",
            "loom_weave_orange", "loom_weave_pink", "loom_weave_magenta", "loom_weave_purple",
            "loom_weave_blue", "loom_weave_light_blue", "loom_weave_cyan", "loom_weave_green",
            "loom_weave_lime", "loom_weave_baotao", "loom_weave_berber", "loom_weave_black_persian",
            "loom_weave_blue_nain", "loom_weave_brown_oriental", "loom_weave_celtic_knot",
            "loom_weave_kashmiri", "loom_weave_kazakh", "loom_weave_kilim", "loom_weave_nahavand",
            "loom_weave_red_and_blue_sarouk", "loom_weave_red_oriental", "loom_weave_red_pazyryk",
            "loom_weave_shirishabad", "loom_weave_william_morris", "loom_weave_yellow_red_persian"
    };

    /** The product a model remaps every other weave from, so its UVs line up. */
    public static final String REFERENCE_PRODUCT = "conquest:white_canvas";

    private static final Map<String, Integer> BY_PRODUCT = Map.ofEntries(
            Map.entry("conquest:red_canvas", 1),
            Map.entry("conquest:black_canvas", 2),
            Map.entry("conquest:gray_canvas", 3),
            Map.entry("conquest:light_gray_canvas", 4),
            Map.entry("conquest:white_canvas", 5),
            Map.entry("conquest:brown_canvas", 6),
            Map.entry("conquest:yellow_canvas", 7),
            Map.entry("conquest:orange_canvas", 8),
            Map.entry("conquest:pink_canvas", 9),
            Map.entry("conquest:magenta_canvas", 10),
            Map.entry("conquest:purple_canvas", 11),
            Map.entry("conquest:blue_canvas", 12),
            Map.entry("conquest:light_blue_canvas", 13),
            Map.entry("conquest:cyan_canvas", 14),
            Map.entry("conquest:green_canvas", 15),
            Map.entry("conquest:lime_canvas", 16),
            Map.entry("conquest:baotuo_rug", 17),
            Map.entry("conquest:berber_rug", 18),
            Map.entry("conquest:black_persian_rug", 19),
            Map.entry("conquest:blue_nain_rug", 20),
            Map.entry("conquest:brown_oriental_carpet", 21),
            Map.entry("conquest:celtic_knot_rug", 22),
            Map.entry("conquest:kashmiri_carpet", 23),
            Map.entry("conquest:kazakh_rug", 24),
            Map.entry("conquest:kilim_rug", 25),
            Map.entry("conquest:nahavand_rug", 26),
            Map.entry("conquest:red_and_blue_sarouk_rug", 27),
            Map.entry("conquest:red_oriental_carpet", 28),
            Map.entry("conquest:red_pazyryk_rug", 29),
            Map.entry("conquest:shirishabad_rug", 30),
            Map.entry("conquest:william_morris_rug", 31),
            Map.entry("conquest:yellow_red_persian_rug", 32)
    );

    private LoomWeaves() {
    }

    /** The sprite {@code product} is woven with, or {@link #DEFAULT} if it has no weave of its own. */
    public static int spriteIndex(String product) {
        return BY_PRODUCT.getOrDefault(product, DEFAULT);
    }

    /**
     * Whether {@code product} has a weave, and so is something a loom can be seen working.
     *
     * <p>This is what drives {@code HAS_THREAD}: a loom holding a block with no weave of its own -
     * the wool going in, say - is left bare rather than drawn in the fallback white.</p>
     */
    public static boolean isKnown(String product) {
        return BY_PRODUCT.containsKey(product);
    }

    /** The product id for {@code stack}, or an empty string if there is nothing there. */
    public static String productOf(ItemStack stack) {
        return stack.isEmpty() ? "" : productOf(stack.getItem());
    }

    /** The product id for {@code item}. */
    public static String productOf(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    /** The item a stored product names, if it is one that is actually registered. */
    public static Optional<Item> itemOf(String product) {
        if (product.isEmpty()) {
            return Optional.empty();
        }
        Identifier id = Identifier.tryParse(product);
        return id == null ? Optional.empty() : BuiltInRegistries.ITEM.getOptional(id);
    }
}
