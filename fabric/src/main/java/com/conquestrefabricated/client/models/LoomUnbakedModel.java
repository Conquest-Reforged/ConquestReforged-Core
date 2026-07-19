package com.conquestrefabricated.client.models;

import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
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

    private static final String[] TEXTURE_NAMES = {
            "loom_weave_white", "loom_weave_red", "loom_weave_black", "loom_weave_gray",
            "loom_weave_light_gray", "loom_weave_white", "loom_weave_brown", "loom_weave_yellow",
            "loom_weave_orange", "loom_weave_pink", "loom_weave_magenta", "loom_weave_purple",
            "loom_weave_blue", "loom_weave_light_blue", "loom_weave_cyan", "loom_weave_green",
            "loom_weave_lime", "loom_weave_baotao", "loom_weave_berber", "loom_weave_black_persian",
            "loom_weave_blue_nain", "loom_weave_brown_oriental", "loom_weave_celtic_knot",
            "loom_weave_kashmiri", "loom_weave_kazakh", "loom_weave_kilim", "loom_weave_nahavand",
            "loom_weave_red_and_blue_sarouk", "loom_weave_red_oriental", "loom_weave_red_pazyryk",
            "loom_weave_shirishabad", "loom_weave_william_morris", "loom_weave_yellow_red_persian"
    };

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

        TextureAtlasSprite[] sprites = new TextureAtlasSprite[TEXTURE_NAMES.length];
        for (int i = 0; i < TEXTURE_NAMES.length; i++) {
            Material material = new Material(
                    Identifier.parse("conquest:block/7_tools/3_utility/loom/weaves/" + size + "/" + TEXTURE_NAMES[i])
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