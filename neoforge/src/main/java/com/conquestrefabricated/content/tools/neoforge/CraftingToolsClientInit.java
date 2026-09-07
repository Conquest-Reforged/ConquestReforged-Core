package com.conquestrefabricated.content.tools.neoforge;

import com.conquestrefabricated.client.gui.station.StationScreen;
import com.conquestrefabricated.content.tools.CraftingTool;
import com.conquestrefabricated.content.tools.CraftingTools;
import com.conquestrefabricated.content.tools.ToolCraftingMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** NeoForge-side client registration for the crafting tool pickers. */
@EventBusSubscriber(modid = "conquest", value = Dist.CLIENT)
public class CraftingToolsClientInit {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        for (CraftingTool tool : CraftingTools.all()) {
            event.register(tool.menuType(), StationScreen<ToolCraftingMenu>::new);
        }
    }
}
