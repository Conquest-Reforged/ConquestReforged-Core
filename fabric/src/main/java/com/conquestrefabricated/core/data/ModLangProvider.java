package com.conquestrefabricated.core.data;

import com.conquestrefabricated.core.asset.lang.Translations;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.HashMap;
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
                .filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("conquest"))
                .forEach(block -> {
                    String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
                    String translationKey = specialCaseTranslations.getOrDefault(
                            path,
                            Translations.translate(path)
                    );
                    if (block.asItem() != net.minecraft.world.item.Items.AIR) translationBuilder.add(block.asItem(), translationKey);
                });
    }
}