package com.conquestrefabricated.content.entities.painting.render;

import com.conquestrefabricated.content.entities.painting.EntityPainting;
import com.conquestrefabricated.content.entities.painting.art.ArtType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PaintingRenderer extends EntityRenderer<EntityPainting, PaintingRenderer.PaintingRenderState> {

    public PaintingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public PaintingRenderState createRenderState() {
        return new PaintingRenderState();
    }

    @Override
    public void extractRenderState(EntityPainting entity, PaintingRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        ArtType art = entity.getArt();
        state.art = art;
        state.texture = entity.getPaintingType().getRegistryName();
        state.yRot = 180.0F - entity.getPreciseBodyRotation(partialTicks);

        int cellsX = art.sizeX / 16;
        int cellsY = art.sizeY / 16;
        int[][] light = new int[cellsX][cellsY];
        for (int x = 0; x < cellsX; ++x) {
            for (int y = 0; y < cellsY; ++y) {
                float maxY = (-(float) art.sizeY) / 2.0F + (float) ((y + 1) * 16);
                float minY = (-(float) art.sizeY) / 2.0F + (float) (y * 16);
                int lightX = Mth.floor(entity.getX());
                int lightY = Mth.floor(entity.getY() + (double) ((maxY + minY) / 2.0F / 16.0F));
                int lightZ = Mth.floor(entity.getZ());
                light[x][y] = LevelRenderer.getLightCoords(entity.level(), new BlockPos(lightX, lightY, lightZ));
            }
        }
        state.light = light;
    }

    @Override
    public void submit(PaintingRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), state.yRot));
        poseStack.scale(0.0625F, 0.0625F, 0.0625F);

        RenderType renderType = RenderTypes.entityCutout(state.texture);
        ArtType art = state.art;
        submitNodeCollector.submitCustomGeometry(poseStack, renderType,
                (pose, buffer) -> render(pose, buffer, state, art.sizeX, art.sizeY, art.offsetX, art.offsetY));

        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    private void render(PoseStack.Pose pose, VertexConsumer builder, PaintingRenderState state, int width, int height, int textureU, int textureV) {
        Matrix4f position = pose.pose();
        Matrix3f normals = pose.normal();

        float xCenter = (float) (-width) / 2.0F;
        float yCenter = (float) (-height) / 2.0F;
        for (int x = 0; x < width / 16; ++x) {
            for (int y = 0; y < height / 16; ++y) {
                float minX = xCenter + (float) (x * 16);
                float minY = yCenter + (float) (y * 16);
                float maxX = xCenter + (float) ((x + 1) * 16);
                float maxY = yCenter + (float) ((y + 1) * 16);

                int light = state.light[x][y];

                float txMin = (float) (textureU + width - x * 16) / 256.0F;
                float txMax = (float) (textureU + width - (x + 1) * 16) / 256.0F;
                float tyMin = (float) (textureV + height - y * 16) / 256.0F;
                float tyMax = (float) (textureV + height - (y + 1) * 16) / 256.0F;
                vertex(position, normals, builder, maxX, minY, txMax, tyMin, 0.2F, 0, 0, -1, light);
                vertex(position, normals, builder, minX, minY, txMin, tyMin, 0.2F, 0, 0, -1, light);
                vertex(position, normals, builder, minX, maxY, txMin, tyMax, 0.2F, 0, 0, -1, light);
                vertex(position, normals, builder, maxX, maxY, txMax, tyMax, 0.2F, 0, 0, -1, light);

                vertex(position, normals, builder, maxX, maxY, txMax, tyMax, 0.2F, 0, 0, -1, light);
                vertex(position, normals, builder, minX, maxY, txMin, tyMax, 0.2F, 0, 0, -1, light);
                vertex(position, normals, builder, minX, minY, txMin, tyMin, 0.2F, 0, 0, -1, light);
                vertex(position, normals, builder, maxX, minY, txMax, tyMin, 0.2F, 0, 0, -1, light);
            }
        }
    }

    private void vertex(Matrix4f position, Matrix3f normals, VertexConsumer builder, float x, float y, float u, float v, float z, int nx, int ny, int nz, int light) {
        builder.addVertex(position, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal((float) nx, (float) ny, (float) nz);
    }

    public static class PaintingRenderState extends EntityRenderState {
        public ArtType art;
        public Identifier texture;
        public float yRot;
        public int[][] light;
    }
}