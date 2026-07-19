package com.conquestrefabricated.content.blocks.init.fabric;

import com.conquestrefabricated.content.blocks.init.ParticleRegistrar;
import com.conquestrefabricated.content.blocks.particles.AnimalParticleData;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ParticleRegistrarEvent {
    public static void onIParticleTypeRegistration() {
        registerAnimalParticle(ParticleRegistrar.raven1ParticleType,"raven_1");
        registerAnimalParticle(ParticleRegistrar.raven2ParticleType,"raven_2");
        registerAnimalParticle(ParticleRegistrar.raven3ParticleType,"raven_3");
        registerAnimalParticle(ParticleRegistrar.raven4ParticleType,"raven_4");
        registerAnimalParticle(ParticleRegistrar.ravenFlying1ParticleType,"raven_flying_1");
        registerAnimalParticle(ParticleRegistrar.ravenFlying2ParticleType,"raven_flying_2");
        registerAnimalParticle(ParticleRegistrar.ravenFlying3ParticleType,"raven_flying_3");
        registerAnimalParticle(ParticleRegistrar.ravenFlying4ParticleType,"raven_flying_4");

        registerAnimalParticle(ParticleRegistrar.hawk1ParticleType,"hawk_1");
        registerAnimalParticle(ParticleRegistrar.hawk2ParticleType,"hawk_2");
        registerAnimalParticle(ParticleRegistrar.hawk3ParticleType,"hawk_3");
        registerAnimalParticle(ParticleRegistrar.hawk4ParticleType,"hawk_4");
        registerAnimalParticle(ParticleRegistrar.hawkFlying1ParticleType,"hawk_flying_1");
        registerAnimalParticle(ParticleRegistrar.hawkFlying2ParticleType,"hawk_flying_2");
        registerAnimalParticle(ParticleRegistrar.hawkFlying3ParticleType,"hawk_flying_3");
        registerAnimalParticle(ParticleRegistrar.hawkFlying4ParticleType,"hawk_flying_4");

        registerAnimalParticle(ParticleRegistrar.bat1ParticleType,"bat_1");
        registerAnimalParticle(ParticleRegistrar.bat2ParticleType,"bat_2");
        registerAnimalParticle(ParticleRegistrar.batFlying1ParticleType,"bat_flying_1");
        registerAnimalParticle(ParticleRegistrar.batFlying2ParticleType,"bat_flying_2");
        registerAnimalParticle(ParticleRegistrar.batFlying3ParticleType,"bat_flying_3");
        registerAnimalParticle(ParticleRegistrar.batFlying4ParticleType,"bat_flying_4");

        registerAnimalParticle(ParticleRegistrar.bluejay1ParticleType,"bluejay_1");
        registerAnimalParticle(ParticleRegistrar.bluejay2ParticleType,"bluejay_2");
        registerAnimalParticle(ParticleRegistrar.bluejay3ParticleType,"bluejay_3");
        registerAnimalParticle(ParticleRegistrar.bluejay4ParticleType,"bluejay_4");
        registerAnimalParticle(ParticleRegistrar.bluejayFlying1ParticleType,"bluejay_flying_1");
        registerAnimalParticle(ParticleRegistrar.bluejayFlying2ParticleType,"bluejay_flying_2");
        registerAnimalParticle(ParticleRegistrar.bluejayFlying3ParticleType,"bluejay_flying_3");
        registerAnimalParticle(ParticleRegistrar.bluejayFlying4ParticleType,"bluejay_flying_4");

        registerAnimalParticle(ParticleRegistrar.duck1ParticleType,"duck_1");
        registerAnimalParticle(ParticleRegistrar.duck2ParticleType,"duck_2");
        registerAnimalParticle(ParticleRegistrar.duck3ParticleType,"duck_3");
        registerAnimalParticle(ParticleRegistrar.duck4ParticleType,"duck_4");
        registerAnimalParticle(ParticleRegistrar.duckFlying1ParticleType,"duck_flying_1");
        registerAnimalParticle(ParticleRegistrar.duckFlying2ParticleType,"duck_flying_2");
        registerAnimalParticle(ParticleRegistrar.duckFlying3ParticleType,"duck_flying_3");
        registerAnimalParticle(ParticleRegistrar.duckFlying4ParticleType,"duck_flying_4");

        registerAnimalParticle(ParticleRegistrar.owl1ParticleType,"owl_1");
        registerAnimalParticle(ParticleRegistrar.owl2ParticleType,"owl_2");
        registerAnimalParticle(ParticleRegistrar.owl3ParticleType,"owl_3");
        registerAnimalParticle(ParticleRegistrar.owl4ParticleType,"owl_4");
        registerAnimalParticle(ParticleRegistrar.owlFlying1ParticleType,"owl_flying_1");

        registerAnimalParticle(ParticleRegistrar.pigeon1ParticleType,"pigeon_1");
        registerAnimalParticle(ParticleRegistrar.pigeon2ParticleType,"pigeon_2");
        registerAnimalParticle(ParticleRegistrar.pigeon3ParticleType,"pigeon_3");
        registerAnimalParticle(ParticleRegistrar.pigeon4ParticleType,"pigeon_4");
        registerAnimalParticle(ParticleRegistrar.pigeonFlying1ParticleType,"pigeon_flying_1");
        registerAnimalParticle(ParticleRegistrar.pigeonFlying2ParticleType,"pigeon_flying_2");
        registerAnimalParticle(ParticleRegistrar.pigeonFlying3ParticleType,"pigeon_flying_3");
        registerAnimalParticle(ParticleRegistrar.pigeonFlying4ParticleType,"pigeon_flying_4");

        registerAnimalParticle(ParticleRegistrar.seagull1ParticleType,"seagull_1");
        registerAnimalParticle(ParticleRegistrar.seagull2ParticleType,"seagull_2");
        registerAnimalParticle(ParticleRegistrar.seagull3ParticleType,"seagull_3");
        registerAnimalParticle(ParticleRegistrar.seagull4ParticleType,"seagull_4");
        registerAnimalParticle(ParticleRegistrar.seagullFlying1ParticleType,"seagull_flying_1");
        registerAnimalParticle(ParticleRegistrar.seagullFlying2ParticleType,"seagull_flying_2");
        registerAnimalParticle(ParticleRegistrar.seagullFlying3ParticleType,"seagull_flying_3");
        registerAnimalParticle(ParticleRegistrar.seagullFlying4ParticleType,"seagull_flying_4");

        registerAnimalParticle(ParticleRegistrar.puffin1ParticleType,"puffin_1");
        registerAnimalParticle(ParticleRegistrar.puffinFlying1ParticleType,"puffin_flying_1");

        registerAnimalParticle(ParticleRegistrar.toad1ParticleType,"toad_1");
        registerAnimalParticle(ParticleRegistrar.toad2ParticleType,"toad_2");
        registerAnimalParticle(ParticleRegistrar.toad3ParticleType,"toad_3");
        registerAnimalParticle(ParticleRegistrar.toad4ParticleType,"toad_4");

        registerAnimalParticle(ParticleRegistrar.rat1ParticleType,"rat_1");
        registerAnimalParticle(ParticleRegistrar.rat2ParticleType,"rat_2");
        registerAnimalParticle(ParticleRegistrar.rat3ParticleType,"rat_3");
        registerAnimalParticle(ParticleRegistrar.rat4ParticleType,"rat_4");
    }
    
    public static void registerAnimalParticle (ParticleType<AnimalParticleData> animal, String registryName) {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, "conquest:"+registryName, animal);
    }
}
