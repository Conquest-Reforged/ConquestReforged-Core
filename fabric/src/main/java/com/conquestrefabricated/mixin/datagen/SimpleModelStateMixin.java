package com.conquestrefabricated.mixin.datagen;

import com.conquestrefabricated.core.data.offset.ModelStateOffsets;
import com.conquestrefabricated.core.data.offset.OffsetAwareModelStateCodec;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.Variant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

/**
 * Gives {@code Variant.SimpleModelState} the three Polytone offset fields and teaches its codec to
 * write them. Applied during datagen only - see {@code DatagenMixinPlugin} - because at runtime
 * Polytone wraps this very same codec itself.
 */
@Mixin(Variant.SimpleModelState.class)
public abstract class SimpleModelStateMixin implements ModelStateOffsets {

    @Unique
    private float conquest$xOffset;

    @Unique
    private float conquest$yOffset;

    @Unique
    private float conquest$zOffset;

    @Override
    public float conquest$getXOffset() {
        return this.conquest$xOffset;
    }

    @Override
    public float conquest$getYOffset() {
        return this.conquest$yOffset;
    }

    @Override
    public float conquest$getZOffset() {
        return this.conquest$zOffset;
    }

    @Override
    public void conquest$setOffsets(float xOffset, float yOffset, float zOffset) {
        this.conquest$xOffset = xOffset;
        this.conquest$yOffset = yOffset;
        this.conquest$zOffset = zOffset;
    }

    @WrapOperation(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;mapCodec(Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"
            )
    )
    private static MapCodec<Variant.SimpleModelState> conquest$wrapCodec(
            Function<RecordCodecBuilder.Instance<Variant.SimpleModelState>, ? extends App<RecordCodecBuilder.Mu<Variant.SimpleModelState>, Variant.SimpleModelState>> builder,
            Operation<MapCodec<Variant.SimpleModelState>> original) {
        return new OffsetAwareModelStateCodec(original.call(builder));
    }

    /**
     * The record's wither methods rebuild the state from its four canonical components, which would
     * drop the offsets. Rotation mutators run after the offset mutator in every generator that uses
     * both, so without this the offset would survive only on the un-rotated facing.
     */
    @ModifyReturnValue(method = {"withX", "withY", "withZ", "withUvLock"}, at = @At("RETURN"))
    private Variant.SimpleModelState conquest$carryOffsets(Variant.SimpleModelState result) {
        ModelStateOffsets.of(result).conquest$copyOffsetsFrom(this);
        return result;
    }
}
