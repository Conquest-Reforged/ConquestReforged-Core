package com.conquestrefabricated.content.arms;

import com.conquestrefabricated.content.station.PreviewStationMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * The arms station container. All of the behaviour lives in {@link PreviewStationMenu}; this only says
 * which recipes it offers and that the player has to stay near the block.
 */
public class ArmsStationMenu extends PreviewStationMenu<ArmsStationRecipe> {

    public ArmsStationMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public ArmsStationMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ArmsStation.MENU, containerId, inventory, access);
    }

    @Override
    protected RecipeType<ArmsStationRecipe> recipeType() {
        return ArmsStation.RECIPE_TYPE;
    }

    @Override
    public MenuType<?> getType() {
        return ArmsStation.MENU;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ArmsStation.BLOCK);
    }
}
