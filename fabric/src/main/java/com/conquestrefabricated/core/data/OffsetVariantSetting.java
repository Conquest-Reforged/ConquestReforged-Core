package com.conquestrefabricated.core.data;

import com.conquestrefabricated.core.data.offset.ModelStateOffsets;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;

/**
 * Polytone's per-variant model offsets, in 16x16 pixels.
 *
 * <p>The 1.20.1 equivalent was a {@code VariantSetting} because {@code BlockStateVariant} was an
 * open map. Since the 1.21.6 model rework a variant is a record with a fixed codec, so {@code SimpleModelStateMixin}
 * delivers the offsets instead, only applying during datagen.</p>
 */
public class OffsetVariantSetting {

    private OffsetVariantSetting() {
    }

    public static VariantMutator xOffset(float xOffset) {
        return offset(Direction.Axis.X, xOffset);
    }

    public static VariantMutator yOffset(float yOffset) {
        return offset(Direction.Axis.Y, yOffset);
    }

    public static VariantMutator zOffset(float zOffset) {
        return offset(Direction.Axis.Z, zOffset);
    }

    private static VariantMutator offset(Direction.Axis axis, float value) {
        return variant -> {
            Variant.SimpleModelState current = variant.modelState();
            ModelStateOffsets currentOffsets = ModelStateOffsets.of(current);

            float x = currentOffsets.conquest$getXOffset();
            float y = currentOffsets.conquest$getYOffset();
            float z = currentOffsets.conquest$getZOffset();
            switch (axis) {
                case X -> x = value;
                case Y -> y = value;
                case Z -> z = value;
            }

            //A fresh state every time: plainVariant() hands out the shared SimpleModelState.DEFAULT,
            //so writing offsets in place would leak onto every other variant in the run
            Variant.SimpleModelState copy = new Variant.SimpleModelState(
                    current.x(), current.y(), current.z(), current.uvLock());
            ModelStateOffsets.of(copy).conquest$setOffsets(x, y, z);

            return variant.withState(copy);
        };
    }
}
