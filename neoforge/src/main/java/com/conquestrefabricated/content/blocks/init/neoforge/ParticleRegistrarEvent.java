package com.conquestrefabricated.content.blocks.init.neoforge;

import com.conquestrefabricated.content.blocks.init.ParticleRegistrar;
import com.conquestrefabricated.content.blocks.particles.AnimalParticleData;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "conquest")
public class ParticleRegistrarEvent {
    
    @SubscribeEvent
    public static void onIParticleTypeRegistration(RegisterEvent event) {
        event.register(Registries.PARTICLE_TYPE, particleTypeRegisterHelper -> {
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.raven1ParticleType,"raven_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.raven2ParticleType,"raven_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.raven3ParticleType,"raven_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.raven4ParticleType,"raven_4");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.ravenFlying1ParticleType,"raven_flying_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.ravenFlying2ParticleType,"raven_flying_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.ravenFlying3ParticleType,"raven_flying_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.ravenFlying4ParticleType,"raven_flying_4");

            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.hawk1ParticleType,"hawk_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.hawk2ParticleType,"hawk_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.hawk3ParticleType,"hawk_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.hawk4ParticleType,"hawk_4");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.hawkFlying1ParticleType,"hawk_flying_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.hawkFlying2ParticleType,"hawk_flying_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.hawkFlying3ParticleType,"hawk_flying_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.hawkFlying4ParticleType,"hawk_flying_4");

            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.bat1ParticleType,"bat_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.bat2ParticleType,"bat_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.batFlying1ParticleType,"bat_flying_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.batFlying2ParticleType,"bat_flying_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.batFlying3ParticleType,"bat_flying_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.batFlying4ParticleType,"bat_flying_4");

            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.bluejay1ParticleType,"bluejay_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.bluejay2ParticleType,"bluejay_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.bluejay3ParticleType,"bluejay_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.bluejay4ParticleType,"bluejay_4");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.bluejayFlying1ParticleType,"bluejay_flying_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.bluejayFlying2ParticleType,"bluejay_flying_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.bluejayFlying3ParticleType,"bluejay_flying_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.bluejayFlying4ParticleType,"bluejay_flying_4");

            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.duck1ParticleType,"duck_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.duck2ParticleType,"duck_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.duck3ParticleType,"duck_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.duck4ParticleType,"duck_4");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.duckFlying1ParticleType,"duck_flying_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.duckFlying2ParticleType,"duck_flying_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.duckFlying3ParticleType,"duck_flying_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.duckFlying4ParticleType,"duck_flying_4");

            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.owl1ParticleType,"owl_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.owl2ParticleType,"owl_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.owl3ParticleType,"owl_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.owl4ParticleType,"owl_4");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.owlFlying1ParticleType,"owl_flying_1");

            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.pigeon1ParticleType,"pigeon_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.pigeon2ParticleType,"pigeon_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.pigeon3ParticleType,"pigeon_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.pigeon4ParticleType,"pigeon_4");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.pigeonFlying1ParticleType,"pigeon_flying_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.pigeonFlying2ParticleType,"pigeon_flying_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.pigeonFlying3ParticleType,"pigeon_flying_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.pigeonFlying4ParticleType,"pigeon_flying_4");

            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.seagull1ParticleType,"seagull_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.seagull2ParticleType,"seagull_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.seagull3ParticleType,"seagull_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.seagull4ParticleType,"seagull_4");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.seagullFlying1ParticleType,"seagull_flying_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.seagullFlying2ParticleType,"seagull_flying_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.seagullFlying3ParticleType,"seagull_flying_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.seagullFlying4ParticleType,"seagull_flying_4");

            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.puffin1ParticleType,"puffin_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.puffinFlying1ParticleType,"puffin_flying_1");

            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.toad1ParticleType,"toad_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.toad2ParticleType,"toad_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.toad3ParticleType,"toad_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.toad4ParticleType,"toad_4");

            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.rat1ParticleType,"rat_1");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.rat2ParticleType,"rat_2");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.rat3ParticleType,"rat_3");
            registerAnimalParticle(particleTypeRegisterHelper, ParticleRegistrar.rat4ParticleType,"rat_4");
        });
    }

    public static void registerAnimalParticle (RegisterEvent.RegisterHelper<ParticleType<?>> particleTypeRegisterHelper, ParticleType<AnimalParticleData> animal, String registryName) {
        particleTypeRegisterHelper.register(Identifier.fromNamespaceAndPath("conquest", registryName), animal);

    }
}
