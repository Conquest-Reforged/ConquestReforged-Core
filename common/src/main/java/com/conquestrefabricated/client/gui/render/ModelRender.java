package com.conquestrefabricated.client.gui.render;

public class ModelRender {

    // TODO: This entire class is currently unused — both renderModel(...) overloads
// (item-stack highlight and block-model rendering) are dead code as of the 26.1.2 port;
// no call sites remain active in Render.java. Left commented out rather than ported,
// since the item path relies on vanilla's internal BakedQuad vertex byte layout, which
// has almost certainly changed shape in this version and isn't worth reverse-engineering
// for code nothing calls. Revisit and re-port from the 1.21.1 source if this is needed again.


//    private static final int[] lightmap = {15728880, 15728880, 15728880, 15728880};
//
//    public static void renderModel(PoseStack poseStack, BakedModel model, int x, int y, int color) {
//        renderModel(poseStack, ItemDisplayContext.GUI, model, x, y, color);
//    }
//
//    public static void renderModel(PoseStack poseStack, ItemDisplayContext transform, BakedModel model, int x, int y, int color) {
//        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
//        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//
//        Matrix4fStack modelViewStack  = RenderSystem.getModelViewStack();
//        modelViewStack.pushMatrix();
//        modelViewStack.translate((float) x, (float) y, 100.0F);
//        modelViewStack.translate(8.0F, 8.0F, 0.0F);
//        modelViewStack.scale(1.0F, -1.0F, 1.0F);
//        modelViewStack.scale(16.0F, 16.0F, 16.0F);
//        RenderSystem.applyModelViewMatrix();
//
//
//        PoseStack matrixstack = new PoseStack();
//        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//        model = handleCameraTransforms(matrixstack, model, transform, false);
//
//        boolean flag = !model.usesBlockLight();
//        if (flag) {
//            Lighting.setupForFlatItems();
//        }
//
//        renderModel(matrixstack, RenderType.cutout(), buffer, model, color);
//
//        buffer.endBatch();
//        RenderSystem.enableDepthTest();
//        if (flag) {
//            Lighting.setupFor3DItems();
//        }
//
//        modelViewStack.popMatrix();
//        RenderSystem.applyModelViewMatrix();
//    }
//
//
//    public static void renderModel(PoseStack poseStack, BlockState state, BakedModel model, int x, int y, int color) {
//        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        poseStack.pushPose();
//
//        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
//        modelViewStack.pushMatrix();
//        modelViewStack.translate((float) x, (float) y, 100.0F);
//        modelViewStack.translate(8.0F, 8.0F, 0.0F);
//        modelViewStack.scale(1.0F, -1.0F, 1.0F);
//        modelViewStack.scale(16.0F, 16.0F, 16.0F);
//
//        PoseStack matrix = new PoseStack();
//        matrix.pushPose();
//        matrix.translate(-0.75, 0, 0);
//
//        float f = (float) Math.toRadians(30);
//        float g = (float) Math.toRadians(0);
//        float i = sin(0.5F * f);
//        float j = cos(0.5F * f);
//        float k = sin(0.5F * g);
//        float l = cos(0.5F * g);
//        float m = sin(0.5F * g);
//        float n = cos(0.5F * g);
//
//        matrix.mulPose(new Quaternionf(30, 30, 0, j * l * n - i * k * m));
//        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrix, buffer, 15728880, OverlayTexture.NO_OVERLAY);
//        matrix.popPose();
//
//        buffer.endBatch();
//        RenderSystem.enableDepthTest();
//
//        poseStack.popPose();
//        modelViewStack.popMatrix();
//        RenderSystem.applyModelViewMatrix();
//    }
//
//    private static void renderModel(PoseStack matrix, RenderType rendertype, MultiBufferSource.BufferSource buffer, BakedModel model, int color) {
//        matrix.pushPose();
//        matrix.translate(-0.5D, -0.5D, -0.5D);
//        VertexConsumer builder = getBuffer(buffer, rendertype, true, false);
//        renderModel(model, matrix, builder, color);
//        matrix.popPose();
//    }
//
//    private static void renderModel(BakedModel modelIn, PoseStack matrix, VertexConsumer buffer, int color) {
//        RandomSource random = RandomSource.create();
//        long i = 42L;
//
//        for (Direction direction : Direction.values()) {
//            random.setSeed(42L);
//            renderQuads(matrix, buffer, modelIn.getQuads(null, direction, random), color);
//        }
//
//        random.setSeed(42L);
//        renderQuads(matrix, buffer, modelIn.getQuads(null, null, random), color);
//    }
//
//    private static void renderQuads(PoseStack matrix, VertexConsumer buffer, List<BakedQuad> quads, int color) {
//        float r = (float) (color >> 16 & 255) / 255.0F;
//        float g = (float) (color >> 8 & 255) / 255.0F;
//        float b = (float) (color & 255) / 255.0F;
//
//        PoseStack.Pose entry = matrix.last();
//        for (BakedQuad bakedquad : quads) {
//            render(buffer, bakedquad, entry, r, g, b, 1);
//        }
//    }
//
//    private static void render(VertexConsumer bufferIn, BakedQuad quadIn, PoseStack.Pose entry, float red, float green, float blue, float alpha) {
//        int[] aint = quadIn.getVertices();
//        Vec3i vec3i = quadIn.getDirection().getNormal();
//        Vector3f vector3f = new Vector3f((float) vec3i.getX(), (float) vec3i.getY(), (float) vec3i.getZ());
//        Matrix4f matrix4f = entry.pose();
//        vector3f.mul(entry.normal());
//        int i = 8;
//        int j = aint.length / 8;
//
//        try (MemoryStack memorystack = MemoryStack.stackPush()) {
//            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
//            IntBuffer intbuffer = bytebuffer.asIntBuffer();
//
//            for (int k = 0; k < j; ++k) {
//                intbuffer.clear();
//                intbuffer.put(aint, k * 8, 8);
//                float f = bytebuffer.getFloat(0);
//                float f1 = bytebuffer.getFloat(4);
//                float f2 = bytebuffer.getFloat(8);
//                int lIdx = Math.min(k, lightmap.length - 1);
//                int light = applyBakedLighting(lightmap[lIdx], bytebuffer);
//                float u = bytebuffer.getFloat(16);
//                float v = bytebuffer.getFloat(20);
//                Vector4f vector4f = new Vector4f(f, f1, f2, 1.0F);
//                vector4f.mul(matrix4f);
//                applyBakedNormals(vector3f, bytebuffer, entry.normal());
//
//                // New builder-chain vertex API in 1.21.1
//                bufferIn.addVertex(vector4f.x, vector4f.y, vector4f.z)
//                        .setColor(red, green, blue, alpha)
//                        .setUv(u, v)
//                        .setOverlay(OverlayTexture.NO_OVERLAY)
//                        .setLight(light)
//                        .setNormal(vector3f.x, vector3f.y, vector3f.z);
//            }
//        }
//    }
//
//    private static int applyBakedLighting(int lightmapCoord, ByteBuffer data) {
//        int bl = LightTexture.block(lightmapCoord);
//        int sl = LightTexture.sky(lightmapCoord);
//        int offset = (6) * 4; // int offset for vertex 0 * 4 bytes per int
//        int blBaked = Short.toUnsignedInt(data.getShort(offset)) >> 4;
//        int slBaked = Short.toUnsignedInt(data.getShort(offset + 2)) >> 4;
//        bl = Math.max(bl, blBaked);
//        sl = Math.max(sl, slBaked);
//        return LightTexture.pack(bl, sl);
//    }
//
//    private static void applyBakedNormals(Vector3f generated, ByteBuffer data, Matrix3f normalTransform) {
//        byte nx = data.get(28);
//        byte ny = data.get(29);
//        byte nz = data.get(30);
//        if (nx != 0 || ny != 0 || nz != 0) {
//            generated.set(nx / 127f, ny / 127f, nz / 127f);
//            generated.mul(normalTransform);
//        }
//    }
//
//    public static VertexConsumer getBuffer(MultiBufferSource buffer, RenderType type, boolean isItemIn, boolean dummy) {
//        return buffer.getBuffer(type);
//    }
//
//    private static final Matrix4f flipX;
//    private static final Matrix3f flipXNormal;
//    static {
//        flipX = new Matrix4f().scale(-1,1,1);
//        flipXNormal = new Matrix3f(flipX);
//    }
//
//    public static BakedModel handleCameraTransforms(PoseStack poseStack, BakedModel model, ItemDisplayContext cameraTransformType, boolean leftHandHackery)
//    {
//        PoseStack stack = new PoseStack();
//        model.getTransforms().getTransform(cameraTransformType).apply(true, stack);
//        //model = PerspectiveMapWrapper.handlePerspective(model, PerspectiveMapWrapper.getTransforms(model.getTransformation()), cameraTransformType, stack);
//                //model.handlePerspective(cameraTransformType, stack);
//
//
//        // If the stack is not empty, the code has added a matrix for us to use.
//        if (!stack.clear())
//        {
//            // Apply the transformation to the real matrix stack, flipping for left hand
//            Matrix4f tMat = stack.last().pose();
//            Matrix3f nMat = stack.last().normal();
//            if (leftHandHackery)
//            {
//                flipX.mul(tMat);
//                tMat.mul(flipX);
//                flipXNormal.mul(nMat);
//                nMat.mul(flipXNormal);
//            }
//            poseStack.last().pose().mul(tMat);
//            poseStack.last().normal().mul(nMat);
//        }
//        return model;
//    }

}
