package com.conquestrefabricated.client.gui.config.neoforge;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import net.neoforged.fml.loading.FMLPaths;

public class ConquestConfigImpl {
    public static ConquestConfig init() {
        return new ConquestConfig(FMLPaths.CONFIGDIR.get().resolve("conquest.json").toFile());
    }
}
