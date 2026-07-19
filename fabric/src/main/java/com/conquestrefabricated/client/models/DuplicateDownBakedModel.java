package com.conquestrefabricated.client.models;

import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class DuplicateDownBakedModel extends WrapperBlockStateModel {

    private static final float DOWN_TRANSLATION = -1.0f;
    private static final float BASE_SCALE_FACTOR = 0.001f;

    public DuplicateDownBakedModel(BlockStateModel baseModel) {
        super(baseModel);
    }

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state,
                          RandomSource random, Predicate<@Nullable Direction> cullTest) {
        wrapped.emitQuads(emitter, level, pos, state, random, cullTest);

        emitter.pushTransform(quad -> {
            if (quad.nominalFace() == Direction.DOWN) return false;
            translateDown(quad);
            quad.cullFace(null);
            quad.nominalFace(quad.nominalFace());
            return true;
        });

        wrapped.emitQuads(emitter, level, pos, state, random, cullTest);
        emitter.popTransform();
    }

    /**
     * Translates and scales the quad downward with a slight outward scale based on its face.
     */
    public void translateDown(MutableQuadView quad) {
        final float scaleFactor = BASE_SCALE_FACTOR;
        final float scaleFactor2 = BASE_SCALE_FACTOR; //Increase scale slightly to avoid z-fighting

        // Store vertex positions in arrays to avoid repetitive calls
        float[] xs = new float[4];
        float[] ys = new float[4];
        float[] zs = new float[4];
        for (int i = 0; i < 4; i++) {
            xs[i] = quad.x(i);
            ys[i] = quad.y(i);
            zs[i] = quad.z(i);
        }

        // Adjust vertices 1 and 2 (typically the “bottom” ones) based on the face direction
        Direction face = quad.nominalFace();
        switch (face) {
            case UP:
                xs[3] += scaleFactor2;
                zs[3] -= scaleFactor2;
                xs[2] += scaleFactor;
                zs[2] += scaleFactor;
                xs[1] -= scaleFactor2;
                zs[1] += scaleFactor2;
                xs[0] -= scaleFactor;
                zs[0] -= scaleFactor;
                break;
            case NORTH:
                xs[0] += scaleFactor2;
                zs[0] -= scaleFactor2;
                xs[1] += scaleFactor;
                zs[1] -= scaleFactor;
                xs[2] -= scaleFactor2;
                zs[2] -= scaleFactor2;
                xs[3] -= scaleFactor;
                zs[3] -= scaleFactor;
                break;
            case SOUTH:
                xs[0] -= scaleFactor2;
                zs[0] += scaleFactor2;
                xs[1] -= scaleFactor;
                zs[1] += scaleFactor;
                xs[2] += scaleFactor2;
                zs[2] += scaleFactor2;
                xs[3] += scaleFactor;
                zs[3] += scaleFactor;
                break;
            case EAST:
                xs[0] += scaleFactor;
                zs[0] += scaleFactor;
                xs[1] += scaleFactor2;
                zs[1] += scaleFactor2;
                xs[2] += scaleFactor;
                zs[2] -= scaleFactor;
                xs[3] += scaleFactor2;
                zs[3] -= scaleFactor2;
                break;
            case WEST:
                xs[0] -= scaleFactor;
                zs[0] -= scaleFactor;
                xs[1] -= scaleFactor2;
                zs[1] -= scaleFactor2;
                xs[2] -= scaleFactor;
                zs[2] += scaleFactor;
                xs[3] -= scaleFactor2;
                zs[3] += scaleFactor2;
                break;
            default:
                break;
        }

        // Apply the downward translation to all vertices and update their positions
        for (int i = 0; i < 4; i++) {
            quad.pos(i, xs[i], ys[i] + DOWN_TRANSLATION, zs[i]);
        }

        // Preserve the nominal face to maintain correct shading?
        quad.nominalFace(face);
    }
}

