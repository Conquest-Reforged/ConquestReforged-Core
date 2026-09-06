package com.conquestrefabricated.core.data;

import com.conquestrefabricated.core.Namespaces;

import com.conquestrefabricated.core.asset.lang.Lore;
import com.conquestrefabricated.core.asset.lang.Translations;
import com.conquestrefabricated.core.block.data.BlockData;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import com.conquestrefabricated.core.util.log.Log;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModLangProvider extends FabricLanguageProvider {

    public ModLangProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider wrapperLookup, TranslationBuilder translationBuilder) {
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
                    if (block.asItem() != net.minecraft.world.item.Items.AIR) translationBuilder.add(block.asItem(), translationKey);
                });

        generateLore(translationBuilder);
    }

    /**
     * Writes the lore lines set with {@code Props.lore(..)}.
     * <p>
     * Lore keys belong to the family rather than to each shape in it, so every block a
     * {@code register(TypeList)} call produced maps to the same key. Emitting them once keeps the
     * lang file to one entry per family instead of one per variant.
     */
    private void generateLore(TranslationBuilder translationBuilder) {
        Map<String, String> written = new HashMap<>();

        Namespaces.stream()
                .flatMap(namespace -> BlockDataRegistry.getInstance().getData(namespace))
                .forEach(blockData -> addLore(blockData, translationBuilder, written));
    }

    private void addLore(BlockData blockData, TranslationBuilder translationBuilder, Map<String, String> written) {
        List<String> lines = blockData.getProps().getLore();
        if (lines.isEmpty()) {
            return;
        }

        List<String> keys = Lore.keys(blockData.getBlockName(), lines.size());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String line = lines.get(i);

            String existing = written.putIfAbsent(key, line);
            if (existing == null) {
                translationBuilder.add(key, line);
                continue;
            }
            // The family's other shapes carry the same key, which is the point - skip them
            // quietly. Two *different* families landing on one key means two families share a
            // name, and one lore would be lost silently, so say so.
            if (!existing.equals(line)) {
                Log.warn("Lore key {} is claimed by two families with different text; keeping \"{}\" and dropping \"{}\"",
                        key, existing, line);
            }
        }
    }
}
