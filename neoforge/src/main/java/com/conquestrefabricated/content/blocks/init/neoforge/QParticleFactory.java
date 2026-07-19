package com.conquestrefabricated.content.blocks.init.neoforge;

import com.conquestrefabricated.content.blocks.init.ParticleRegistrar;
import com.conquestrefabricated.content.blocks.particles.AnimalParticleFactory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;


@EventBusSubscriber(value = Dist.CLIENT, modid = "conquest")
public class QParticleFactory {
    @SubscribeEvent
    public static void onParticleFactoryRegistration(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistrar.raven1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.raven2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.raven3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.raven4ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.ravenFlying1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.ravenFlying2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.ravenFlying3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.ravenFlying4ParticleType, sprite -> new AnimalParticleFactory(sprite));

        event.registerSpriteSet(ParticleRegistrar.hawk1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.hawk2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.hawk3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.hawk4ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.hawkFlying1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.hawkFlying2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.hawkFlying3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.hawkFlying4ParticleType, sprite -> new AnimalParticleFactory(sprite));

        event.registerSpriteSet(ParticleRegistrar.bat1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.bat2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.batFlying1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.batFlying2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.batFlying3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.batFlying4ParticleType, sprite -> new AnimalParticleFactory(sprite));

        event.registerSpriteSet(ParticleRegistrar.bluejay1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.bluejay2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.bluejay3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.bluejay4ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.bluejayFlying1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.bluejayFlying2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.bluejayFlying3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.bluejayFlying4ParticleType, sprite -> new AnimalParticleFactory(sprite));

        event.registerSpriteSet(ParticleRegistrar.duck1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.duck2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.duck3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.duck4ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.duckFlying1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.duckFlying2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.duckFlying3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.duckFlying4ParticleType, sprite -> new AnimalParticleFactory(sprite));

        event.registerSpriteSet(ParticleRegistrar.owl1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.owl2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.owl3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.owl4ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.owlFlying1ParticleType, sprite -> new AnimalParticleFactory(sprite));

        event.registerSpriteSet(ParticleRegistrar.pigeon1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.pigeon2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.pigeon3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.pigeon4ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.pigeonFlying1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.pigeonFlying2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.pigeonFlying3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.pigeonFlying4ParticleType, sprite -> new AnimalParticleFactory(sprite));

        event.registerSpriteSet(ParticleRegistrar.puffin1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.puffinFlying1ParticleType, sprite -> new AnimalParticleFactory(sprite));

        event.registerSpriteSet(ParticleRegistrar.seagull1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.seagull2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.seagull3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.seagull4ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.seagullFlying1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.seagullFlying2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.seagullFlying3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.seagullFlying4ParticleType, sprite -> new AnimalParticleFactory(sprite));

        event.registerSpriteSet(ParticleRegistrar.toad1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.toad2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.toad3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.toad4ParticleType, sprite -> new AnimalParticleFactory(sprite));

        event.registerSpriteSet(ParticleRegistrar.rat1ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.rat2ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.rat3ParticleType, sprite -> new AnimalParticleFactory(sprite));
        event.registerSpriteSet(ParticleRegistrar.rat4ParticleType, sprite -> new AnimalParticleFactory(sprite));
    }
}
