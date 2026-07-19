package com.conquestrefabricated.content.blocks.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class AnimalParticleType extends ParticleType<AnimalParticleData> {

    public AnimalParticleType() {
        super(true);
    }

    @Override
    public MapCodec<AnimalParticleData> codec() {
        return AnimalParticleData.createCodec(this);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AnimalParticleData> streamCodec() {
        return AnimalParticleData.createPacketCodec(this);
    }
}