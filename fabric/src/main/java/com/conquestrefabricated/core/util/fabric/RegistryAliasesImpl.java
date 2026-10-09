package com.conquestrefabricated.core.util.fabric;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class RegistryAliasesImpl {
    public static void add(Registry<?> registry, Identifier from, Identifier to) {
        // FabricRegistry, injected into Registry by Fabric API
        registry.addAlias(from, to);
    }
}
