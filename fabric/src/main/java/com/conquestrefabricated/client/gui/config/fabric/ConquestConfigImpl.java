package com.conquestrefabricated.client.gui.config.fabric;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import net.fabricmc.loader.api.FabricLoader;

public class ConquestConfigImpl {
    public static ConquestConfig init() {
        return new ConquestConfig(FabricLoader.getInstance().getConfigDir().resolve("conquest.json").toFile());
    }
}
