package com.conquestrefabricated.content.tools;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Conquest's crafting tool sets and the recipe type behind all of them.
 *
 * <p>A tool set is just an id plus an item that opens a picker for the recipes carrying that id, so
 * addons add one by calling {@link #register(CraftingTool)} and registering the item and menu type
 * their loader's registration phase hands them.</p>
 */
public final class CraftingTools {

    public static final Identifier RECIPE_ID =
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "tool_crafting");

    public static final RecipeType<ToolCraftingRecipe> RECIPE_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return RECIPE_ID.toString();
        }
    };

    public static final RecipeSerializer<ToolCraftingRecipe> RECIPE_SERIALIZER =
            new RecipeSerializer<>(ToolCraftingRecipe.MAP_CODEC, ToolCraftingRecipe.STREAM_CODEC);

    private static final Map<Identifier, CraftingTool> TOOLS = new LinkedHashMap<>();

    /** Saws, chisels and planes: planks, beams, panelling, anything worked from wood. */
    public static final CraftingTool WOODWORKING = register(CraftingTool.of(
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "woodworking_tools")));

    /** Hammers and chisels: ashlar, brick, tile, anything worked from stone. */
    public static final CraftingTool MASON = register(CraftingTool.of(
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "mason_tools")));

    /** Hammer, tongs: plate, bar, grillwork, anything worked from metal. */
    public static final CraftingTool METALWORKING = register(CraftingTool.of(
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "metalworking_tools")));

    private CraftingTools() {
    }

    /** Declares a tool set. Its item and menu type still have to be registered by each loader. */
    public static CraftingTool register(CraftingTool tool) {
        CraftingTool previous = TOOLS.putIfAbsent(tool.id(), tool);
        if (previous != null) {
            throw new IllegalStateException("Duplicate crafting tool set: " + tool.id());
        }
        return tool;
    }

    public static Optional<CraftingTool> get(Identifier id) {
        return Optional.ofNullable(TOOLS.get(id));
    }

    /** Every declared tool set, in declaration order. */
    public static Collection<CraftingTool> all() {
        return TOOLS.values();
    }
}
