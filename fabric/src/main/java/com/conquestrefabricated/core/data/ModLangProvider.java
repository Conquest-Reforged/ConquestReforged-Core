package com.conquestrefabricated.core.data;

import com.conquestrefabricated.core.Namespaces;

import com.conquestrefabricated.core.asset.lang.Lore;
import com.conquestrefabricated.core.asset.lang.Translations;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLangProvider extends FabricLanguageProvider {

    public ModLangProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider wrapperLookup, TranslationBuilder translationBuilder) {
        // Every key written this run. See writeOnce(..) for why one is needed.
        Set<String> written = new HashSet<>();

        /* === Blocks === */
        Map<String, String> specialCaseTranslations = new HashMap<>();
        BuiltInRegistries.BLOCK.stream()
                .filter(block -> Namespaces.isRegistered(BuiltInRegistries.BLOCK.getKey(block).getNamespace()))
                .forEach(block -> {
                    String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String translationKey = specialCaseTranslations.getOrDefault(
                            path,
                            Translations.translate(path)
                    );
                    writeOnce(translationBuilder, written, block.asItem(), translationKey);
                });

        /* === Items === */
        // Covers the items that are not block items - weapons, armour, the crafting tools. The
        // block items in here were already written by the walk above, and writeOnce skips them.
        BuiltInRegistries.ITEM.stream()
                .filter(item -> Namespaces.isRegistered(BuiltInRegistries.ITEM.getKey(item).getNamespace()))
                .forEach(item -> {
                    String path = BuiltInRegistries.ITEM.getKey(item).getPath();
                    String translationKey = specialCaseTranslations.getOrDefault(
                            path,
                            Translations.translate(path)
                    );
                    writeOnce(translationBuilder, written, item, translationKey);
                });

        generateLore(translationBuilder, written);
    }

    /**
     * Writes one item's name, unless its key has already been written.
     * <p>
     * A block's item sits in the item registry as well, so walking blocks and then items reaches
     * every {@link net.minecraft.world.item.BlockItem} twice - and both visits produce the same
     * key, because a block item takes its description id from its block rather than from its own
     * registry name. Fabric's {@code TranslationBuilder} treats a repeated key as a mistake and
     * throws, which failed data generation outright on the first block item it reached. Keeping
     * the first write per key lets the block walk name the blocks and leaves the item walk to the
     * plain items it was added for.
     * <p>
     * The block walk runs first, so a block item keeps the name derived from the <i>block's</i>
     * registry path, which is what it was named by before the item walk existed.
     */
    private static void writeOnce(TranslationBuilder translationBuilder, Set<String> written,
                                  Item item, String translation) {
        if (item == Items.AIR) {
            // A block with no item of its own has nothing to name.
            return;
        }
        if (written.add(item.getDescriptionId())) {
            translationBuilder.add(item, translation);
        }
    }

    /**
     * Writes the lore lines set with {@code Props.lore(..)}.
     * <p>
     * Lore is stored per family rather than per block, so this walks the lore registry instead of
     * the block registry. That is what keeps one entry per family even when a family is split
     * across several {@code register(TypeList)} calls that share a {@code family(..)} target.
     */
    private void generateLore(TranslationBuilder translationBuilder, Set<String> written) {
        Lore.entries().forEach((familyId, lines) -> {
            if (!Namespaces.isRegistered(familyId.getNamespace())) {
                return;
            }
            List<String> keys = Lore.keys(familyId, lines.size());
            for (int i = 0; i < keys.size(); i++) {
                if (written.add(keys.get(i))) {
                    translationBuilder.add(keys.get(i), lines.get(i));
                }
            }
        });
    }
}
