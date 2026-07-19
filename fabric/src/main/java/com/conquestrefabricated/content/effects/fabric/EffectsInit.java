package com.conquestrefabricated.content.effects.fabric;

import com.conquestrefabricated.content.effects.Effects;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class EffectsInit {
    public static void init() {
        Registry.register(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath("conquest", "custom_slowness"), Effects.CUSTOM_SLOWNESS);
    }
}
