package com.conquestrefabricated.content.fuel;

import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Pillar;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.blocks.block.SlabLessLayers;
import com.conquestrefabricated.content.blocks.block.SlabQuarter;
import com.conquestrefabricated.content.blocks.block.VerticalCorner;
import com.conquestrefabricated.content.blocks.block.VerticalCornerLessLayers;
import com.conquestrefabricated.content.blocks.block.VerticalQuarter;
import com.conquestrefabricated.content.blocks.block.VerticalQuarterLessLayers;
import com.conquestrefabricated.content.blocks.block.VerticalSlab;
import com.conquestrefabricated.content.blocks.block.VerticalSlabLessLayers;
import com.conquestrefabricated.content.blocks.block.directional.LayerDirectional;
import com.conquestrefabricated.core.item.family.Family;
import com.conquestrefabricated.core.item.family.FamilyRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * How long Conquest's logs and planks burn, which is what vanilla's are worth.
 *
 * <p>Vanilla fuels logs and planks through item tags our blocks are not in, so they would not burn at
 * all. A block counts as wood if it, or the parent of its family, is a log or planks, which brings in
 * every shape: a full block burns as long as a vanilla log or plank, and the thin ones as long as a
 * vanilla wooden slab.</p>
 */
public final class WoodFuel {

    /** Vanilla's burn time for a log or plank, in ticks. */
    public static final int FULL = 300;
    /** Vanilla's burn time for a wooden slab. */
    public static final int PARTIAL = 150;

    private WoodFuel() {
    }

    /** Ticks {@code block} burns for in a furnace, or 0 if it is not wooden. */
    public static int burnTime(Block block, Set<Block> woodTagged) {
        if (!isWood(block, woodTagged)) {
            return 0;
        }
        return isPartial(block) ? PARTIAL : FULL;
    }

    private static boolean isWood(Block block, Set<Block> woodTagged) {
        if (woodTagged.contains(block)) {
            return true;
        }
        Family<Block> family = FamilyRegistry.BLOCKS.getFamily(block);
        if (family.isAbsent()) {
            return false;
        }
        Block root = family.getRoot();
        return root != null && woodTagged.contains(root);
    }

    /** Every block in the log and plank tags, read from {@code registries} once they are loaded. */
    public static Set<Block> woodTagged(HolderLookup.Provider registries) {
        HolderLookup<Block> blocks = registries.lookupOrThrow(Registries.BLOCK);
        Set<Block> wood = new HashSet<>();
        for (TagKey<Block> tag : List.of(BlockTags.LOGS, BlockTags.PLANKS)) {
            blocks.get(tag).ifPresent(holders -> holders.forEach(holder -> wood.add(holder.value())));
        }
        return wood;
    }

    private static boolean isPartial(Block block) {
        return block instanceof Layer || block instanceof Slab || block instanceof LayerDirectional
                || block instanceof SlabLessLayers || block instanceof SlabQuarter
                || block instanceof VerticalSlab || block instanceof VerticalSlabLessLayers
                || block instanceof VerticalCorner || block instanceof VerticalCornerLessLayers
                || block instanceof VerticalQuarter || block instanceof VerticalQuarterLessLayers
                || block instanceof Pillar;
    }
}
