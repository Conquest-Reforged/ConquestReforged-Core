package com.conquestrefabricated.content.tools.neoforge;

import com.conquestrefabricated.content.tools.CraftingTool;
import com.conquestrefabricated.content.tools.CraftingTools;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

/** NeoForge-side registration for the crafting tool items, their menus and the shared recipe type. */
@EventBusSubscriber(modid = "conquest")
public class CraftingToolsInit {

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.ITEM, helper -> {
            for (CraftingTool tool : CraftingTools.all()) {
                helper.register(tool.itemId(), tool.createItem());
            }
        });
        event.register(Registries.MENU, helper -> {
            for (CraftingTool tool : CraftingTools.all()) {
                helper.register(tool.itemId(), tool.createMenu());
            }
        });
        event.register(Registries.RECIPE_TYPE, helper ->
                helper.register(CraftingTools.RECIPE_ID, CraftingTools.RECIPE_TYPE));
        event.register(Registries.RECIPE_SERIALIZER, helper ->
                helper.register(CraftingTools.RECIPE_ID, CraftingTools.RECIPE_SERIALIZER));
    }
}
