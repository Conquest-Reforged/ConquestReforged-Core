package com.conquestrefabricated.core.data;

import com.conquestrefabricated.core.Namespaces;

import com.conquestrefabricated.core.asset.lang.Lore;
import com.conquestrefabricated.core.asset.lang.Translations;
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
     * Lore is stored per family rather than per block, so this walks the lore registry instead of
     * the block registry. That is what keeps one entry per family even when a family is split
     * across several {@code register(TypeList)} calls that share a {@code family(..)} target.
     */
    private void generateLore(TranslationBuilder translationBuilder) {
        Lore.entries().forEach((familyId, lines) -> {
            if (!Namespaces.isRegistered(familyId.getNamespace())) {
                return;
            }
            List<String> keys = Lore.keys(familyId, lines.size());
            for (int i = 0; i < keys.size(); i++) {
                translationBuilder.add(keys.get(i), lines.get(i));
            }
        });
    }
}
