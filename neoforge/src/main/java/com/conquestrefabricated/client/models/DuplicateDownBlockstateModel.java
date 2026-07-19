package com.conquestrefabricated.client.models;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class DuplicateDownBlockstateModel implements BlockStateModel {

    private static final float DOWN_TRANSLATION = -1.0f;
    private static final float SCALE_FACTOR = 0.001f;

    private final BlockStateModel wrapped;

    public DuplicateDownBlockstateModel(BlockStateModel wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
        List<BlockStateModelPart> originalParts = new ArrayList<>();
        wrapped.collectParts(random, originalParts);
        for (BlockStateModelPart part : originalParts) {
            parts.add(new DuplicateDownPart(part));
        }
    }

    @Override
    public Material.Baked particleMaterial() {
        return wrapped.particleMaterial();
    }

    @Override
    public int materialFlags() {
        return wrapped.materialFlags();
    }

    private static class DuplicateDownPart implements BlockStateModelPart {
        private final BlockStateModelPart original;
        private @Nullable List<BakedQuad> cachedUnculledQuads;

        DuplicateDownPart(BlockStateModelPart original) {
            this.original = original;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable Direction cullFace) {
            if (cullFace != null) {
                return original.getQuads(cullFace);
            }
            if (cachedUnculledQuads == null) {
                List<BakedQuad> result = new ArrayList<>(original.getQuads(null));
                for (Direction dir : Direction.values()) {
                    for (BakedQuad quad : original.getQuads(dir)) {
                        if (quad.direction() != Direction.DOWN) {
                            result.add(translateDown(quad));
                        }
                    }
                }
                for (BakedQuad quad : original.getQuads(null)) {
                    if (quad.direction() != Direction.DOWN) {
                        result.add(translateDown(quad));
                    }
                }
                cachedUnculledQuads = result;
            }
            return cachedUnculledQuads;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return original.useAmbientOcclusion();
        }

        @Override
        public Material.Baked particleMaterial() {
            return original.particleMaterial();
        }

        @Override
        public int materialFlags() {
            return original.materialFlags();
        }

        private static BakedQuad translateDown(BakedQuad quad) {
            Vector3f[] pos = {
                    new Vector3f(quad.position0()),
                    new Vector3f(quad.position1()),
                    new Vector3f(quad.position2()),
                    new Vector3f(quad.position3())
            };

            Direction face = quad.direction();
            if (face != null) {
                switch (face) {
                    case UP -> {
                        pos[3].x += SCALE_FACTOR; pos[3].z -= SCALE_FACTOR;
                        pos[2].x += SCALE_FACTOR; pos[2].z += SCALE_FACTOR;
                        pos[1].x -= SCALE_FACTOR; pos[1].z += SCALE_FACTOR;
                        pos[0].x -= SCALE_FACTOR; pos[0].z -= SCALE_FACTOR;
                    }
                    case NORTH -> {
                        pos[0].x += SCALE_FACTOR; pos[0].z -= SCALE_FACTOR;
                        pos[1].x += SCALE_FACTOR; pos[1].z -= SCALE_FACTOR;
                        pos[2].x -= SCALE_FACTOR; pos[2].z -= SCALE_FACTOR;
                        pos[3].x -= SCALE_FACTOR; pos[3].z -= SCALE_FACTOR;
                    }
                    case SOUTH -> {
                        pos[0].x -= SCALE_FACTOR; pos[0].z += SCALE_FACTOR;
                        pos[1].x -= SCALE_FACTOR; pos[1].z += SCALE_FACTOR;
                        pos[2].x += SCALE_FACTOR; pos[2].z += SCALE_FACTOR;
                        pos[3].x += SCALE_FACTOR; pos[3].z += SCALE_FACTOR;
                    }
                    case EAST -> {
                        pos[0].x += SCALE_FACTOR; pos[0].z += SCALE_FACTOR;
                        pos[1].x += SCALE_FACTOR; pos[1].z += SCALE_FACTOR;
                        pos[2].x += SCALE_FACTOR; pos[2].z -= SCALE_FACTOR;
                        pos[3].x += SCALE_FACTOR; pos[3].z -= SCALE_FACTOR;
                    }
                    case WEST -> {
                        pos[0].x -= SCALE_FACTOR; pos[0].z -= SCALE_FACTOR;
                        pos[1].x -= SCALE_FACTOR; pos[1].z -= SCALE_FACTOR;
                        pos[2].x -= SCALE_FACTOR; pos[2].z += SCALE_FACTOR;
                        pos[3].x -= SCALE_FACTOR; pos[3].z += SCALE_FACTOR;
                    }
                    default -> {}
                }
            }

            for (Vector3f v : pos) {
                v.y += DOWN_TRANSLATION;
            }

            return new BakedQuad(
                    pos[0], pos[1], pos[2], pos[3],
                    quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                    quad.direction(),
                    quad.materialInfo()
            );
        }
    }
}
