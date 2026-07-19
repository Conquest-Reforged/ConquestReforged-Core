package com.conquestrefabricated.client.models;

import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperUnbakedRootBlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.state.BlockState;

public class DuplicateDownUnbakedModel extends WrapperUnbakedRootBlockStateModel {

    public DuplicateDownUnbakedModel(BlockStateModel.UnbakedRoot baseModel) {
        super(baseModel);
    }

    @Override
    public BlockStateModel bake(BlockState state, ModelBaker baker) {
        return new DuplicateDownBakedModel(wrapped.bake(state, baker));
    }
}