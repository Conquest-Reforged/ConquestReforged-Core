package com.conquestrefabricated.core.data;

import com.conquestrefabricated.content.blocks.block.*;
import com.conquestrefabricated.content.blocks.block.directional.LayerDirectional;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                BlockDataRegistry.getInstance().getData("conquest").forEach(blockData -> {
                    if (blockData.getProps().hasParent()) {
                        Block rootBlock = blockData.getProps().getParent().getBlock();
                        Block productBlock = blockData.getBlock();

                        if (rootBlock != productBlock) {
                            if (productBlock instanceof VerticalSlab) {
                                offerSCRecipe(output, RecipeCategory.BUILDING_BLOCKS, productBlock, rootBlock, 4);
                            } else if (productBlock instanceof VerticalSlabLessLayers) {
                                offerSCRecipe(output, RecipeCategory.BUILDING_BLOCKS, productBlock, rootBlock, 3);
                            } else if (productBlock instanceof Layer || productBlock instanceof Slab || productBlock instanceof LayerDirectional) {
                                offerSCRecipe(output, RecipeCategory.BUILDING_BLOCKS, productBlock, rootBlock, 8);
                            } else if (productBlock instanceof VerticalCorner) {
                                offerSCRecipe(output, RecipeCategory.BUILDING_BLOCKS, productBlock, rootBlock, 4);
                            } else if (productBlock instanceof VerticalCornerLessLayers) {
                                offerSCRecipe(output, RecipeCategory.BUILDING_BLOCKS, productBlock, rootBlock, 3);
                            } else if (productBlock instanceof VerticalQuarter) {
                                offerSCRecipe(output, RecipeCategory.BUILDING_BLOCKS, productBlock, rootBlock, 4);
                            } else if (productBlock instanceof VerticalQuarterLessLayers) {
                                offerSCRecipe(output, RecipeCategory.BUILDING_BLOCKS, productBlock, rootBlock, 3);
                            } else if (productBlock instanceof Pillar) {
                                offerSCRecipe(output, RecipeCategory.BUILDING_BLOCKS, productBlock, rootBlock, 3);
                            } else if (productBlock instanceof SlabLessLayers) {
                                offerSCRecipe(output, RecipeCategory.BUILDING_BLOCKS, productBlock, rootBlock, 4);
                            } else if (productBlock instanceof SlabQuarter) {
                                offerSCRecipe(output, RecipeCategory.BUILDING_BLOCKS, productBlock, rootBlock, 3);
                            } else {
                                offerSCRecipe(output, RecipeCategory.BUILDING_BLOCKS, productBlock, rootBlock, 1);
                            }
                        }
                    }
                });
            }
        };
    }

    public void offerSCRecipe(RecipeOutput exporter, RecipeCategory category, ItemLike output, ItemLike input, int count) {
        ResourceKey<Recipe<?>> recipeId = ResourceKey.create(Registries.RECIPE,
                Identifier.fromNamespaceAndPath("conquest", RecipeProvider.getItemName(output) + "_sc"));

        NoAdvancementStonecuttingRecipe recipe = new NoAdvancementStonecuttingRecipe(
                Ingredient.of(input),
                new ItemStackTemplate(output.asItem(), count)
        );

        exporter.accept(recipeId, recipe, null);
    }

    @Override
    public String getName() {
        return "";
    }
}
