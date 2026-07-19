package com.conquestrefabricated.content.blocks.tileentity.enchantment;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.EnchantTableRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;


@Environment(EnvType.CLIENT)
public class ModdedTileEntityEnchanterRenderer implements BlockEntityRenderer<ModdedEnchantingTableTileEntity, EnchantTableRenderState> {

    public static final SpriteId BOOK_TEXTURE = Sheets.BLOCK_ENTITIES_MAPPER.defaultNamespaceApply("enchantment/enchanting_table_book");
    private final SpriteGetter sprites;
    private final net.minecraft.client.model.object.book.BookModel bookModel;

    public ModdedTileEntityEnchanterRenderer(final BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
        this.bookModel = new net.minecraft.client.model.object.book.BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    public EnchantTableRenderState createRenderState() {
        return new EnchantTableRenderState();
    }

    @Override
    public void extractRenderState(ModdedEnchantingTableTileEntity blockEntity, EnchantTableRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.flip = Mth.lerp(partialTicks, blockEntity.oFlip, blockEntity.flip);
        state.open = Mth.lerp(partialTicks, blockEntity.oOpen, blockEntity.open);
        state.time = (float)blockEntity.time + partialTicks;

        float or;
        for(or = blockEntity.rot - blockEntity.oRot; or >= (float)Math.PI; or -= ((float)Math.PI * 2F)) {
        }

        while(or < -(float)Math.PI) {
            or += ((float)Math.PI * 2F);
        }

        state.yRot = blockEntity.oRot + or * partialTicks;
    }

    public void submit(final EnchantTableRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.75F, 0.5F);
        poseStack.translate(0.0F, 0.1F + Mth.sin((double)(state.time * 0.1F)) * 0.01F, 0.0F);
        float yRot = state.yRot;
        poseStack.mulPose(Axis.YP.rotation(-yRot));
        poseStack.mulPose(Axis.ZP.rotationDegrees(80.0F));
        float ff1 = Mth.frac(state.flip + 0.25F) * 1.6F - 0.3F;
        float ff2 = Mth.frac(state.flip + 0.75F) * 1.6F - 0.3F;
        net.minecraft.client.model.object.book.BookModel.State bookState = net.minecraft.client.model.object.book.BookModel.State.forAnimation(state.time, Mth.clamp(ff1, 0.0F, 1.0F), Mth.clamp(ff2, 0.0F, 1.0F), state.open);
        submitNodeCollector.submitModel(this.bookModel, bookState, poseStack, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, BOOK_TEXTURE, this.sprites, 0, state.breakProgress);
        poseStack.popPose();
    }

}