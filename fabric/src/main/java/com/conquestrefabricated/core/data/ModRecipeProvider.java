package com.conquestrefabricated.core.data;

import com.conquestrefabricated.core.Namespaces;

import com.conquestrefabricated.content.blocks.block.*;
import com.conquestrefabricated.content.blocks.block.directional.LayerDirectional;
import com.conquestrefabricated.content.arms.ArmsStationRecipeBuilder;
import com.conquestrefabricated.content.loom.WeavingRecipeBuilder;
import com.conquestrefabricated.content.tools.ToolCraftingRecipeBuilder;
import com.conquestrefabricated.core.block.data.BlockData;
import com.conquestrefabricated.core.util.log.Log;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
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
                HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);

                // Any vanilla helmet reforges into any of ours, any sword into any of our swords, and
                // so on. Only covers gear on the classpath of whichever module runs datagen.
                ArmsStationRecipeBuilder.Generated gear = ArmsStationRecipeBuilder.allEquipment(output, items);
                Log.info("Arms station: wrote {} recipes, passed over {} items with no recorded armour slot or weapon kind",
                        gear.recipes(), gear.skipped());

                Namespaces.stream().flatMap(namespace -> BlockDataRegistry.getInstance().getData(namespace)).forEach(blockData -> {
                    // Only the family's parent is made at a set of crafting tools or on a loom; every
                    // other member is cut from the parent by the stonecutting recipes below, which is
                    // also what the pickers' family toggle walks.
                    if (isFamilyParent(blockData)) {
                        blockData.getProps().getToolRecipe().ifPresent(spec ->
                                ToolCraftingRecipeBuilder.from(spec, items, blockData.getBlock()).save(output));
                        blockData.getProps().getWeavingRecipe().ifPresent(spec ->
                                WeavingRecipeBuilder.from(spec, items, blockData.getBlock()).save(output));
                    }

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

    /**
     * Whether {@code data} is the block a family is built from. A builder that never had a parent set
     * registered one block and that block is its own root; otherwise the parent is whatever
     * {@code Props.parent(..)} points at, which for cutout families lands on a copied {@code Props}
     * whose parent was never filled in.
     */
    private static boolean isFamilyParent(BlockData data) {
        return !data.getProps().hasParent() || data.getProps().getParent().getBlock() == data.getBlock();
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
