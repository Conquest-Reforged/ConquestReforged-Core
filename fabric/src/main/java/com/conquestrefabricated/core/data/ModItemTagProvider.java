package com.conquestrefabricated.core.data;

import com.conquestrefabricated.core.Namespaces;
import com.conquestrefabricated.core.block.builder.RecipeIngredient;
import com.conquestrefabricated.core.block.data.BlockData;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import com.conquestrefabricated.core.util.log.Log;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Mirrors, into item tags, the block tags that blocks are crafted from.
 *
 * <p>A recipe can only ever match on an item tag, so declaring
 * {@code craftedWith(MASON.id(), ModTags.STONE)} writes {@code #conquest:stone} into the recipe - and
 * Conquest declares that as a <i>block</i> tag. Without the item tag beside it the recipe would load
 * happily and then match nothing, which is the sort of failure that takes an afternoon to find.</p>
 *
 * <p>Only tags actually used as an ingredient are mirrored, so this stays empty until someone crafts
 * from one, and picks up new ones on its own. Vanilla block tags mostly have an item counterpart
 * already; where one does not, this fills it in the same way.</p>
 */
public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {

    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        Set<TagKey<Block>> crafted = craftedFromTags();
        if (crafted.isEmpty()) {
            return;
        }

        int mirrored = 0;
        for (BlockData data : blockData().toList()) {
            Item item = data.getBlock().asItem();
            if (item == Items.AIR) {
                // A block with no item of its own can never be an ingredient.
                continue;
            }
            for (TagKey<Block> tag : data.getTags()) {
                if (crafted.contains(tag)) {
                    valueLookupBuilder(RecipeIngredient.itemTagFor(tag)).add(item).setReplace(false);
                    mirrored++;
                }
            }
        }

        Log.info("Crafting ingredients: mirrored {} block tag(s) into item tags, {} entries",
                crafted.size(), mirrored);
    }

    /** Every block tag some block declares itself as being crafted or woven from. */
    private static Set<TagKey<Block>> craftedFromTags() {
        Set<TagKey<Block>> tags = new LinkedHashSet<>();
        blockData().forEach(data -> {
            data.getProps().getToolRecipe()
                    .ifPresent(spec -> spec.ingredient().blockTag().ifPresent(tags::add));
            data.getProps().getWeavingRecipe()
                    .ifPresent(spec -> spec.ingredient().blockTag().ifPresent(tags::add));
        });
        return tags;
    }

    private static Stream<BlockData> blockData() {
        return Namespaces.stream().flatMap(namespace -> BlockDataRegistry.getInstance().getData(namespace));
    }
}
