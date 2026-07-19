package com.conquestrefabricated.client.models.obj;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import org.jspecify.annotations.Nullable;

/**
 * Adapted from NeoForged/Create (LGPL-2.1-only): base interface for unbaked models
 * that support a ContextMap-aware bake overload (root transform / part visibility).
 */
@FunctionalInterface
public interface ExtendedUnbakedGeometry extends UnbakedGeometry {
    ContextMap EMPTY = new ContextMap.Builder().create(ModProperties.EMPTY_TYPE);

    @Override
    default QuadCollection bake(TextureSlots slots, ModelBaker baker, ModelState state, ModelDebugName name) {
        return bake(slots, baker, state, name, EMPTY);
    }

    QuadCollection bake(TextureSlots slots, ModelBaker baker, ModelState state, ModelDebugName name, ContextMap additionalProperties);
}