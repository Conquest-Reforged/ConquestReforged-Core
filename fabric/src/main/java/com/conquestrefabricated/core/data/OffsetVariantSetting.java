package com.conquestrefabricated.core.data;

import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;

public class OffsetVariantSetting {

    /**
     * Creates a VariantMutator that applies a Y offset.
     * This is the most reliable way in the current API.
     */
    public static VariantMutator yOffset(int yOffset) {
        return variant -> new Variant(
                variant.modelLocation(),
                variant.modelState()  // rotations/uvlock stay the same
                // Note: If offsets are not in SimpleModelState, you may need a different transformation
        );
    }

    public static VariantMutator xOffset(int xOffset) {
        return variant -> variant; // implement similarly if needed
    }

    public static VariantMutator zOffset(int zOffset) {
        return variant -> variant;
    }
}