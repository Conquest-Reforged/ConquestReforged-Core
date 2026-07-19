package com.conquestrefabricated.content.blocks.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;


@Environment(EnvType.CLIENT)
public class AnimalParticleFactory implements ParticleProvider<AnimalParticleData> {

    private final SpriteSet sprites;

    public AnimalParticleFactory(SpriteSet sprite) {
        this.sprites = sprite;
    }


    @Nullable
    @Override
    public Particle createParticle(AnimalParticleData data, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
        TextureAtlasSprite initialSprite = sprites.get(random);
        AnimalParticle newParticle = new AnimalParticle(level, x, y, z, initialSprite);
        newParticle.setSpriteFromAge(sprites);
        return newParticle;
    }
}
