package com.conquestrefabricated.core.data.offset;

/**
 * Implemented on {@code Variant.SimpleModelState} by {@code SimpleModelStateMixin} so that a
 * variant can carry Polytone's {@code xoffset}/{@code yoffset}/{@code zoffset} through datagen.
 *
 * <p>Vanilla's {@code SimpleModelState} is a record whose codec only knows {@code x}, {@code y},
 * {@code z} and {@code uvlock}, so there is nowhere to put these values without adding fields to
 * the record itself. Offsets are in 16x16 pixels to align with Polytone's system.
 * ({@code Matrix4f.translate(x / 16f, y / 16f, z / 16f)}).</p>
 */
public interface ModelStateOffsets {

    float conquest$getXOffset();

    float conquest$getYOffset();

    float conquest$getZOffset();

    void conquest$setOffsets(float xOffset, float yOffset, float zOffset);

    /** Copies the offsets of {@code from} onto this state. */
    default void conquest$copyOffsetsFrom(ModelStateOffsets from) {
        conquest$setOffsets(from.conquest$getXOffset(), from.conquest$getYOffset(), from.conquest$getZOffset());
    }

    default boolean conquest$hasOffsets() {
        return conquest$getXOffset() != 0.0F || conquest$getYOffset() != 0.0F || conquest$getZOffset() != 0.0F;
    }

    /** Convenience cast; the mixin makes every {@code SimpleModelState} implement this interface. */
    static ModelStateOffsets of(Object modelState) {
        return (ModelStateOffsets) modelState;
    }
}
