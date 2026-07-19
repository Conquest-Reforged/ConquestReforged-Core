package com.conquestrefabricated.client.models.obj;

import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import net.minecraft.client.resources.model.UnbakedModel;

public abstract class AbstractUnbakedModel implements UnbakedModel {
    protected final StandardModelParameters parameters;

    protected AbstractUnbakedModel(StandardModelParameters parameters) {
        this.parameters = parameters;
    }

    @Override public @Nullable Boolean ambientOcclusion() { return parameters.ambientOcclusion(); }
    @Override public @Nullable GuiLight guiLight() { return parameters.guiLight(); }
    @Override public @Nullable ItemTransforms transforms() { return parameters.itemTransforms(); }
    @Override public TextureSlots.Data textureSlots() { return parameters.textures(); }
    @Override public @Nullable Identifier parent() { return parameters.parent(); }
}