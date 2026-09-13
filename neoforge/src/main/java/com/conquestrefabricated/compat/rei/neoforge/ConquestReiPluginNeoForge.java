package com.conquestrefabricated.compat.rei.neoforge;

import com.conquestrefabricated.compat.rei.ConquestReiPlugin;
import me.shedaniel.rei.forge.REIPluginClient;

/**
 * How NeoForge finds the plugin.
 *
 * <p>Fabric declares its plugins in {@code fabric.mod.json}; here REI scans for this annotation
 * instead, and an annotation cannot be put on the shared class because it lives in a NeoForge-only
 * artifact. So the plugin itself stays in common and this only points at it.</p>
 */
@REIPluginClient
public class ConquestReiPluginNeoForge extends ConquestReiPlugin {
}
