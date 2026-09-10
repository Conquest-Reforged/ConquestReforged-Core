package com.conquestrefabricated.core.data;

import com.conquestrefabricated.api.tags.ModTags;
import com.conquestrefabricated.core.Namespaces;
import com.conquestrefabricated.core.block.builder.RecipeIngredient;
import com.conquestrefabricated.core.block.data.BlockData;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import com.conquestrefabricated.core.util.log.Log;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagAppender;
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
        this.addLimeSources();

        Set<RecipeIngredient.BlockTagMirror> mirrors = mirrorsInUse();
        // Some tags are mirrored whether or not a recipe asks, because other tags are built on them.
        for (TagKey<Block> always : ModTags.MIRRORED_TO_ITEMS) {
            mirrors.add(new RecipeIngredient.BlockTagMirror(always, RecipeIngredient.itemTagFor(always), false));
            // Touch the builder so the file exists even where this module has no blocks in the tag -
            // the union below refers to it, and the modules that do have blocks merge into it.
            this.valueLookupBuilder(RecipeIngredient.itemTagFor(always));
        }

        if (mirrors.isEmpty()) {
            return;
        }

        int entries = 0;
        for (BlockData data : blockData().toList()) {
            Item item = data.getBlock().asItem();
            if (item == Items.AIR) {
                // A block with no item of its own can never be an ingredient.
                continue;
            }
            for (TagKey<Block> tag : data.getTags()) {
                for (RecipeIngredient.BlockTagMirror mirror : mirrors) {
                    if (!mirror.source().equals(tag)) {
                        continue;
                    }
                    if (mirror.basesOnly() && !data.isFamilyParent()) {
                        // The shapes cut from this block stay out, so a recipe asking for "any brick"
                        // is not also offered every slab and stair of one.
                        continue;
                    }
                    valueLookupBuilder(mirror.target()).add(item).setReplace(false);
                    entries++;
                }
            }
        }

        for (RecipeIngredient.BlockTagMirror mirror : mirrors) {
            Log.info("Crafting ingredients: {} -> {}{}",
                    mirror.source().location(), mirror.target().location(),
                    mirror.basesOnly() ? " (family parents only)" : "");
        }
        Log.info("Crafting ingredients: mirrored {} block tag(s) into item tags, {} entries",
                mirrors.size(), entries);
    }

    /**
     * The union every quicklime recipe smelts: the calcareous stones, plus vanilla calcite.
     *
     * <p>Written as tag references rather than as a flattened list of blocks, so a module adding its
     * own chalk joins it by tagging the block and re-running its own data generation.</p>
     */
    private void addLimeSources() {
        TagAppender<Item, Item> limeSources = this.valueLookupBuilder(ModTags.LIME_SOURCES);
        for (TagKey<Block> stone : ModTags.MIRRORED_TO_ITEMS) {
            limeSources.addOptionalTag(RecipeIngredient.itemTagFor(stone));
        }
        limeSources.add(Items.CALCITE).setReplace(false);
    }

    /** Every block tag some block declares itself as being crafted or woven from, and how. */
    private static Set<RecipeIngredient.BlockTagMirror> mirrorsInUse() {
        Set<RecipeIngredient.BlockTagMirror> mirrors = new LinkedHashSet<>();
        blockData().forEach(data -> {
            data.getProps().getToolRecipe()
                    .ifPresent(spec -> spec.ingredient().mirror().ifPresent(mirrors::add));
            data.getProps().getWeavingRecipe()
                    .ifPresent(spec -> spec.ingredient().mirror().ifPresent(mirrors::add));
            data.getProps().getPotteryRecipe()
                    .ifPresent(spec -> spec.ingredient().mirror().ifPresent(mirrors::add));
            data.getProps().getPaintingRecipe().ifPresent(spec -> {
                spec.base().mirror().ifPresent(mirrors::add);
                spec.paint().mirror().ifPresent(mirrors::add);
            });
        });
        return mirrors;
    }

    private static Stream<BlockData> blockData() {
        return Namespaces.stream().flatMap(namespace -> BlockDataRegistry.getInstance().getData(namespace));
    }
}
