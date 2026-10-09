package com.conquestrefabricated.core.data;

import com.conquestrefabricated.core.Modules;
import com.conquestrefabricated.core.asset.lang.Lore;
import com.conquestrefabricated.core.asset.lang.Translations;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModLangManualProvider extends FabricLanguageProvider {

    public ModLangManualProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
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
        translationBuilder.add("tooltip.conquest.block.loom_toggle_4", "§6Toggles: Mallet - Size, Sneak - Position, Rugs & Canvas - Weave, Hand - Open");
        translationBuilder.add("tooltip.conquest.block.board_toggle", "§6Toggles: Length (Right-Click), Width (Shift+Right-Click)");
        translationBuilder.add(Lore.HINT_KEY, Lore.HINT_FALLBACK);
        /* === Module names, shown on advanced tooltips (F3+H) === */
        for (String moduleId : Modules.all()) {
            translationBuilder.add(Modules.key(moduleId), Modules.displayName(moduleId));
        }

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
        translationBuilder.add("itemGroup.conquest.rr_utility", "");
        translationBuilder.add("itemGroup.conquest.n_flowers", "Flowers");
        translationBuilder.add("itemGroup.conquest.q_food_and_consumables", "Food & Consumables");
        translationBuilder.add("itemGroup.conquest.hh_storage", "Storage");
        translationBuilder.add("itemGroup.conquest.r_miscellaneous", "Miscellaneous");
        translationBuilder.add("itemGroup.conquest.oo_animals", "Animals");

        /* === Arms station === */
        translationBuilder.add("block.conquest.arms_station", "Arms Station");
        translationBuilder.add("container.conquest.arms_station", "Arms Station");
        translationBuilder.add("tooltip.conquest.block.arms_station", "§eForges Conquest's medieval armors, weapons, and shields§r");
        translationBuilder.add("tooltip.conquest.arms_station.material", "Material: %s");
        /* Material names for reforged gear. Unlisted materials fall back to their id in title case. */
        translationBuilder.add("material.conquest.wooden", "Wood");
        translationBuilder.add("material.conquest.stone", "Stone");
        translationBuilder.add("material.conquest.copper", "Copper");
        translationBuilder.add("material.conquest.iron", "Iron");
        translationBuilder.add("material.conquest.gold", "Gold");
        translationBuilder.add("material.conquest.diamond", "Diamond");
        translationBuilder.add("material.conquest.netherite", "Netherite");
        translationBuilder.add("material.conquest.leather", "Leather");
        translationBuilder.add("material.conquest.chain", "Chainmail");
        translationBuilder.add("material.conquest.turtle", "Turtle Scute");

        /* === Crafting tools === */
        translationBuilder.add("container.conquest.station.show_variants", "Show every shape");
        translationBuilder.add("container.conquest.station.hide_variants", "Show base blocks only");
        translationBuilder.add("container.conquest.station.needs", "Needs: %s");
        translationBuilder.add("item.conquest.woodworking_tools", "Woodworking Tools");
        translationBuilder.add("item.conquest.mason_tools", "Mason's Tools");
        translationBuilder.add("item.conquest.metalworking_tools", "Metalworking Tools");
        translationBuilder.add("item.conquest.landscaping_tools", "Landscaping Tools");
        translationBuilder.add("container.conquest.woodworking_tools", "Woodworking");
        translationBuilder.add("container.conquest.mason_tools", "Masonry");
        translationBuilder.add("container.conquest.metalworking_tools", "Metalworking");
        translationBuilder.add("container.conquest.landscaping_tools", "Landscaping");
        translationBuilder.add("tooltip.conquest.item.woodworking_tools", "§eRight-click to shape planks, beams and panelling§r");
        translationBuilder.add("tooltip.conquest.item.mason_tools", "§eRight-click to shape ashlar, brick and tile§r");
        translationBuilder.add("tooltip.conquest.item.metalworking_tools", "§eRight-click to shape plate, bar and grillwork§r");
        translationBuilder.add("tooltip.conquest.item.landscaping_tools", "§eRight-click to shape soil, turf and gravel§r");

        /* === Painter's kit === */
        translationBuilder.add("item.conquest.painters_kit", "Painter's Kit");
        translationBuilder.add("container.conquest.painters_kit", "Painting");
        translationBuilder.add("tooltip.conquest.item.painters_kit", "§eRight-click to paint plaster, stucco and washes§r");

        /* === The lime cycle === */
        translationBuilder.add("item.conquest.quicklime", "Quicklime");
        translationBuilder.add("item.conquest.slaked_lime", "Slaked Lime");
        translationBuilder.add("item.conquest.lime_plaster", "Lime Plaster");

        /* === Loom === */
        translationBuilder.add("container.conquest.loom.progress", "Weaving: %s%%");
        translationBuilder.add("container.conquest.loom.confirm", "Begin weaving");
        translationBuilder.add("container.conquest.loom.stop", "Stop weaving");

        /* === Pottery wheel === */
        translationBuilder.add("container.conquest.pottery_wheel", "Pottery Wheel");
        translationBuilder.add("container.conquest.pottery_wheel.progress", "Shaping: %s%%");
        translationBuilder.add("container.conquest.pottery_wheel.confirm", "Begin shaping");
        translationBuilder.add("container.conquest.pottery_wheel.stop", "Stop shaping");
        translationBuilder.add("container.conquest.pottery_wheel.no_selection", "Choose something to shape");
        translationBuilder.add("container.conquest.loom.no_selection", "Choose something to weave");

        /* === Soaking barrel and tanning frame === */
        translationBuilder.add("message.conquest.soaking.progress", "Soaking: %s%%");
        translationBuilder.add("message.conquest.salt_pan.drying", "The brine is still drying in the sun");
        translationBuilder.add("message.conquest.brine_kettle.too_salty", "Too salty to scoop out - boil it down");
        translationBuilder.add("options.conquest.salt_production", "Salt Production");
        translationBuilder.add("options.conquest.salt_production.tooltip", "Toggle off to stop sea water being scooped and poured and salt pans working.");
        translationBuilder.add("tooltip.conquest.freshness.fresh", "Fresh (%s%% left)");
        translationBuilder.add("tooltip.conquest.freshness.ageing", "Ageing (%s%% left)");
        translationBuilder.add("tooltip.conquest.freshness.stale", "Stale (%s%% left)");
        translationBuilder.add("tooltip.conquest.freshness.nearly_rotten", "Nearly rotten (%s%% left)");
        translationBuilder.add("options.conquest.spoilage", "Food Spoilage");
        translationBuilder.add("options.conquest.spoilage.tooltip", "Toggle on to make food go off over time. Toggle off to stop it; food that has already aged keeps its freshness.");
        translationBuilder.add("message.conquest.soaking.too_far_gone", "That is too far gone to cure");
        translationBuilder.add("message.conquest.cauldron.too_salty", "Too salty to scoop out - boil it down");
        translationBuilder.add("container.conquest.cauldron", "Cauldron");
        translationBuilder.add("container.conquest.cauldron.empty", "Empty");
        translationBuilder.add("container.conquest.cauldron.water", "Water: %s/3");
        translationBuilder.add("container.conquest.cauldron.sea_water", "Sea water: %s/3");
        translationBuilder.add("container.conquest.packing_box", "Packing Box");
        translationBuilder.add("container.conquest.cauldron.slot.ingredient", "Ingredient");
        translationBuilder.add("container.conquest.cauldron.slot.ingredient.hint", "Up to three. A recipe uses one of each.");
        translationBuilder.add("container.conquest.cauldron.slot.fuel", "Fuel");
        translationBuilder.add("container.conquest.cauldron.slot.fuel.hint", "Anything a furnace burns.");
        translationBuilder.add("container.conquest.cauldron.slot.output", "Result");
        translationBuilder.add("container.conquest.cauldron.slot.output.hint", "Take what it made here.");
        translationBuilder.add("tooltip.conquest.spoilage.keeps", "%s - keeps about %s days");
        translationBuilder.add("tooltip.conquest.spoilage.class.produce", "Fruit and vegetables");
        translationBuilder.add("tooltip.conquest.spoilage.class.grain", "Grain");
        translationBuilder.add("tooltip.conquest.spoilage.class.meat", "Meat");
        translationBuilder.add("tooltip.conquest.spoilage.class.fish", "Fish");
        translationBuilder.add("tooltip.conquest.spoilage.class.dairy", "Dairy");
        translationBuilder.add("tooltip.conquest.spoilage.class.baked", "Baked goods");
        translationBuilder.add("tooltip.conquest.spoilage.class.preserved", "Preserved");
        translationBuilder.add("tooltip.conquest.spoilage.rots_in", "Rots in about %s");
        translationBuilder.add("tooltip.conquest.time.day", "%s day");
        translationBuilder.add("tooltip.conquest.time.days", "%s days");
        translationBuilder.add("tooltip.conquest.time.hour", "%s hour");
        translationBuilder.add("tooltip.conquest.time.hours", "%s hours");
        translationBuilder.add("tooltip.conquest.time.and", "%s %s");
        translationBuilder.add("tooltip.conquest.time.under_an_hour", "less than an hour");
        translationBuilder.add("tooltip.conquest.time.with_real", "%s (%s min real time)");
        translationBuilder.add("tooltip.conquest.spoilage.where", "%s - ages at %s%% of the usual pace");
        translationBuilder.add("tooltip.conquest.spoilage.place.carried", "Carried");
        translationBuilder.add("tooltip.conquest.spoilage.place.open", "In the open");
        translationBuilder.add("tooltip.conquest.spoilage.place.roofed", "Under a roof");
        translationBuilder.add("tooltip.conquest.spoilage.place.attic", "In an attic");
        translationBuilder.add("tooltip.conquest.spoilage.place.cellar", "In a cellar");
        translationBuilder.add("container.conquest.packing_box.curing", "Curing... %s%%");
        translationBuilder.add("container.conquest.packing_box.state.101", "Needs salt in the crate");
        translationBuilder.add("container.conquest.packing_box.state.102", "Too far gone to cure");
        translationBuilder.add("container.conquest.packing_box.state.103", "No room for the cured goods");
        translationBuilder.add("tooltip.conquest.ripening", "Ripening: %s%% - ready in about %s");
        translationBuilder.add("message.conquest.soaking.busy", "The barrel is already in use");
        translationBuilder.add("message.conquest.soaking.needs_water", "The barrel needs water first");
        translationBuilder.add("message.conquest.soaking.needs_additive", "It needs something added to the water first");
        translationBuilder.add("message.conquest.soaking.treated", "The water is treated with %s");
        translationBuilder.add("message.conquest.soaking.already_treated", "The water is already treated with %s");
        translationBuilder.add("message.conquest.stretching.progress", "Working: %s%%");
        translationBuilder.add("message.conquest.stretching.needs_tool", "It needs a tool to work it");

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