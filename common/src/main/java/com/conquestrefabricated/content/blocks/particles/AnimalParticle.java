package com.conquestrefabricated.content.blocks.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class AnimalParticle extends SingleQuadParticle {

    public AnimalParticle(ClientLevel world, double x, double y, double z, TextureAtlasSprite sprite) {
        super(world, x, y, z, sprite);
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.lifetime = 80;
    }

    @Override
    public float getQuadSize(float tickDelta) {
        return 0.5F;
    }

    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }
}