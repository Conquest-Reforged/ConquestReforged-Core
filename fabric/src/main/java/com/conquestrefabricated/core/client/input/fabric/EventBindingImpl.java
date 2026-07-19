package com.conquestrefabricated.core.client.input.fabric;

import com.conquestrefabricated.core.client.input.EventBinding;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

public class EventBindingImpl {
    public static void registerBinding(EventBinding binding) {
        KeyMappingHelper.registerKeyMapping(binding);
    }
}
