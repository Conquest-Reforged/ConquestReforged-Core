package com.conquestrefabricated.content.pottery;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * The working half of the pottery wheel: its menu type and the {@code conquest:pottery} recipe type.
 *
 * <p>There is no block or item here - the wheel is an ordinary Conquest block, registered by whichever
 * module declares it, in the same way the loom is. This only holds what it needs to be a
 * workstation.</p>
 */
public final class PotteryWheelStation {

    public static final String PATH = "pottery";
    /** Recipe type id, named for the work rather than for the block that does it. */
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, PATH);

    /** Menu id, kept apart from the recipe id so the two can be told apart in logs. */
    public static final Identifier MENU_ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "pottery_wheel");

    /** Lang key prefix for the picker's own controls. */
    public static final String LANG_PREFIX = "container." + Namespaces.DEFAULT + ".pottery_wheel";

    public static final RecipeType<PotteryRecipe> RECIPE_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return ID.toString();
        }
    };

    public static final RecipeSerializer<PotteryRecipe> RECIPE_SERIALIZER =
            new RecipeSerializer<>(PotteryRecipe.MAP_CODEC, PotteryRecipe.STREAM_CODEC);

    /** Set by {@link #createMenu()} during menu registration. */
    public static MenuType<PotteryWheelMenu> MENU;

    private PotteryWheelStation() {
    }

    /** Builds the menu type. Call only while the menu registry is open, then register the result. */
    public static MenuType<PotteryWheelMenu> createMenu() {
        MENU = new MenuType<>(PotteryWheelMenu::new, FeatureFlags.DEFAULT_FLAGS);
        return MENU;
    }
}
