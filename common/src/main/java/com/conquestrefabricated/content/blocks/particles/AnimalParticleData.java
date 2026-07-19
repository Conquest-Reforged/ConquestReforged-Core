package com.conquestrefabricated.content.blocks.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.Locale;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class AnimalParticleData implements ParticleOptions {

    private final ParticleType<AnimalParticleData> animalParticleType;

    public AnimalParticleData(ParticleType<AnimalParticleData> animalParticleType) {
        this.animalParticleType = animalParticleType;
    }

    @Override
    public ParticleType<AnimalParticleData> getType() {
        return animalParticleType;
    }

    public static MapCodec<AnimalParticleData> createCodec(ParticleType<AnimalParticleData> type) {
        return MapCodec.unit(new AnimalParticleData(type));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, AnimalParticleData> createPacketCodec(ParticleType<AnimalParticleData> type) {
        return StreamCodec.unit(new AnimalParticleData(type));
    }
}
