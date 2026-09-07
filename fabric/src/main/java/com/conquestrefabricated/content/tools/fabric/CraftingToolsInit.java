package com.conquestrefabricated.content.tools.fabric;

import com.conquestrefabricated.client.gui.station.StationScreen;
import com.conquestrefabricated.content.tools.CraftingTool;
import com.conquestrefabricated.content.tools.CraftingTools;
import com.conquestrefabricated.content.tools.ToolCraftingMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/** Fabric-side registration for the crafting tool items, their menus and the shared recipe type. */
public final class CraftingToolsInit {

    private CraftingToolsInit() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, CraftingTools.RECIPE_ID, CraftingTools.RECIPE_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CraftingTools.RECIPE_ID, CraftingTools.RECIPE_SERIALIZER);

        for (CraftingTool tool : CraftingTools.all()) {
            Registry.register(BuiltInRegistries.ITEM, tool.itemKey(), tool.createItem());
            Registry.register(BuiltInRegistries.MENU, tool.itemId(), tool.createMenu());
        }
    }

    public static void registerClient() {
        for (CraftingTool tool : CraftingTools.all()) {
            MenuScreens.register(tool.menuType(), StationScreen<ToolCraftingMenu>::new);
        }
    }
}
