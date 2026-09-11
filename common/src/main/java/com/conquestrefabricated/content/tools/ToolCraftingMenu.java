package com.conquestrefabricated.content.tools;

import com.conquestrefabricated.content.station.PreviewStationMenu;
import com.conquestrefabricated.content.station.StationRecipes;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;
import java.util.List;

/**
 * The picker a set of crafting tools opens. Held rather than placed, so it stays open only while the
 * player is still carrying the tools that opened it.
 */
public class ToolCraftingMenu extends PreviewStationMenu<ToolCraftingRecipe> {

    private final CraftingTool tool;

    public ToolCraftingMenu(CraftingTool tool, int containerId, Inventory inventory) {
        super(tool.menuType(), containerId, inventory, ContainerLevelAccess.NULL);
        this.tool = tool;
    }

    public CraftingTool tool() {
        return this.tool;
    }

    @Override
    protected RecipeType<ToolCraftingRecipe> recipeType() {
        return CraftingTools.RECIPE_TYPE;
    }

    @Override
    public boolean supportsVariants() {
        return true;
    }

    @Override
    protected boolean accepts(ToolCraftingRecipe recipe) {
        return recipe.tool().equals(this.tool.id());
    }

    /**
     * A set of tools shapes anything it can transform, plus anything it made in the first place -
     * otherwise you could turn granite into ashlar and then have no way to cut the ashlar into a
     * slab, because nothing takes ashlar as an ingredient.
     */
    @Override
    protected boolean worksWith(ItemStack input) {
        return super.worksWith(input)
                || StationRecipes.produces(this.level, this.recipeType(), this::accepts, input,
                        this.emptyRecipeInput());
    }

    /** Extra materials this set may shape, on top of what its recipes touch. */
    @Override
    protected Optional<TagKey<Item>> shapesTag() {
        return Optional.of(this.tool.shapesTag());
    }

    @Override
    public MenuType<?> getType() {
        return this.tool.menuType();
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getMainHandItem().is(this.tool.item())
                || player.getOffhandItem().is(this.tool.item());
    }
}
