package com.conquestrefabricated.content.blocks.util;

import com.conquestrefabricated.content.blocks.block.SlabCorner;
import com.conquestrefabricated.content.blocks.block.SlabEighth;
import com.conquestrefabricated.content.blocks.block.SlabQuarter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

/**
 * Supports a plant can be lowered onto besides {@code Layer}/{@code Slab}. A plant's offset is
 * derived from its {@code LAYERS} state (1-8), so each support maps to the layers value that
 * gives the right drop; {@code offset_toggle} stays false.
 */
public final class PlantSupport {

    /** Bottom-half stairs and corner slabs are half a block high. */
    public static final int HALF_BLOCK_LAYERS = 4;
    /** Bottom-half quarter and eighth slabs; layers 0 doesn't exist, so the lowest value is used. */
    public static final int THIN_SLAB_LAYERS = 1;

    private PlantSupport() {
    }

    /** The layers value for a support handled here, or -1 when the block isn't one of them. */
    public static int specialLayers(BlockState below) {
        Block block = below.getBlock();
        if (block instanceof StairBlock) {
            return below.getValue(StairBlock.HALF) == Half.BOTTOM ? HALF_BLOCK_LAYERS : -1;
        }
        if (block instanceof SlabCorner && below.getValue(TYPE_UPDOWN) == Half.BOTTOM) {
            return HALF_BLOCK_LAYERS;
        }
        if ((block instanceof SlabQuarter || block instanceof SlabEighth) && below.getValue(TYPE_UPDOWN) == Half.BOTTOM) {
            return THIN_SLAB_LAYERS;
        }
        return -1;
    }

    public static boolean isSpecial(BlockState below) {
        return specialLayers(below) >= 0;
    }

    /** The plant's layers value for the block below: the special mapping, else the block's own layers. */
    public static int layers(BlockState below) {
        int special = specialLayers(below);
        return special >= 0 ? special : below.getValue(BlockStateProperties.LAYERS);
    }
}
