package com.conquestrefabricated.content.blocks.init;

import com.conquestrefabricated.content.blocks.group.ModGroups;
import com.conquestrefabricated.core.block.factory.TypeList;
import com.conquestrefabricated.core.item.family.Family;
import com.conquestrefabricated.core.item.family.FamilyRegistry;
import com.conquestrefabricated.core.item.family.block.BlockFamily;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class BlockFamilyInit {

    private static final Gson GSON = new Gson();

    public static void init() {
//        setRoot("conquest:limestone_cobble_small_arch", Blocks.COBBLESTONE, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE_WALL);
//        setRoot("conquest:mossy_limestone_cobble_small_arch", Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_WALL);
//        setRoot("conquest:limestone_brick_small_arch", Blocks.STONE_BRICKS, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICK_WALL);
//        setRoot("conquest:mossy_limestone_brick_small_arch", Blocks.MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_WALL);
//        setRoot("conquest:cracked_limestone_brick_small_arch", Blocks.CRACKED_STONE_BRICKS);
//        setRoot("conquest:large_andesite_masonry_small_arch", Blocks.POLISHED_ANDESITE, Blocks.POLISHED_ANDESITE_STAIRS);
//        setRoot("conquest:large_light_limestone_brick_small_arch", Blocks.POLISHED_DIORITE, Blocks.POLISHED_DIORITE_STAIRS);
//        setRoot("conquest:large_granite_brick_small_arch", Blocks.POLISHED_GRANITE, Blocks.POLISHED_GRANITE_STAIRS);
//        setRoot("conquest:clay_brick_small_arch", Blocks.BRICKS, Blocks.BRICK_STAIRS, Blocks.BRICK_WALL);
//        setRoot("conquest:concrete_wall_small_arch", Blocks.LIGHT_GRAY_CONCRETE);
//        setRoot("conquest:red_sandstone_cobble_small_arch", Blocks.RED_SANDSTONE, Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE_WALL);
//        setRoot("conquest:sandstone_small_arch", Blocks.SANDSTONE, Blocks.SANDSTONE_STAIRS, Blocks.SANDSTONE_WALL);
//        setRoot("conquest:marble_brick_small_arch", Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_STAIRS);
//        setRoot("conquest:prismarine_brick_small_arch", Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICK_STAIRS);
//        setRoot("conquest:dark_prismarine_small_arch", Blocks.DARK_PRISMARINE, Blocks.DARK_PRISMARINE_STAIRS);
//        setRoot("conquest:white_concrete_powder_layer", Blocks.WHITE_CONCRETE_POWDER);
//        setRoot("conquest:white_concrete_small_arch", Blocks.WHITE_CONCRETE);
//        setRoot("conquest:orange_concrete_powder_layer", Blocks.ORANGE_CONCRETE_POWDER);
//        setRoot("conquest:orange_concrete_small_arch", Blocks.ORANGE_CONCRETE);
//        setRoot("conquest:magenta_concrete_powder_layer", Blocks.MAGENTA_CONCRETE_POWDER);
//        setRoot("conquest:light_blue_concrete_powder_layer", Blocks.LIGHT_BLUE_CONCRETE_POWDER);
//        setRoot("conquest:light_blue_concrete_small_arch", Blocks.LIGHT_BLUE_CONCRETE);
//        setRoot("conquest:yellow_concrete_powder_layer", Blocks.YELLOW_CONCRETE_POWDER);
//        setRoot("conquest:yellow_concrete_small_arch", Blocks.YELLOW_CONCRETE);
//        setRoot("conquest:lime_concrete_powder_layer", Blocks.LIME_CONCRETE_POWDER);
//        setRoot("conquest:pink_concrete_powder_layer", Blocks.PINK_CONCRETE_POWDER);
//        setRoot("conquest:pink_concrete_small_arch", Blocks.PINK_CONCRETE);
//        setRoot("conquest:gray_concrete_powder_layer", Blocks.GRAY_CONCRETE_POWDER);
//        setRoot("conquest:gray_concrete_small_arch", Blocks.GRAY_CONCRETE);
//        setRoot("conquest:light_gray_concrete_powder_layer", Blocks.LIGHT_GRAY_CONCRETE_POWDER);
//        setRoot("conquest:cyan_concrete_powder_layer", Blocks.CYAN_CONCRETE_POWDER);
//        setRoot("conquest:purple_concrete_powder_layer", Blocks.PURPLE_CONCRETE_POWDER);
//        setRoot("conquest:purple_concrete_small_arch", Blocks.PURPLE_CONCRETE);
//        setRoot("conquest:black_concrete_small_arch", Blocks.BLACK_CONCRETE);
//        setRoot("conquest:blue_concrete_powder_layer", Blocks.BLUE_CONCRETE_POWDER);
//        setRoot("conquest:brown_concrete_powder_layer", Blocks.BROWN_CONCRETE_POWDER);
//        setRoot("conquest:brown_concrete_small_arch", Blocks.BROWN_CONCRETE);
//        setRoot("conquest:green_concrete_small_arch", Blocks.GREEN_CONCRETE);
//        setRoot("conquest:green_concrete_powder_layer", Blocks.GREEN_CONCRETE_POWDER);
//        setRoot("conquest:red_concrete_powder_layer", Blocks.RED_CONCRETE_POWDER);
//        setRoot("conquest:black_concrete_powder_layer", Blocks.BLACK_CONCRETE_POWDER);
//        setRoot("conquest:dirty_blue_clay_beaver_tail_tile_stairs", Blocks.LIGHT_BLUE_TERRACOTTA);
//        setRoot("conquest:blue_clay_beaver_tail_tile_stairs", Blocks.BLUE_TERRACOTTA);
//        setRoot("conquest:old_slate_roof_tile_stairs", Blocks.CYAN_TERRACOTTA);
//        setRoot("conquest:overgrown_green_clay_shingle_stairs", Blocks.LIME_TERRACOTTA);
//        setRoot("conquest:green_clay_shingle_stairs", Blocks.GREEN_TERRACOTTA);
//        setRoot("conquest:pink_clay_tile_stairs", Blocks.PINK_TERRACOTTA);
//        setRoot("conquest:bundled_hay_slab", Blocks.HAY_BLOCK);
//        setRoot("conquest:bamboo_mosaic_slab", Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_MOSAIC_STAIRS);
//        setRoot("conquest:bamboo_wood_plank_small_window", Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_STAIRS, Blocks.BAMBOO_FENCE, Blocks.BAMBOO_FENCE_GATE, Blocks.BAMBOO_BUTTON, Blocks.BAMBOO_PRESSURE_PLATE, Blocks.BAMBOO_SIGN, Blocks.BAMBOO_HANGING_SIGN);
//        setRoot("conquest:cherry_wood_plank_small_window", Blocks.CHERRY_PLANKS, Blocks.CHERRY_STAIRS, Blocks.CHERRY_FENCE, Blocks.CHERRY_FENCE_GATE, Blocks.CHERRY_BUTTON, Blocks.CHERRY_PRESSURE_PLATE, Blocks.CHERRY_SIGN, Blocks.CHERRY_HANGING_SIGN);
//        setRoot("conquest:mangrove_wood_plank_small_window", Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_STAIRS, Blocks.MANGROVE_FENCE, Blocks.MANGROVE_FENCE_GATE, Blocks.MANGROVE_BUTTON, Blocks.MANGROVE_PRESSURE_PLATE, Blocks.MANGROVE_SIGN, Blocks.MANGROVE_HANGING_SIGN);
//        setRoot("conquest:oak_wood_plank_small_window", Blocks.OAK_PLANKS, Blocks.OAK_STAIRS, Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.OAK_BUTTON, Blocks.OAK_PRESSURE_PLATE, Blocks.OAK_SIGN, Blocks.OAK_HANGING_SIGN);
//        setRoot("conquest:spruce_wood_plank_small_window", Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_BUTTON, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.SPRUCE_SIGN, Blocks.SPRUCE_HANGING_SIGN);
//        setRoot("conquest:acacia_wood_plank_small_window", Blocks.ACACIA_PLANKS, Blocks.ACACIA_STAIRS, Blocks.ACACIA_FENCE, Blocks.ACACIA_FENCE_GATE, Blocks.ACACIA_BUTTON, Blocks.ACACIA_PRESSURE_PLATE, Blocks.ACACIA_SIGN, Blocks.ACACIA_HANGING_SIGN);
//        setRoot("conquest:birch_wood_plank_small_window", Blocks.BIRCH_PLANKS, Blocks.BIRCH_STAIRS, Blocks.BIRCH_FENCE, Blocks.BIRCH_FENCE_GATE, Blocks.BIRCH_BUTTON, Blocks.BIRCH_PRESSURE_PLATE, Blocks.BIRCH_SIGN, Blocks.BIRCH_HANGING_SIGN);
//        setRoot("conquest:rough_palm_wood_plank_small_window", Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_STAIRS, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_FENCE_GATE, Blocks.JUNGLE_BUTTON, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.JUNGLE_SIGN, Blocks.JUNGLE_HANGING_SIGN);
//        setRoot("conquest:dark_oak_wood_plank_small_window", Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_FENCE_GATE, Blocks.DARK_OAK_BUTTON, Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.DARK_OAK_HANGING_SIGN);
//        setRoot("conquest:decayed_maple_wood_plank_small_window", Blocks.PALE_OAK_PLANKS, Blocks.PALE_OAK_STAIRS, Blocks.PALE_OAK_FENCE, Blocks.PALE_OAK_FENCE_GATE, Blocks.PALE_OAK_BUTTON, Blocks.PALE_OAK_PRESSURE_PLATE, Blocks.PALE_OAK_SIGN, Blocks.PALE_OAK_HANGING_SIGN);
//        setRoot("conquest:oak_wood_beam_wall", Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_WOOD);
//        setRoot("conquest:spruce_wood_beam_wall", Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_WOOD);
//        setRoot("conquest:birch_wood_beam_wall", Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_WOOD);
//        setRoot("conquest:palm_wood_beam_wall", Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_WOOD);
//        setRoot("conquest:acacia_wood_beam_wall", Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_WOOD);
//        setRoot("conquest:dark_oak_wood_beam_wall", Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_WOOD);
//        setRoot("conquest:cherry_wood_beam_wall", Blocks.STRIPPED_CHERRY_LOG, Blocks.STRIPPED_CHERRY_WOOD);
//        setRoot("conquest:mangrove_wood_beam_wall", Blocks.STRIPPED_MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_WOOD);
//        setRoot("conquest:decayed_maple_wood_beam_wall", Blocks.STRIPPED_PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_WOOD);
//        setRoot("conquest:bamboo_wood_beam_wall", Blocks.STRIPPED_BAMBOO_BLOCK);
//        setRoot("conquest:green_bamboo_wood_beam_wall", Blocks.BAMBOO_BLOCK);
//        setRoot("conquest:fancy_stone_column_vertical_slab", Blocks.CHISELED_STONE_BRICKS);
//        setRoot("conquest:iron_block_arrowslit", Blocks.IRON_BLOCK);
//        setRoot("conquest:carbonite_block_vertical_slab", Blocks.CYAN_CONCRETE);
//        setRoot("conquest:carbonite_paneling_small_arch", Blocks.PURPUR_BLOCK);
//        setRoot("conquest:carbonite_pillar_vertical_slab", Blocks.PURPUR_PILLAR);
//        setRoot("conquest:end_stone_brick_small_arch", Blocks.END_STONE_BRICKS, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE_BRICK_WALL);
//        setRoot("conquest:old_rusted_metal_tile_arrowslit", Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE);
//        setRoot("conquest:old_rusted_metal_block_arrowslit", Blocks.RED_NETHER_BRICKS, Blocks.RED_NETHER_BRICK_STAIRS);
//        setRoot("conquest:red_painted_angled_tile_slab", Blocks.MAGENTA_CONCRETE);
//        setRoot("conquest:engraved_red_sandstone_pillar_vertical_slab", Blocks.CHISELED_RED_SANDSTONE);
//        setRoot("conquest:red_sandstone_pillar_vertical_slab", Blocks.CUT_RED_SANDSTONE);
//        setRoot("conquest:smooth_sandstone_column_vertical_slab", Blocks.CUT_SANDSTONE);
//        setRoot("conquest:sandstone_bordered_stucco_vertical_slab", Blocks.CHISELED_SANDSTONE);
//        setRoot("conquest:chiseled_polished_parian_marble_design_vertical_slab", Blocks.CHISELED_QUARTZ_BLOCK);
//        setRoot("conquest:polished_parian_marble_column_vertical_slab", Blocks.QUARTZ_PILLAR);
//        setRoot("conquest:worn_magenta_plaster_small_arch", Blocks.MAGENTA_TERRACOTTA);
//        setRoot("conquest:worn_light_gray_plaster_small_arch", Blocks.LIGHT_GRAY_TERRACOTTA);
//        setRoot("conquest:worn_purple_plaster_small_arch", Blocks.PURPLE_TERRACOTTA);
//        setRoot("conquest:blue_painted_wall_small_arch", Blocks.BLUE_CONCRETE);
//        setRoot("conquest:green_painted_wall_small_arch", Blocks.LIME_CONCRETE);
//        setRoot("conquest:red_painted_wall_small_arch", Blocks.RED_CONCRETE);
//        setRoot("conquest:white_stained_glass_vertical_slab", Blocks.WHITE_STAINED_GLASS);
//        setRoot("conquest:orange_stained_glass_vertical_slab", Blocks.ORANGE_STAINED_GLASS);
//        setRoot("conquest:magenta_stained_glass_vertical_slab", Blocks.MAGENTA_STAINED_GLASS);
//        setRoot("conquest:light_blue_stained_glass_vertical_slab", Blocks.LIGHT_BLUE_STAINED_GLASS);
//        setRoot("conquest:yellow_stained_glass_vertical_slab", Blocks.YELLOW_STAINED_GLASS);
//        setRoot("conquest:lime_stained_glass_vertical_slab", Blocks.LIME_STAINED_GLASS);
//        setRoot("conquest:pink_stained_glass_vertical_slab", Blocks.PINK_STAINED_GLASS);
//        setRoot("conquest:gray_stained_glass_vertical_slab", Blocks.GRAY_STAINED_GLASS);
//        setRoot("conquest:light_gray_stained_glass_vertical_slab", Blocks.LIGHT_GRAY_STAINED_GLASS);
//        setRoot("conquest:cyan_stained_glass_vertical_slab", Blocks.CYAN_STAINED_GLASS);
//        setRoot("conquest:purple_stained_glass_vertical_slab", Blocks.PURPLE_STAINED_GLASS);
//        setRoot("conquest:blue_stained_glass_vertical_slab", Blocks.BLUE_STAINED_GLASS);
//        setRoot("conquest:brown_stained_glass_vertical_slab", Blocks.BROWN_STAINED_GLASS);
//        setRoot("conquest:green_stained_glass_vertical_slab", Blocks.GREEN_STAINED_GLASS);
//        setRoot("conquest:red_stained_glass_vertical_slab", Blocks.RED_STAINED_GLASS);
//        setRoot("conquest:black_stained_glass_vertical_slab", Blocks.BLACK_STAINED_GLASS);
//        setRoot("conquest:circular_glass_1_vertical_slab", Blocks.GLASS);
//        setRoot("conquest:white_wool_slab", Blocks.WHITE_WOOL);
//        setRoot("conquest:orange_wool_slab", Blocks.ORANGE_WOOL);
//        setRoot("conquest:magenta_wool_slab", Blocks.MAGENTA_WOOL);
//        setRoot("conquest:light_blue_wool_slab", Blocks.LIGHT_BLUE_WOOL);
//        setRoot("conquest:yellow_wool_slab", Blocks.YELLOW_WOOL);
//        setRoot("conquest:lime_wool_slab", Blocks.LIME_WOOL);
//        setRoot("conquest:pink_wool_slab", Blocks.PINK_WOOL);
//        setRoot("conquest:gray_wool_slab", Blocks.GRAY_WOOL);
//        setRoot("conquest:light_gray_wool_slab", Blocks.LIGHT_GRAY_WOOL);
//        setRoot("conquest:cyan_wool_slab", Blocks.CYAN_WOOL);
//        setRoot("conquest:purple_wool_slab", Blocks.PURPLE_WOOL);
//        setRoot("conquest:blue_wool_slab", Blocks.BLUE_WOOL);
//        setRoot("conquest:brown_wool_slab", Blocks.BROWN_WOOL);
//        setRoot("conquest:green_wool_slab", Blocks.GREEN_WOOL);
//        setRoot("conquest:red_wool_slab", Blocks.RED_WOOL);
//        setRoot("conquest:black_wool_slab", Blocks.BLACK_WOOL);
//        setRoot("conquest:large_mirror_2", Blocks.DIAMOND_BLOCK);
//        setRoot("conquest:red_mudstone_slab", Blocks.RED_TERRACOTTA);
//        setRoot("conquest:limestone_arrowslit", Blocks.STONE);
//        setRoot("conquest:brown_mudstone_slab", Blocks.BROWN_TERRACOTTA);
//        setRoot("conquest:yellow_mudstone_slab", Blocks.YELLOW_TERRACOTTA);
//        setRoot("conquest:light_limestone_arrowslit", Blocks.DIORITE);
//        setRoot("conquest:gray_cave_silt_slab", Blocks.GRAY_TERRACOTTA);
//        setRoot("conquest:umbre_mudstone_slab", Blocks.TERRACOTTA);
//        setRoot("conquest:prismarine_slab", Blocks.PRISMARINE, Blocks.PRISMARINE_WALL);
//        setRoot("conquest:andesite_arrowslit", Blocks.ANDESITE);
//        setRoot("conquest:red_mudstone_slab", Blocks.RED_TERRACOTTA);
//        setRoot("conquest:black_hardened_clay_slab", Blocks.BLACK_TERRACOTTA);
//        setRoot("conquest:magma_slab", Blocks.MAGMA_BLOCK);
//        setRoot("conquest:orange_mudstone_slab", Blocks.ORANGE_TERRACOTTA);
//        setRoot("conquest:granite_arrowslit", Blocks.GRANITE);
//        setRoot("conquest:peridotite_slab", Blocks.BEDROCK);
//        setRoot("conquest:end_stone_slab", Blocks.END_STONE);
//        setRoot("conquest:light_mudstone_slab", Blocks.WHITE_TERRACOTTA);
//        setRoot("conquest:obsidian_slab", Blocks.OBSIDIAN);
//        setRoot("conquest:stack_of_coal_layer", Blocks.COAL_BLOCK);
//        setRoot("conquest:grass_block_layer", Blocks.GRASS_BLOCK);
//        setRoot("conquest:gray_clay_slab", Blocks.CLAY);
//        setRoot("conquest:dark_mud_slab", Blocks.MUD);
//        setRoot("conquest:packed_mud_slab", Blocks.PACKED_MUD);
//        setRoot("conquest:loamy_dirt_slab", Blocks.DIRT);
//        setRoot("conquest:vibrant_autumnal_forest_floor_layer", Blocks.PODZOL);
//        setRoot("conquest:mycelium_layer", Blocks.MYCELIUM);
//        setRoot("conquest:dirt_path_layer", Blocks.COARSE_DIRT);
//        setRoot("conquest:netherrack_layer", Blocks.NETHERRACK);
//        setRoot("conquest:soul_sand_layer", Blocks.SOUL_SAND);
//        setRoot("conquest:gravel_layer", Blocks.GRAVEL);
//        setRoot("conquest:red_sand_layer", Blocks.RED_SAND);
//        setRoot("conquest:sand_layer", Blocks.SAND);
//        setRoot("conquest:snow_slab", Blocks.SNOW_BLOCK, Blocks.SNOW);
//        setRoot("conquest:palm_log_large_branch", Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD);
//        setRoot("conquest:acacia_log_large_branch", Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD);
//        setRoot("conquest:oak_log_large_branch", Blocks.OAK_LOG, Blocks.OAK_WOOD);
//        setRoot("conquest:dark_oak_log_large_branch", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD);
//        setRoot("conquest:birch_log_large_branch", Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD);
//        setRoot("conquest:dark_spruce_log_large_branch", Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD);
//        setRoot("conquest:cherry_log_large_branch", Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD);
//        setRoot("conquest:mangrove_log_large_branch", Blocks.MANGROVE_LOG, Blocks.MANGROVE_WOOD);
//        setRoot("conquest:decayed_maple_log_large_branch", Blocks.PALE_OAK_LOG, Blocks.PALE_OAK_WOOD);
//
//        setRoot("conquest:ancient_debris_layer", Blocks.ANCIENT_DEBRIS);
//        setRoot("conquest:soul_soil_layer", Blocks.SOUL_SOIL);
//        setRoot("conquest:crimson_nylium_layer", Blocks.CRIMSON_NYLIUM);
//        setRoot("conquest:warped_nylium_layer", Blocks.WARPED_NYLIUM);
//        setRoot("conquest:nether_wart_block_layer", Blocks.NETHER_WART_BLOCK);
//        setRoot("conquest:warped_wart_block_layer", Blocks.WARPED_WART_BLOCK);
//
//        setRoot("conquest:tuff_brick_vertical_slab", Blocks.TUFF_BRICKS, Blocks.TUFF_BRICK_STAIRS);
//        setRoot("conquest:large_tuff_brick_vertical_slab", Blocks.POLISHED_TUFF, Blocks.POLISHED_TUFF_STAIRS, Blocks.POLISHED_TUFF_WALL);
//        setRoot("conquest:chiseled_tuff_vertical_slab", Blocks.CHISELED_TUFF);
//        setRoot("conquest:chiseled_tuff_brick_vertical_slab", Blocks.CHISELED_TUFF_BRICKS);
//
//        setRoot("conquest:resin_block_vertical_slab", Blocks.RESIN_BLOCK);
//        setRoot("conquest:resin_brick_vertical_slab", Blocks.RESIN_BRICKS, Blocks.RESIN_BRICK_STAIRS, Blocks.RESIN_BRICK_WALL);
//        setRoot("conquest:chiseled_resin_pillar_vertical_slab", Blocks.CHISELED_RESIN_BRICKS);
//
//        setRoot("conquest:blackstone_slab", Blocks.BLACKSTONE, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE_WALL);
//        setRoot("conquest:gilded_blackstone_slab", Blocks.GILDED_BLACKSTONE);
//        setRoot("conquest:crying_obsidian_slab", Blocks.CRYING_OBSIDIAN);
//
//        setRoot("conquest:large_marble_blocks_small_arch", Blocks.QUARTZ_BRICKS);
//        setRoot("conquest:polished_basalt_vertical_slab", Blocks.POLISHED_BASALT);
//        setRoot("conquest:large_smooth_limestone_brick_small_arch", Blocks.SMOOTH_STONE);
//
//        setRoot("conquest:crimson_stem_vertical_slab", Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE);
//        setRoot("conquest:stripped_crimson_stem_vertical_slab", Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_HYPHAE);
//        setRoot("conquest:warped_stem_vertical_slab", Blocks.WARPED_STEM, Blocks.WARPED_HYPHAE);
//        setRoot("conquest:stripped_warped_stem_vertical_slab", Blocks.STRIPPED_WARPED_STEM, Blocks.STRIPPED_WARPED_HYPHAE);
//        setRoot("conquest:basalt_arrowslit", Blocks.BASALT);
//
//        setRoot("conquest:crimson_shell_planks_slab", Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_DOOR, Blocks.CRIMSON_FENCE, Blocks.CRIMSON_FENCE_GATE, Blocks.CRIMSON_PRESSURE_PLATE, Blocks.CRIMSON_BUTTON, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_STAIRS, Blocks.CRIMSON_TRAPDOOR);
//        setRoot("conquest:warped_planks_slab", Blocks.WARPED_PLANKS, Blocks.WARPED_DOOR, Blocks.WARPED_FENCE, Blocks.WARPED_FENCE_GATE, Blocks.WARPED_PRESSURE_PLATE, Blocks.WARPED_BUTTON, Blocks.WARPED_SIGN, Blocks.WARPED_STAIRS, Blocks.WARPED_TRAPDOOR);
//
//        setRoot("conquest:chiseled_polished_blackstone_slab", Blocks.CHISELED_POLISHED_BLACKSTONE);
//        setRoot("conquest:polished_blackstone_small_arch", Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE_BUTTON);
//        setRoot("conquest:polished_blackstone_bricks_small_arch", Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
//        setRoot("conquest:cracked_polished_blackstone_bricks_small_arch", Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
//
//        setRoot("conquest:old_rusted_metal_panel_arrowslit", Blocks.CHISELED_NETHER_BRICKS);
//        setRoot("conquest:cracked_old_rusted_metal_arrowslit", Blocks.CRACKED_NETHER_BRICKS);
//        setRoot("conquest:gold_block_vertical_slab", Blocks.GOLD_BLOCK);
//
//
//        add("conquest:old_iron_block", Blocks.DARK_OAK_SIGN);
//
//        setRoot("conquest:cut_copper_slab", Blocks.WAXED_CUT_COPPER, Blocks.WAXED_CUT_COPPER_STAIRS);
//        setRoot("conquest:exposed_cut_copper_slab", Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS);
//        setRoot("conquest:weathered_cut_copper_slab", Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS);
//        setRoot("conquest:oxidized_cut_copper_slab", Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
//
//        setRoot("conquest:raw_copper_slab", Blocks.RAW_COPPER_BLOCK);
//        setRoot("conquest:raw_gold_slab", Blocks.RAW_GOLD_BLOCK);
//        setRoot("conquest:raw_iron_slab", Blocks.RAW_IRON_BLOCK);
//
//        setRoot("conquest:calcite_and_limestone_slab", Blocks.CALCITE);
//
//        setRoot("conquest:copper_balustrade", Blocks.WAXED_COPPER_BLOCK);
//        setRoot("conquest:exposed_copper_balustrade", Blocks.WAXED_EXPOSED_COPPER);
//        setRoot("conquest:weathered_copper_balustrade", Blocks.WAXED_WEATHERED_COPPER);
//        setRoot("conquest:oxidized_copper_balustrade", Blocks.WAXED_OXIDIZED_COPPER);
//
//        setRoot("conquest:copper_grate_slab", Blocks.WAXED_COPPER_GRATE);
//        setRoot("conquest:exposed_copper_grate_slab", Blocks.WAXED_EXPOSED_COPPER_GRATE);
//        setRoot("conquest:weathered_copper_grate_slab", Blocks.WAXED_WEATHERED_COPPER_GRATE);
//        setRoot("conquest:oxidized_copper_grate_slab", Blocks.WAXED_OXIDIZED_COPPER_GRATE);
//
//        setRoot("conquest:chiseled_copper_slab", Blocks.WAXED_CHISELED_COPPER);
//        setRoot("conquest:exposed_chiseled_copper_slab", Blocks.WAXED_EXPOSED_CHISELED_COPPER);
//        setRoot("conquest:weathered_chiseled_copper_slab", Blocks.WAXED_WEATHERED_CHISELED_COPPER);
//        setRoot("conquest:oxidized_chiseled_copper_slab", Blocks.WAXED_OXIDIZED_CHISELED_COPPER);
//
//        setRoot("conquest:polished_deepslate_small_arch", Blocks.POLISHED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.POLISHED_DEEPSLATE_WALL);
//        setRoot("conquest:deepslate_tile_slab", Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILE_WALL);
//        setRoot("conquest:cracked_deepslate_tile_slab", Blocks.CRACKED_DEEPSLATE_TILES);
//        setRoot("conquest:chiseled_deepslate_slab", Blocks.CHISELED_DEEPSLATE);
//
//        setRoot("conquest:pumice_slab", Blocks.SMOOTH_BASALT);
//
//        setRoot("conquest:ice_slab", Blocks.ICE);
//        setRoot("conquest:packed_ice_slab", Blocks.PACKED_ICE);
//        setRoot("conquest:blue_ice_slab", Blocks.BLUE_ICE);
//
//        setRoot("conquest:bubble_coral_block_slab", Blocks.BUBBLE_CORAL_BLOCK);
//        setRoot("conquest:horn_coral_block_slab", Blocks.HORN_CORAL_BLOCK);
//        setRoot("conquest:fire_coral_block_slab", Blocks.FIRE_CORAL_BLOCK);
//        setRoot("conquest:tube_coral_block_slab", Blocks.TUBE_CORAL_BLOCK);
//        setRoot("conquest:brain_coral_block_slab", Blocks.BRAIN_CORAL_BLOCK);
//        setRoot("conquest:dead_bubble_coral_block_slab", Blocks.DEAD_BUBBLE_CORAL_BLOCK);
//        setRoot("conquest:dead_horn_coral_block_slab", Blocks.DEAD_HORN_CORAL_BLOCK);
//        setRoot("conquest:dead_fire_coral_block_slab", Blocks.DEAD_FIRE_CORAL_BLOCK);
//        setRoot("conquest:dead_tube_coral_block_slab", Blocks.DEAD_TUBE_CORAL_BLOCK);
//        setRoot("conquest:dead_brain_coral_block_slab", Blocks.DEAD_BRAIN_CORAL_BLOCK);
//
//        setRoot("conquest:red_mushroom_block_slab", Blocks.RED_MUSHROOM_BLOCK);
//        setRoot("conquest:brown_mushroom_block_slab", Blocks.BROWN_MUSHROOM_BLOCK);
//        setRoot("conquest:mushroom_stem_slab", Blocks.MUSHROOM_STEM);
//
//        setRoot("conquest:oak_ground_leaves", Blocks.OAK_LEAVES);
//        setRoot("conquest:spruce_ground_needles", Blocks.SPRUCE_LEAVES);
//        setRoot("conquest:birch_straight_branches", Blocks.BIRCH_LEAVES);
//        setRoot("conquest:eucalyptus_ground_leaves", Blocks.JUNGLE_LEAVES);
//        setRoot("conquest:maple_ground_leaves", Blocks.DARK_OAK_LEAVES);
//        setRoot("conquest:acacia_ground_leaves", Blocks.ACACIA_LEAVES);
//        setRoot("conquest:mangrove_ground_leaves", Blocks.MANGROVE_LEAVES);
//        setRoot("conquest:azalea_ground_leaves", Blocks.AZALEA_LEAVES);
//        setRoot("conquest:decayed_maple_ground_leaves", Blocks.PALE_OAK_LEAVES);
//
//        setRoot("conquest:pale_moss_slab", Blocks.PALE_MOSS_BLOCK, Blocks.PALE_MOSS_CARPET);
//
//        addCarpets();
//        addStoneShapes();
    }

    private static void addCarpets() {
        add("conquest:white_carpet", Blocks.WHITE_CARPET);
        add("conquest:orange_carpet", Blocks.ORANGE_CARPET);
        add("conquest:magenta_carpet", Blocks.MAGENTA_CARPET);
        add("conquest:light_blue_carpet", Blocks.LIGHT_BLUE_CARPET);
        add("conquest:yellow_carpet", Blocks.YELLOW_CARPET);
        add("conquest:lime_carpet", Blocks.LIME_CARPET);
        add("conquest:pink_carpet", Blocks.PINK_CARPET);
        add("conquest:gray_carpet", Blocks.GRAY_CARPET);
        add("conquest:light_gray_carpet", Blocks.LIGHT_GRAY_CARPET);
        add("conquest:cyan_carpet", Blocks.CYAN_CARPET);
        add("conquest:purple_carpet", Blocks.PURPLE_CARPET);
        add("conquest:blue_carpet", Blocks.BLUE_CARPET);
        add("conquest:brown_carpet", Blocks.BROWN_CARPET);
        add("conquest:green_carpet", Blocks.GREEN_CARPET);
        add("conquest:red_carpet", Blocks.RED_CARPET);
        add("conquest:black_carpet", Blocks.BLACK_CARPET);
    }

    private static void addStoneShapes() {
        add(Blocks.STONE, Blocks.STONE_STAIRS);
        add(Blocks.ANDESITE, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE_WALL);
        add(Blocks.DIORITE, Blocks.DIORITE_STAIRS, Blocks.DIORITE_WALL);
        add(Blocks.GRANITE, Blocks.GRANITE_STAIRS, Blocks.GRANITE_WALL);
        add(Blocks.POLISHED_ANDESITE, Blocks.POLISHED_ANDESITE_STAIRS);
        add(Blocks.POLISHED_DIORITE, Blocks.POLISHED_DIORITE_STAIRS);
        add(Blocks.POLISHED_GRANITE, Blocks.POLISHED_GRANITE_STAIRS);
    }

    private static void setRoot(String target, Block block) {
        Family<Block> family = FamilyRegistry.BLOCKS.getFamily(Identifier.parse(target));
        family.setRoot(block);
        FamilyRegistry.BLOCKS.register(block, family);
    }

    private static void setRoot(String parent, Block root, Block... blocks) {
        Family<Block> family = FamilyRegistry.BLOCKS.getFamily(Identifier.parse(parent));
        family.setRoot(root);
        FamilyRegistry.BLOCKS.register(root, family);
        for (Block block : blocks) {
            family.add(block);
            FamilyRegistry.BLOCKS.register(block, family);
        }
    }

    private static void add(Block parent, Block... blocks) {
        Family<Block> family = FamilyRegistry.BLOCKS.getFamily(parent);
        for (Block block : blocks) {
            family.add(block);
            FamilyRegistry.BLOCKS.register(block, family);
        }
    }

    private static void add(String parent, Block... blocks) {
        Family<Block> family = FamilyRegistry.BLOCKS.getFamily(Identifier.parse(parent));
        for (Block block : blocks) {
            family.add(block);
            FamilyRegistry.BLOCKS.register(block, family);
        }
    }

    /**
     * Loads block families from JSON files in the datapack
     * Should be called during mod initialization
     * @param resourceManager The resource manager instance
     */
    public static void loadBlockFamilies(ResourceManager resourceManager) {
        Map<Identifier, Resource> resources = resourceManager.listResources(
                "families",
                id -> id.getPath().endsWith(".json")
        );

        resources.forEach((identifier, resource) -> {
            try (InputStreamReader reader = new InputStreamReader(resource.open())) {
                // Parse JSON file
                BlockFamilyJson familyJson = GSON.fromJson(reader, BlockFamilyJson.class);

                // Get parent block
                Block parent = getBlockFromIdentifier(familyJson.parentBlock);
                if (parent == null) {
                    System.err.println("Failed to find parent block: " + familyJson.parentBlock);
                    return;
                }

                // Create a new family with TypeList (you'll need to decide how to create/configure TypeList)
                BlockFamily family = new BlockFamily(ModGroups.UTILITY, TypeList.EMPTY);
                // Add parent block first
                family.add(parent);
                FamilyRegistry.BLOCKS.register(parent, family);

                // Add family blocks
                for (String blockId : familyJson.familyBlocks) {
                    Block block = getBlockFromIdentifier(blockId);
                    if (block != null) {
                        family.add(block);
                        FamilyRegistry.BLOCKS.register(block, family);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing block family file: " + identifier);
                e.printStackTrace();
            }
        });
    }

    /**
     * Helper method to get Block instance from identifier string
     */
    private static Block getBlockFromIdentifier(String identifier) {
        try {
            Identifier blockId = Identifier.parse(identifier);
            return BuiltInRegistries.BLOCK.get(blockId).get().value();
        } catch (Exception e) {
            System.err.println("Invalid block identifier: " + identifier);
            return null;
        }
    }

    private static class BlockFamilyJson {
        @SerializedName("parent_block")
        private String parentBlock;

        @SerializedName("family_blocks")
        private List<String> familyBlocks;
    }
}
