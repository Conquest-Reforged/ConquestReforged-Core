package com.conquestrefabricated.client.gui.palette.neoforge;

import com.conquestrefabricated.client.ModBinds;
import com.conquestrefabricated.client.gui.palette.Palette;
import com.conquestrefabricated.client.gui.palette.PaletteContainer;
import com.conquestrefabricated.client.gui.palette.PaletteScreen;
import com.conquestrefabricated.core.util.log.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT)
public class PaletteGuiEvents {

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        Log.info("key pressed: " + event.getKey());
        Log.info("expected key: " + ModBinds.getPaletteBind().getDefaultKey().getValue());

        if (event.getAction() == GLFW.GLFW_PRESS || event.getAction() == GLFW.GLFW_REPEAT) {
            if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null || !player.getAbilities().instabuild) {
                    return;
                }

                if (screen instanceof CreativeModeInventoryScreen creativeScreen) {
                    // ignore search tab in creative inventory
                    int tabIndex = BuiltInRegistries.CREATIVE_MODE_TAB.getId(creativeScreen.selectedTab);
                    if (tabIndex == BuiltInRegistries.CREATIVE_MODE_TAB.getId(CreativeModeTabs.searchTab())) {
                        return;
                    }
                }

                if (screen instanceof PaletteScreen) {
                    if (PaletteScreen.closesGui(event.getKey())) {
                        screen.onClose();
                        return;
                    }

                    if (event.getKey() == Minecraft.getInstance().options.keyInventory.getDefaultKey().getValue()) {
                        //event.setCanceled(true);
                        Minecraft.getInstance().setScreen(new CreativeModeInventoryScreen(player, FeatureFlagSet.of(), true));
                        return;
                    }
                }

                if (event.getKey() != ModBinds.getPaletteBind().getDefaultKey().getValue()) {
                    return;
                }

                Slot slot = screen.getHoveredSlot();
                if (slot == null || !slot.hasItem()) {
                    return;
                }

                ItemStack stack = slot.getItem();
                Optional<Container> palette = Palette.getPalette(stack);
                if (!palette.isPresent()) {
                    return;
                }

                PaletteContainer container = new PaletteContainer(player.getInventory(), palette.get());
                PaletteScreen paletteScreen = new PaletteScreen(screen, player, player.getInventory(), container);
                Minecraft.getInstance().setScreen(paletteScreen);
            }
        }
    }

    @SubscribeEvent
    public static void onRender(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.HOTBAR)) {
            if (Minecraft.getInstance().screen instanceof PaletteScreen) {
                event.setCanceled(true);
            }
        }
    }
}