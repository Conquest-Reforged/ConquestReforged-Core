package com.conquestrefabricated.content.station;

import com.conquestrefabricated.content.arms.ArmsStation;
import com.conquestrefabricated.content.loom.LoomStation;
import com.conquestrefabricated.content.painting.PaintersKit;
import com.conquestrefabricated.content.pottery.PotteryWheelStation;
import com.conquestrefabricated.content.tools.CraftingTool;
import com.conquestrefabricated.content.tools.CraftingTools;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

/**
 * The catalogue of places you can craft at, as one list.
 *
 * <p>Each station is otherwise only known to its own package, which is fine until something has to
 * iterate them: a recipe viewer needs a category per station, and a recipe needs to name the one it
 * belongs to. Keeping both off this list means a station cannot appear in a viewer under one id and
 * tag its recipes with another.</p>
 *
 * <p>Tool sets are read from {@link CraftingTools} rather than listed, so an addon registering its
 * own set gets a category without touching this.</p>
 */
public final class Stations {

    /**
     * One station: the id its recipes carry, and what to draw for it.
     *
     * @param id     also the registry id its item or block is registered under, where it has one
     * @param icon   drawn when nothing is registered under {@link #id} - a handheld kit is its own
     *               item, but a loom is a vanilla block and a pottery wheel belongs to another module
     * @param shapes whether its picker has the family-shape toggle at all
     * @param shapesOwnOutput whether the shapes it offers are of what it makes rather than of what it
     *               takes - true only for a painter's kit, which shapes what it painted and nothing
     *               else, so that painting cobblestone does not turn the kit into a stonecutter
     */
    public record Station(Identifier id, ItemLike icon, boolean shapes, boolean shapesOwnOutput) {

        /** What to draw for this station: its own item where there is one, else the fallback. */
        public Item displayItem() {
            return BuiltInRegistries.ITEM.getOptional(this.id).orElseGet(this.icon::asItem);
        }

        public ItemStack displayStack() {
            return new ItemStack(this.displayItem());
        }
    }

    /**
     * The recipe book category every station recipe is filed under.
     *
     * <p>Ours rather than a vanilla one on purpose. A recipe has to be in the player's book for the
     * server to send its display - that is the only road to the client - but these are not meant to
     * be browsed there. No vanilla recipe book screen draws an unknown category, so filing them here
     * keeps them out of sight while still letting the display through.</p>
     */
    public static final RecipeBookCategory RECIPE_BOOK_CATEGORY = new RecipeBookCategory();

    /** Registry id of {@link #RECIPE_BOOK_CATEGORY}. */
    public static final Identifier RECIPE_BOOK_CATEGORY_ID =
            Identifier.fromNamespaceAndPath(com.conquestrefabricated.core.Namespaces.DEFAULT, "station");

    public static final Station LOOM = new Station(LoomStation.MENU_ID, Items.LOOM, true, false);
    public static final Station POTTERY_WHEEL =
            new Station(PotteryWheelStation.MENU_ID, Items.CLAY, true, false);
    public static final Station PAINTERS_KIT = new Station(PaintersKit.ID, Items.BRUSH, true, true);
    /** No shapes: the arms station turns iron into a breastplate, and iron has no family. */
    public static final Station ARMS = new Station(ArmsStation.ID, Items.ANVIL, false, false);

    private Stations() {
    }

    /** A tool set as a station. Its item is registered under its own id, so that is the icon. */
    public static Station of(CraftingTool tool) {
        return new Station(tool.id(), Items.STONECUTTER, true, false);
    }

    /** Every station, tool sets first. Order is what a recipe viewer lists its categories in. */
    public static List<Station> all() {
        List<Station> stations = new ArrayList<>();
        for (CraftingTool tool : CraftingTools.all()) {
            stations.add(of(tool));
        }
        stations.add(LOOM);
        stations.add(POTTERY_WHEEL);
        stations.add(PAINTERS_KIT);
        stations.add(ARMS);
        return List.copyOf(stations);
    }
}
