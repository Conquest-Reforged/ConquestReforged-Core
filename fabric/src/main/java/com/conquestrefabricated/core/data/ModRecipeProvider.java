package com.conquestrefabricated.core.data;

import com.conquestrefabricated.core.Namespaces;

import com.conquestrefabricated.content.arms.ArmsStationRecipeBuilder;
import com.conquestrefabricated.content.loom.WeavingRecipeBuilder;
import com.conquestrefabricated.content.painting.PaintingRecipeBuilder;
import com.conquestrefabricated.content.pottery.PotteryRecipeBuilder;
import com.conquestrefabricated.content.tools.ToolCraftingRecipeBuilder;
import com.conquestrefabricated.core.util.log.Log;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;

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
                    // Only the family's parent gets a recipe. The rest of the family is reached by the
                    // pickers' shape toggle, which reads the family itself rather than any recipe file,
                    // so there is nothing to write for slabs, stairs and the like.
                    if (blockData.isFamilyParent()) {
                        blockData.getProps().getToolRecipe().ifPresent(spec ->
                                ToolCraftingRecipeBuilder.from(spec, items, blockData.getBlock()).save(output));
                        blockData.getProps().getWeavingRecipe().ifPresent(spec ->
                                WeavingRecipeBuilder.from(spec, items, blockData.getBlock()).save(output));
                        blockData.getProps().getPaintingRecipe().ifPresent(spec ->
                                PaintingRecipeBuilder.from(spec, items, blockData.getBlock()).save(output));
                        blockData.getProps().getPotteryRecipe().ifPresent(spec ->
                                PotteryRecipeBuilder.from(spec, items, blockData.getBlock()).save(output));
                    }
                });
            }
        };
    }

    @Override
    public String getName() {
        return "";
    }
}
