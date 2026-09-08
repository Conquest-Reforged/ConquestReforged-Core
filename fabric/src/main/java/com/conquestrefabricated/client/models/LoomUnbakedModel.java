package com.conquestrefabricated.client.models;

import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import com.conquestrefabricated.content.loom.LoomWeaves;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class LoomUnbakedModel implements BlockStateModel.UnbakedRoot {

    private final Identifier baseModelId;
    private final Identifier extraModelId;
    private final BlockModelRotation modelRotation;
    private final String size;
    private final Vector3f translation;

    public LoomUnbakedModel(Identifier baseModelId, Identifier extraModelId, String size, BlockModelRotation modelRotation) {
        this(baseModelId, extraModelId, size, modelRotation, null);
    }

    public LoomUnbakedModel(Identifier baseModelId, Identifier extraModelId, String size, BlockModelRotation modelRotation, Vector3f translation) {
        this.baseModelId = baseModelId;
        this.extraModelId = extraModelId;
        this.modelRotation = modelRotation;
        this.size = size;
        this.translation = translation;
    }

    @Override
    public void resolveDependencies(Resolver resolver) {
        resolver.markDependency(baseModelId);
        resolver.markDependency(extraModelId);
    }

    @Override
    public BlockStateModel bake(BlockState state, ModelBaker baker) {
        ModelState bakeSettings = modelRotation;
        if (translation != null) {
            bakeSettings = new LoomTranslationBakeSettings(modelRotation, translation);
        }

        BlockStateModel base = SimpleUnbakedExtraModel.blockStateModel(baseModelId, bakeSettings).bake(baker);
        BlockStateModel extra = SimpleUnbakedExtraModel.blockStateModel(extraModelId, bakeSettings).bake(baker);

        TextureAtlasSprite[] sprites = new TextureAtlasSprite[LoomWeaves.TEXTURE_NAMES.length];
        for (int i = 0; i < LoomWeaves.TEXTURE_NAMES.length; i++) {
            Material material = new Material(
                    Identifier.parse("conquest:block/7_tools/3_utility/loom/weaves/" + size + "/" + LoomWeaves.TEXTURE_NAMES[i])
            );
            Material.Baked baked = baker.materials().get(material, () -> "loom_weave_" + size);
            sprites[i] = baked.sprite(); // see note below — accessor name inferred, not confirmed
        }

        return new LoomBakedModel(base, extra, sprites);
    }

    @Override
    public Object visualEqualityGroup(BlockState state) {
        return this;
    }
}