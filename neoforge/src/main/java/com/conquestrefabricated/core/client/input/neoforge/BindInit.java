package com.conquestrefabricated.core.client.input.neoforge;

import com.conquestrefabricated.client.ModBinds;
import com.conquestrefabricated.client.bind.PaintingBindListener;
import com.conquestrefabricated.client.bind.PaletteBindListener;
import com.conquestrefabricated.core.asset.lang.Translations;
import com.conquestrefabricated.core.util.log.Log;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;


@EventBusSubscriber(value = Dist.CLIENT)
public class BindInit {

    @SubscribeEvent
    public static void init(RegisterKeyMappingsEvent event) {
        Log.info("Registering keybinds");
        String category = "key.category.conquest";
        Translations.getInstance().add(category, "Conquest Reforged");
        //event.register(Bindings.create("key.search.title", "key.keyboard.unknown", category, new SearchBindListener()));
        ModBinds.palette = Bindings.create("key.palette.title", "key.keyboard.v", category)
                .addListener(new PaletteBindListener())
                .addListener(new PaintingBindListener());
        event.register(ModBinds.palette);
    }
}
