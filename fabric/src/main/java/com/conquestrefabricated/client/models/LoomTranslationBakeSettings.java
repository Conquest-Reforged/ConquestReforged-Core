package com.conquestrefabricated.client.models;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import org.joml.Vector3f;

public class LoomTranslationBakeSettings implements ModelState {
    private final Transformation transformation;

    public LoomTranslationBakeSettings(ModelState parent, Vector3f translation) {
        Transformation ownTransform = new Transformation(translation, null, null, null);
        this.transformation = parent.transformation().compose(ownTransform);
    }

    public LoomTranslationBakeSettings(ModelState parent, float x, float y, float z) {
        this(parent, new Vector3f(x, y, z));
    }

    @Override
    public Transformation transformation() {
        return this.transformation;
    }
}