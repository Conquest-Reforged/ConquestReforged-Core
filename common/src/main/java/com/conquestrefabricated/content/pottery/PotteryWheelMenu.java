package com.conquestrefabricated.content.pottery;

import com.conquestrefabricated.content.station.StationMenu;
import com.conquestrefabricated.content.station.WorkstationMenu;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeType;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

/**
 * The pottery wheel's picker. All of the behaviour is {@link WorkstationMenu}; this only says which
 * recipes it offers.
 */
public class PotteryWheelMenu extends WorkstationMenu<PotteryRecipe> {

    /** Client-side constructor: an empty stand-in the server then fills in over the wire. */
    public PotteryWheelMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null, clientData());
    }

    public PotteryWheelMenu(int containerId, Inventory inventory, @Nullable Container wheel, ContainerData data) {
        super(PotteryWheelStation.MENU, containerId, inventory, wheel, data);
    }

    @Override
    protected RecipeType<PotteryRecipe> recipeType() {
        return PotteryWheelStation.RECIPE_TYPE;
    }

    @Override
    protected Optional<TagKey<Item>> shapesTag() {
        return Optional.of(StationMenu.shapesTagFor(PotteryWheelStation.MENU_ID));
    }
}
