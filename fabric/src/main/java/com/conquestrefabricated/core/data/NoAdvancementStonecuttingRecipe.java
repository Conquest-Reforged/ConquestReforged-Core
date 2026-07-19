package com.conquestrefabricated.core.data;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class NoAdvancementStonecuttingRecipe extends SingleItemRecipe {
    public NoAdvancementStonecuttingRecipe(Ingredient ingredient, ItemStackTemplate result) {
        super(new Recipe.CommonInfo(false), ingredient, result);
    }

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public RecipeSerializer<? extends SingleItemRecipe> getSerializer() {
        return StonecutterRecipe.SERIALIZER;
    }

    @Override
    public RecipeType<? extends SingleItemRecipe> getType() {
        return RecipeType.STONECUTTING;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.STONECUTTER;
    }
}
