package com.conquestrefabricated.content.station;

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
import com.conquestrefabricated.core.item.family.block.BlockFamily;
import com.conquestrefabricated.core.item.family.FamilyRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * The block family behind a station's shape toggle.
 *
 * <p>This is the same family the palette wheel shows - {@link FamilyRegistry} - rather than anything
 * written to disk. Conquest used to generate a stonecutting recipe per shape and the picker walked
 * those; the families are the thing those recipes were derived from in the first place, so reading
 * them directly means a station offers exactly what the wheel does, for every module, with no recipe
 * files to generate, ship or keep in step.</p>
 *
 * <p>Unlike {@code BlockDataRegistry}, which is disposed once loading finishes, the family registry
 * lives for the run and is populated on both sides, so this works at any point after registration.</p>
 */
public final class StationFamilies {

    private StationFamilies() {
    }

    /**
     * The rest of {@code parent}'s family - what the shape toggle offers, in wheel order.
     *
     * <p>Empty for anything with no family, which is how a station with the toggle on shows nothing
     * rather than showing the input back to the player.</p>
     */
    public static List<ItemStack> shapesOf(ItemStack parent) {
        if (parent.isEmpty()) {
            return List.of();
        }

        Family<Block> family = familyOf(parent);
        if (family.isAbsent()) {
            return List.of();
        }

        NonNullList<ItemStack> members = NonNullList.create();
        family.addAllItems(family.getGroup(), members);

        List<ItemStack> shapes = new ArrayList<>(members.size());
        for (ItemStack member : members) {
            if (member.isEmpty() || member.getItem() == parent.getItem()) {
                continue;
            }
            shapes.add(new ItemStack(member.getItem(), yieldOf(member.getItem())));
        }
        return List.copyOf(shapes);
    }

    /** Whether {@code shape} is one of the shapes {@link #shapesOf} would offer for {@code parent}. */
    public static boolean isShapeOf(ItemStack parent, Item shape) {
        if (parent.isEmpty() || shape == parent.getItem()) {
            return false;
        }
        for (ItemStack offered : shapesOf(parent)) {
            if (offered.getItem() == shape) {
                return true;
            }
        }
        return false;
    }

    /** The family {@code stack} belongs to, or an absent one if it is not a block or has none. */
    private static Family<Block> familyOf(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return BlockFamily.EMPTY;
        }
        return FamilyRegistry.BLOCKS.getFamily(blockItem.getBlock());
    }

    /**
     * How many of {@code item} one parent block yields.
     *
     * <p>Carried over from the stonecutting recipes this replaced, so the trade is what it always
     * was: eight slabs from a block, four vertical slabs, and so on. Anything whose shape is not
     * recognised is a straight swap.</p>
     */
    public static int yieldOf(Item item) {
        if (!(item instanceof BlockItem blockItem)) {
            return 1;
        }
        return yieldOf(blockItem.getBlock());
    }

    /** @see #yieldOf(Item) */
    public static int yieldOf(Block block) {
        if (block instanceof Layer || block instanceof Slab || block instanceof LayerDirectional) {
            return 8;
        }
        if (block instanceof VerticalSlab || block instanceof VerticalCorner
                || block instanceof VerticalQuarter || block instanceof SlabLessLayers) {
            return 4;
        }
        if (block instanceof VerticalSlabLessLayers || block instanceof VerticalCornerLessLayers
                || block instanceof VerticalQuarterLessLayers || block instanceof SlabQuarter
                || block instanceof Pillar) {
            return 3;
        }
        return 1;
    }
}
