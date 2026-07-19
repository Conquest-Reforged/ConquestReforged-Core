package com.conquestrefabricated.content.effects.neoforge;

import com.conquestrefabricated.content.effects.Effects;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = "conquest")
public class EffectsInit {
    @SubscribeEvent
    public static void init(RegisterEvent event) {
        event.register(BuiltInRegistries.MOB_EFFECT.key(), effectRegistryHelper -> {
            Registry.register(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath("conquest", "custom_slowness"), Effects.CUSTOM_SLOWNESS);
        });
    }
}
