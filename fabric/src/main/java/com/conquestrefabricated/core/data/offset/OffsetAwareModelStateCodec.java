package com.conquestrefabricated.core.data.offset;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.client.renderer.block.dispatch.Variant;

import java.util.stream.Stream;

/**
 * Wraps vanilla's {@code SimpleModelState} map codec so that encoding also writes Polytone's
 * offset keys. Installed by {@code SimpleModelStateMixin} during datagen only.
 */
public class OffsetAwareModelStateCodec extends MapCodec<Variant.SimpleModelState> {

    public static final String X_KEY = "xoffset";
    public static final String Y_KEY = "yoffset";
    public static final String Z_KEY = "zoffset";

    private final MapCodec<Variant.SimpleModelState> delegate;

    public OffsetAwareModelStateCodec(MapCodec<Variant.SimpleModelState> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.concat(
                delegate.keys(ops),
                Stream.of(ops.createString(X_KEY), ops.createString(Y_KEY), ops.createString(Z_KEY))
        );
    }

    @Override
    public <T> DataResult<Variant.SimpleModelState> decode(DynamicOps<T> ops, MapLike<T> input) {
        //Datagen only ever encodes. At runtime Polytone owns the decode side, and vanilla ignores
        //unknown keys, so there is nothing to do here.
        return delegate.decode(ops, input);
    }

    @Override
    public <T> RecordBuilder<T> encode(Variant.SimpleModelState input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        RecordBuilder<T> builder = delegate.encode(input, ops, prefix);
        ModelStateOffsets offsets = ModelStateOffsets.of(input);
        builder = addOffset(builder, ops, X_KEY, offsets.conquest$getXOffset());
        builder = addOffset(builder, ops, Y_KEY, offsets.conquest$getYOffset());
        builder = addOffset(builder, ops, Z_KEY, offsets.conquest$getZOffset());
        return builder;
    }

    private static <T> RecordBuilder<T> addOffset(RecordBuilder<T> builder, DynamicOps<T> ops, String key, float value) {
        if (value == 0.0F) {
            return builder;
        }
        //Whole pixel counts stay numbers/integers so the output matches the 1.20.1 blockstate files
        T encoded = value == Math.rint(value) ? ops.createInt((int) value) : ops.createFloat(value);
        return builder.add(key, encoded);
    }
}
