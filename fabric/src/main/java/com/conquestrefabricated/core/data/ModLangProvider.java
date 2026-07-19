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
        specialCaseTranslations.put("sickle_on_the_ground", "Iron Sickle");
        specialCaseTranslations.put("invisible_light", "Invisible Light High");
        specialCaseTranslations.put("metal_stairs", "Metal Step Stairs");
        specialCaseTranslations.put("horizontal_birch_wood_railing", "Horizontal Birch Log Railing");
        specialCaseTranslations.put("horizontal_birch_wood_railing_corner", "Horizontal Birch Log Railing Corner");
        specialCaseTranslations.put("diagonal_birch_wood_railing", "Diagonal Birch Log Railing");
        specialCaseTranslations.put("horizontal_spruce_wood_railing", "Horizontal Tarred Spruce Wood Railing");
        specialCaseTranslations.put("horizontal_spruce_wood_railing_corner", "Horizontal Tarred Spruce Wood Railing Corner");
        specialCaseTranslations.put("diagonal_spruce_wood_railing", "Diagonal Tarred Spruce Wood Railing");
        specialCaseTranslations.put("horizontal_asian_acacia_wood_railing", "Diagonal Red Acacia Wood Railing");
        specialCaseTranslations.put("horizontal_asian_acacia_wood_railing_corner", "Diagonal Red Acacia Wood Railing");
        specialCaseTranslations.put("diagonal_asian_acacia_wood_railing", "Diagonal Red Acacia Wood Railing");
        specialCaseTranslations.put("asian_acacia_wood_pillar", "Red Acacia Wood Pillar");
        specialCaseTranslations.put("asian_acacia_wood_wall", "Red Acacia Wood Wall");
        specialCaseTranslations.put("asian_acacia_wood_fence", "Red Acacia Wood Fence");
        specialCaseTranslations.put("asian_acacia_wood_fence_gate", "Red Acacia Wood Fence Gate");
        specialCaseTranslations.put("tea_kettle", "Iron Kettle");

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

        translationBuilder.add("effect.conquest.custom_slowness", "Foliage Slowness");
        /* === Key-binds === */
        translationBuilder.add("key.palette.title", "Palette GUI");
        translationBuilder.add("key.search.title", "Search");
        translationBuilder.add("key.category.conquest", "Conquest Reforged");
        translationBuilder.add("conquest.dependency.close", "Continue");
        translationBuilder.add("conquest.intro.close", "Continue");
        translationBuilder.add("conquest.dependency.checkbox", "Do not show again");
        translationBuilder.add("conquest.intro.checkbox", "Do not show again");
        translationBuilder.add("conquest.dependency.missing", "Missing Dependencies:");
        translationBuilder.add("tooltip.conquest.block.toggle_2", "§62 Toggleable Variants (Right-Click)");
        translationBuilder.add("tooltip.conquest.block.toggle_3", "§63 Toggleable Variants (Right-Click)");
        translationBuilder.add("tooltip.conquest.block.toggle_4", "§64 Toggleable Variants (Right-Click)");
        translationBuilder.add("tooltip.conquest.block.toggle_5", "§65 Toggleable Variants (Right-Click)");
        translationBuilder.add("tooltip.conquest.block.toggle_6", "§66 Toggleable Variants (Right-Click)");
        translationBuilder.add("tooltip.conquest.block.toggle_7", "§67 Toggleable Variants (Right-Click)");
        translationBuilder.add("tooltip.conquest.block.toggle_8", "§68 Toggleable Variants (Right-Click)");
        translationBuilder.add("tooltip.conquest.block.toggle_10", "§610 Toggleable Variants (Right-Click)");
        translationBuilder.add("tooltip.conquest.block.loom_toggle_4", "§6Toggles: Mallet - Size, Rugs & Canvas - Weave, Hand - Position");
        translationBuilder.add("tooltip.conquest.block.board_toggle", "§6Toggles: Length (Right-Click), Width (Shift+Right-Click)");

        //Intro screens
        translationBuilder.add("conquest.dependency.modpack", "Modpack");
        translationBuilder.add("conquest.dependency.tooltip.modpack", "Page for the Modpack is here, install via ATLauncher or Modrinth launcher!");
        translationBuilder.add("conquest.dependency.tooltip.polytone", "Fixes Creative Inventory tab organization, improves fluid and fog colors when used with the Conquest Reforged resource-pack");
        translationBuilder.add("conquest.dependency.tooltip.continuity", "For connected textures! Without this your game will probably look funny");
        translationBuilder.add("conquest.dependency.tooltip.rp_crrp", "Fixes Creative inventory tab organization and improves biome colors when used with Polytone. Make sure to enable the resource-pack to get rid of this warning!");
        translationBuilder.add("conquest.dependency.tooltip.ardagrass", "Allows the side textures of grass to be replaced with the top texture for better landscapes");
        translationBuilder.add("conquest.dependency.tooltip.nuit", "Allows for a custom skybox provided by Conquest Reforged");
        translationBuilder.add("conquest.dependency.tooltip.forgeskyboxes", "Allows for a custom skybox provided by Conquest Reforged");
        translationBuilder.add("conquest.dependency.tooltip.entity_texture_features", "Allows for custom and random mob textures");
        translationBuilder.add("conquest.dependency.tooltip.entity_model_features", "Allows for custom mob models");
        translationBuilder.add("conquest.intro.1", "This screen will introduce you to keybinds for making building faster.");
        translationBuilder.add("conquest.intro.2", " - (Creative Mode only) shows texture shape variants in the block palette.");
        translationBuilder.add("conquest.intro.3", "Works while hovering over a block in the creative menu or when selected in the hotbar.");
        translationBuilder.add("conquest.intro.4", " - (Creative Mode only) press while looking at a block.");
        translationBuilder.add("conquest.intro.5", "This gives the exact shape you're looking at as a block item in your hotbar. Holding ALT as well will give you the exact direction of the block too");
        translationBuilder.add("conquest.intro.pickblock", "CTRL+MIDDLE-MOUSE-BUTTON");
        translationBuilder.add("conquest.intro.welcome", "Welcome to Conquest Reforged!");
        translationBuilder.add("conquest.dependency.1", "It appears you're not using the Conquest Reforged Modpack!");
        translationBuilder.add("conquest.dependency.2", "Our modpack adds all of the required dependencies,");
        translationBuilder.add("conquest.dependency.3", "along with optimization mods and proper configs for the best experience.");
        translationBuilder.add("conquest.dependency.4", "Getting all of the right versions of every mod is hard, this takes care of that for you.");
        translationBuilder.add("conquest.dependency.5", "If you're making your own modpack, you can use ours as a base.");
        translationBuilder.add("conquest.dependency.6", "Otherwise, this screen will show you which of the most essential dependencies are missing.");

        /* === Creative Tabs === */
        translationBuilder.add("itemGroup.conquest.f_metal", "Metal");
        translationBuilder.add("itemGroup.conquest.pp_weapons_and_tools", "Weapons & Tools");
        translationBuilder.add("itemGroup.conquest.ff_windows_and_glass", "Windows & Glass");
        translationBuilder.add("itemGroup.conquest.aa_advanced_masonry", "Advanced Masonry");
        translationBuilder.add("itemGroup.conquest.c_mosaics_tiles_and_floors", "Mosaics Tiles & Floors");
        translationBuilder.add("itemGroup.conquest.ii_lighting", "Lighting");
        translationBuilder.add("itemGroup.conquest.k_stone", "Stone");
        translationBuilder.add("itemGroup.conquest.nn_crops_and_herbs", "Crops & Herbs");
        translationBuilder.add("itemGroup.conquest.o_water_and_air", "Water & Air");
        translationBuilder.add("itemGroup.conquest.qq_brewing", "Brewing");
        translationBuilder.add("itemGroup.conquest.mm_grasses_and_shrubs", "Grasses & Shrubs");
        translationBuilder.add("itemGroup.conquest.bb_columns", "Columns");
        translationBuilder.add("itemGroup.conquest.cc_plaster_stucco_and_paint", "Plaster Stucco & Paint");
        translationBuilder.add("itemGroup.conquest.ll_logs", "Logs");
        translationBuilder.add("itemGroup.conquest.m_leaves", "Leaves");
        translationBuilder.add("itemGroup.conquest.d_half_timbered_walls", "Half Timbered Walls");
        translationBuilder.add("itemGroup.conquest.j_tool_blocks", "Tool Blocks");
        translationBuilder.add("itemGroup.conquest.a_cobble_and_brick", "Cobble & Brick");
        translationBuilder.add("itemGroup.conquest.kk_grass_and_dirt", "Grass & Dirt");
        translationBuilder.add("itemGroup.conquest.e_planks_and_beams", "Planks & Beams");
        translationBuilder.add("itemGroup.conquest.ee_advanced_carpentry", "Advanced Carpentry");
        translationBuilder.add("itemGroup.conquest.i_decor", "Decor");
        translationBuilder.add("itemGroup.conquest.h_appliances", "Appliances");
        translationBuilder.add("itemGroup.conquest.p_armor", "Armor");
        translationBuilder.add("itemGroup.conquest.dd_roofing", "Roofing");
        translationBuilder.add("itemGroup.conquest.g_cloth_and_fibers", "Cloth & Fibers");
        translationBuilder.add("itemGroup.conquest.ia_pottery", "Pottery");
        translationBuilder.add("itemGroup.conquest.l_sand_and_gravel", "Sand & Gravel");
        translationBuilder.add("itemGroup.conquest.jj_food_blocks", "Food Blocks");
        translationBuilder.add("itemGroup.conquest.gg_furniture", "Furniture");
        translationBuilder.add("itemGroup.conquest.rr_utility", "All Blocks");
        translationBuilder.add("itemGroup.conquest.n_flowers", "Flowers");
        translationBuilder.add("itemGroup.conquest.q_food_and_consumables", "Food & Consumables");
        translationBuilder.add("itemGroup.conquest.hh_storage", "Storage");
        translationBuilder.add("itemGroup.conquest.r_miscellaneous", "Miscellaneous");
        translationBuilder.add("itemGroup.conquest.oo_animals", "Animals");

        translationBuilder.add("item.conquest.mallet_item", "Mallet (Conquest Toggle Tool)");
        translationBuilder.add("tooltip.conquest.mallet_item", "§6Use this (right-click) on toggleable blocks to change their shape");

        //Configs
        translationBuilder.add("options.conquest.title", "Conquest Reforged - Configurations");
        translationBuilder.add("options.conquest.plant_breaking", "Plant Breaking");
        translationBuilder.add("options.conquest.plant_breaking.tooltip", "Toggle on to make Conquest plants break when they don't have a block supporting them below.");
        translationBuilder.add("options.conquest.plant_slowness", "Plant Slowness");
        translationBuilder.add("options.conquest.plant_slowness.tooltip", "Toggle on to make plants slow the player when walking through them.");
        translationBuilder.add("options.conquest.pass_through_leaves", "Pass-through Leaves");
        translationBuilder.add("options.conquest.pass_through_leaves.tooltip", "Toggle on to be able to walk through leaves.");

    }
}