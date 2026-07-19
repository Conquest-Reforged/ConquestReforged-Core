package com.conquestrefabricated.client.models;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RotatedPart implements BlockStateModelPart {
    private final BlockStateModelPart original;
    private final Direction facing;

    RotatedPart(BlockStateModelPart original, Direction facing) {
        this.original = original;
        this.facing = facing;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction cullFace) {
        List<BakedQuad> quads = original.getQuads(rotateCullFace(cullFace));
        List<BakedQuad> result = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            result.add(rotateQuad(quad));
        }
        return result;
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

    private @Nullable Direction rotateCullFace(@Nullable Direction cullFace) {
        if (cullFace == null || cullFace.getAxis() == Direction.Axis.Y) {
            return cullFace;
        }
        int steps = facing == Direction.EAST ? 1 : facing == Direction.SOUTH ? 2 : facing == Direction.WEST ? 3 : 0;
        Direction result = cullFace;
        for (int i = 0; i < steps; i++) {
            result = result.getClockWise(Direction.Axis.Y);
        }
        return result;
    }

    private BakedQuad rotateQuad(BakedQuad quad) {
        int steps = facing == Direction.EAST ? 1 : facing == Direction.SOUTH ? 2 : facing == Direction.WEST ? 3 : 0;
        if (steps == 0) {
            return quad;
        }

        Vector3f[] pos = {
                new Vector3f(quad.position0()),
                new Vector3f(quad.position1()),
                new Vector3f(quad.position2()),
                new Vector3f(quad.position3())
        };

        for (Vector3f v : pos) {
            rotateAroundY(v, steps);
        }

        Direction newDirection = quad.direction();
        if (newDirection != null && newDirection.getAxis() != Direction.Axis.Y) {
            for (int i = 0; i < steps; i++) {
                newDirection = newDirection.getClockWise(Direction.Axis.Y);
            }
        }

        return new BakedQuad(
                pos[0], pos[1], pos[2], pos[3],
                quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                newDirection,
                quad.materialInfo()
        );
    }

    private static void rotateAroundY(Vector3f v, int steps) {
        for (int i = 0; i < steps; i++) {
            float x = v.x - 0.5f;
            float z = v.z - 0.5f;
            v.x = 0.5f - z;
            v.z = 0.5f + x;
        }
    }
}