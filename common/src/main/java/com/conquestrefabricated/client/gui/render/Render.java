package com.conquestrefabricated.client.gui.render;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/**
 * @author dags <dags@dags.me>
 */
public class Render {

    private static final int HIDE_MOUSE_MODE = 212995;
    private static final int SHOW_MOUSE_MODE = 212993;

    public static void hideMouse() {
        double mx = Minecraft.getInstance().mouseHandler.xpos();
        double my = Minecraft.getInstance().mouseHandler.ypos();
        InputConstants.grabOrReleaseMouse(Minecraft.getInstance().getWindow(), HIDE_MOUSE_MODE, mx, my);
    }

    public static void showMouse() {
        double mx = Minecraft.getInstance().getWindow().getScreenWidth() / 2D;
        double my = Minecraft.getInstance().getWindow().getScreenHeight() / 2D;
        InputConstants.grabOrReleaseMouse(Minecraft.getInstance().getWindow(), SHOW_MOUSE_MODE, mx, my);
    }

    public static void drawTexture(Identifier texture, GuiGraphicsExtractor drawContext, int left, int top, int width, int height, float u, float v) {
        drawTexture(texture, drawContext, left, top, width, height, u, v, width, height);
    }

    public static void drawTexture(Identifier texture, GuiGraphicsExtractor drawContext, int left, int top, int width, int height, float u, float v, int umax, int vmax) {
        drawContext.blit(RenderPipelines.GUI_TEXTURED, texture, left, top, u, v, width, height, umax, vmax, 0xFFFFFFFF);
    }

//    public static void drawItemStackHighlight(PoseStack poseStack, ItemStack stack, int x, int y, Style style) {
//        drawItemStackHighlight(poseStack, stack, x, y, style.highlightScale, style.highlightColor);
//    }
//
//    public static void drawItemStackHighlight(PoseStack poseStack, ItemStack stack, int x, int y, float scale, int color) {
//        poseStack.pushPose();
//        //RenderSystem.setupOutline();
//        poseStack.scale(scale, scale, 1F);
//        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack);
//        ModelRender.renderModel(poseStack, model, x, y, color);
//        //RenderSystem.teardownOutline();
//        poseStack.popPose();
//    }
//
//    public static void drawBlockModel(PoseStack poseStack, BlockState state, int x, int y, float scale) {
//        poseStack.pushPose();
//        poseStack.translate(x, y, 0);
//        poseStack.scale(scale, scale, 1F);
//        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
//        ModelRender.renderModel(poseStack, state, model, 0, 0, 0x00FFFFFF);
//        poseStack.popPose();
//    }
}
