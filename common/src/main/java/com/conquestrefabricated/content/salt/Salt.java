package com.conquestrefabricated.content.salt;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.core.Namespaces;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;

/**
 * What the salt blocks need to know about the world: which water is the sea's, what counts as a heat
 * source, and how to tell a container of sea water from one of fresh.
 *
 * <p>Core never names one of the items; the items belong to whichever module registers salt, and are
 * found through the tags below. An item in {@link #SEA_WATER_BUCKETS} pours out as an empty bucket,
 * one in {@link #SEA_WATER_BOTTLES} as an empty glass bottle.</p>
 */
public final class Salt {

    /** Biomes whose water is salt: scooping a bucket in one gives sea water, in any other fresh. */
    public static final TagKey<Biome> SALT_WATER_BIOMES = TagKey.create(Registries.BIOME,
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "salt_water"));

    /** Blocks that can sit under a kettle and boil it - but only while lit, if they have a {@code lit} state. */
    public static final TagKey<Block> BOILING_HEAT = TagKey.create(Registries.BLOCK,
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "boiling_heat"));

    /** Full buckets of sea water. The first item in the tag is what scooping the sea hands out. */
    public static final TagKey<Item> SEA_WATER_BUCKETS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "sea_water_buckets"));

    /** Bottles of sea water. The first item in the tag is what filling a glass bottle at the sea hands out. */
    public static final TagKey<Item> SEA_WATER_BOTTLES = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "sea_water_bottles"));

    /** Salt a curing vessel will take. */
    public static final TagKey<Item> SALTS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "salts"));

    /** The most brine, in levels, a pan or kettle holds - a bucket fills it, a bottle is one level. */
    public static final int MAX_BRINE = 3;

    private Salt() {
    }

    public static boolean enabled() {
        return ConquestConfig.INSTANCE.saltProduction.get();
    }

    /** True for a still source block of water in a salt-water biome. */
    public static boolean isSeaSource(Level level, BlockPos pos) {
        FluidState fluid = level.getFluidState(pos);
        return fluid.is(FluidTags.WATER) && fluid.isSource() && level.getBiome(pos).is(SALT_WATER_BIOMES);
    }

    /**
     * True if sea water is within reach of a pan at {@code pos}: a source block up to two blocks away
     * sideways and up to two below, so a pan on a beach, a step above the waterline, is still fed.
     */
    public static boolean seaNear(Level level, BlockPos pos) {
        for (BlockPos near : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 0, 2))) {
            if (isSeaSource(level, near)) {
                return true;
            }
        }
        return false;
    }

    /** True if something lit or molten sits at {@code pos} to boil a vessel above it. */
    public static boolean isHeat(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(BOILING_HEAT)) {
            return !state.hasProperty(BlockStateProperties.LIT) || state.getValue(BlockStateProperties.LIT);
        }
        return false;
    }

    public static boolean isSeaBucket(ItemStack stack) {
        return stack.is(SEA_WATER_BUCKETS);
    }

    public static boolean isSeaBottle(ItemStack stack) {
        return stack.is(SEA_WATER_BOTTLES);
    }

    /** What a sea-water container becomes once poured out. */
    public static ItemStack emptied(ItemStack full) {
        return new ItemStack(isSeaBucket(full) ? Items.BUCKET : Items.GLASS_BOTTLE);
    }

    /** The first item of a tag, or empty if no module has filled it. */
    public static ItemStack first(TagKey<Item> tag) {
        for (var holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            return new ItemStack(holder.value());
        }
        return ItemStack.EMPTY;
    }

    /** Hands a stack to a player, in stacks that fit, dropping whatever their inventory cannot take. */
    public static void give(Player player, ItemStack stack) {
        ItemStack rest = stack.copy();
        while (!rest.isEmpty()) {
            player.getInventory().placeItemBackInInventory(rest.split(Math.min(rest.getCount(), rest.getMaxStackSize())));
        }
    }
}
