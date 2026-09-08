package com.conquestrefabricated.content.loom;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * The weaving half of the loom: its menu type and the {@code conquest:weaving} recipe type.
 *
 * <p>There is no block or item here - the looms are ordinary Conquest blocks, registered through the
 * block pipeline like any other. This only holds what a loom needs to be a crafting station, which
 * is why it is far shorter than {@code ArmsStation}.</p>
 *
 * <p>The menu type is built on demand rather than eagerly for the same reason the arms station's is:
 * NeoForge only opens one registry at a time, so each loader calls {@link #createMenu()} from its
 * menu registration phase (see {@code LoomStationInit} in the fabric and neoforge source sets).</p>
 */
public final class LoomStation {

    public static final String PATH = "weaving";
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, PATH);

    /** Menu id, kept apart from the recipe id so the two can be told apart in logs and F3 output. */
    public static final Identifier MENU_ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "loom");

    public static final RecipeType<WeavingRecipe> RECIPE_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return ID.toString();
        }
    };

    public static final RecipeSerializer<WeavingRecipe> RECIPE_SERIALIZER =
            new RecipeSerializer<>(WeavingRecipe.MAP_CODEC, WeavingRecipe.STREAM_CODEC);

    /** Set by {@link #createMenu()} during menu registration. */
    public static MenuType<LoomMenu> MENU;

    private LoomStation() {
    }

    /** Builds the menu type. Call only while the menu registry is open, then register the result. */
    public static MenuType<LoomMenu> createMenu() {
        MENU = new MenuType<>(LoomMenu::new, FeatureFlags.DEFAULT_FLAGS);
        return MENU;
    }
}
