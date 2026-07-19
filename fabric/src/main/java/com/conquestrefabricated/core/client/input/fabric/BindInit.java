package com.conquestrefabricated.core.client.input.fabric;

import com.conquestrefabricated.client.ModBinds;
import com.conquestrefabricated.client.bind.PaintingBindListener;
import com.conquestrefabricated.client.bind.PaletteBindListener;
import com.conquestrefabricated.core.asset.lang.Translations;
import com.conquestrefabricated.core.util.log.Log;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BindInit {
    public static void init() {
        Log.info("Registering keybinds");
        String category = "key.category.conquest";
        Translations.getInstance().add(category, "Conquest Reforged");
        ModBinds.palette = Bindings.create("key.palette.title", "key.keyboard.v", category)
                .addListener(new PaletteBindListener())
                .addListener(new PaintingBindListener());
    }

}
