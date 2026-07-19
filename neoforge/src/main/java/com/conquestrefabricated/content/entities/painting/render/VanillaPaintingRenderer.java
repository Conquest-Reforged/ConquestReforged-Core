package com.conquestrefabricated.content.entities.painting.render;

import com.conquestrefabricated.core.client.render.type.RenderTypeInjector;
import com.conquestrefabricated.core.client.render.type.ReplaceFirstInjector;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.entity.state.PaintingRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

/**
 * An alternative renderer for vanilla paintings that enables cut-out rendering
 */
public class VanillaPaintingRenderer extends PaintingRenderer {

    private static final Identifier BACK_SPRITE_LOCATION = Identifier.withDefaultNamespace("back");
    private final TextureAtlas paintingsAtlas;

    public VanillaPaintingRenderer(EntityRendererProvider.Context manager) {
        super(manager);
        this.paintingsAtlas = manager.getAtlas(AtlasIds.PAINTINGS);
    }

    @Override
    public void submit(PaintingRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Identifier atlasLocation = paintingsAtlas.getSprite(BACK_SPRITE_LOCATION).atlasLocation();
        RenderTypeInjector injector = new ReplaceFirstInjector(
                submitNodeCollector,
                RenderTypes.entityCutout(atlasLocation)
        );
        super.submit(state, poseStack, injector, camera);
    }
}
