package com.conquestrefabricated.client.gui.palette.fabric;

import com.conquestrefabricated.client.ModBinds;
import com.conquestrefabricated.client.gui.palette.Palette;
import com.conquestrefabricated.client.gui.palette.PaletteContainer;
import com.conquestrefabricated.client.gui.palette.PaletteScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class PaletteGuiEvents {

   // @SubscribeEvent
    public static void onKeyPress(Screen currentScreen, KeyEvent key) {
        if (currentScreen instanceof AbstractContainerScreen<?> screen) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
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
                // open previous screen or close if none
                if (PaletteScreen.closesGui(key)) {
                    screen.onClose();
                    return;
                }

                // open creative gui regardless if was there previously
                if (key.key() == Minecraft.getInstance().options.keyInventory.getDefaultKey().getValue()) {
                    Minecraft.getInstance().setScreen(new CreativeModeInventoryScreen(player, FeatureFlagSet.of(), true));
                    return;
                }
            }

            // ignore everything else
            if (key.key() != ModBinds.getPaletteBind().getDefaultKey().getValue()) {
                return;
            }

            Slot slot = screen.hoveredSlot;
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

/*
    public static void onRender() {
        if (event.getOverlay() == ForgeIngameGui.HOTBAR_ELEMENT) {
            if (Minecraft.getInstance().screen instanceof PaletteScreen) {
                event.setCanceled(true);
            }
        }
    }*/
}
