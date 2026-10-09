package com.conquestrefabricated.core.util.neoforge;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class RegistryAliasesImpl {
    public static void add(Registry<?> registry, Identifier from, Identifier to) {
        // IRegistryExtension, patched into Registry by NeoForge
        registry.addAlias(from, to);
    }
}
