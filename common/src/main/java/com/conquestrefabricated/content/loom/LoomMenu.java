package com.conquestrefabricated.content.loom;

import com.conquestrefabricated.content.station.WorkstationMenu;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

/**
 * The loom's picker. All of the behaviour is {@link WorkstationMenu}; this only says which recipes it
 * offers and what it sounds like.
 */
public class LoomMenu extends WorkstationMenu<WeavingRecipe> {

    /** Client-side constructor: an empty stand-in the server then fills in over the wire. */
    public LoomMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null, clientData());
    }

    public LoomMenu(int containerId, Inventory inventory, @Nullable Container loom, ContainerData data) {
        super(LoomStation.MENU, containerId, inventory, loom, data);
    }

    @Override
    protected RecipeType<WeavingRecipe> recipeType() {
        return LoomStation.RECIPE_TYPE;
    }

    @Override
    protected SoundEvent takeResultSound() {
        return SoundEvents.UI_LOOM_TAKE_RESULT;
    }
}
