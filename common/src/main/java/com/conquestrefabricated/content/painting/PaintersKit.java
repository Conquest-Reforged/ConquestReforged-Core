package com.conquestrefabricated.content.painting;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Objects;

/**
 * The painter's kit: the item, its menu type, and the {@code conquest:painting} recipe type.
 *
 * <p>It works like a set of {@code CraftingTools} - held, right-clicked, a stonecutter-style picker -
 * with one difference that runs all the way down: it takes a base material <i>and</i> something to
 * paint with, so it cannot share the tools' single-ingredient recipe type.</p>
 *
 * <p>The item and menu type are built on demand rather than eagerly, because {@code Item}
 * constructors claim an intrusive holder from the item registry and NeoForge only opens one registry
 * at a time (see {@code PaintersKitInit} in the fabric and neoforge source sets).</p>
 */
public final class PaintersKit {

    public static final String PATH = "painters_kit";

    /** Item and menu id. */
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, PATH);
    /** Recipe type id, named for what it does rather than for the tool that does it. */
    public static final Identifier RECIPE_ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "painting");

    public static final ResourceKey<Item> ITEM_KEY = ResourceKey.create(Registries.ITEM, ID);

    public static final String CONTAINER_TITLE_KEY = "container." + Namespaces.DEFAULT + "." + PATH;
    public static final String TOOLTIP_KEY = "tooltip." + Namespaces.DEFAULT + ".item." + PATH;

    public static final RecipeType<PaintingRecipe> RECIPE_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return RECIPE_ID.toString();
        }
    };

    public static final RecipeSerializer<PaintingRecipe> RECIPE_SERIALIZER =
            new RecipeSerializer<>(PaintingRecipe.MAP_CODEC, PaintingRecipe.STREAM_CODEC);

    /** Set by {@link #createItem()} during item registration. */
    public static Item ITEM;
    /** Set by {@link #createMenu()} during menu registration. */
    public static MenuType<PaintersKitMenu> MENU;

    private PaintersKit() {
    }

    public static Component title() {
        return Component.translatable(CONTAINER_TITLE_KEY);
    }

    /** Builds the item. Call only while the item registry is open, then register the result. */
    public static Item createItem() {
        ITEM = new PaintersKitItem(new Item.Properties().stacksTo(1).setId(ITEM_KEY));
        return ITEM;
    }

    /** Builds the menu type. */
    public static MenuType<PaintersKitMenu> createMenu() {
        MENU = new MenuType<>(PaintersKitMenu::new, FeatureFlags.DEFAULT_FLAGS);
        return MENU;
    }

    public static Item item() {
        return Objects.requireNonNull(ITEM, "Painter's kit item has not been created yet");
    }
}
