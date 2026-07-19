package com.conquestrefabricated.core.client.input.fabric;

import com.conquestrefabricated.core.client.input.BindListener;
import com.conquestrefabricated.core.client.input.EventBinding;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import java.util.LinkedList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class Bindings {

    private static final List<EventBinding> bindings = new LinkedList<>();

//    public static KeyBinding createBasic(String description, String input, String category) {
//        KeyBinding binding = new KeyBinding(description, InputUtil.fromTranslationKey(input).getCode(), category);
//        KeyBindingHelper.registerKeyBinding(binding);
//        return binding;
//    }


    public static EventBinding create(String description, String input, String category) {
        EventBinding binding = new EventBinding(description, InputConstants.getKey(input), category);
        bindings.add(binding);
        return binding;
    }

    public static EventBinding create(String description, String input, String category, BindListener listener) {
        EventBinding binding = new EventBinding(description, InputConstants.getKey(input), category);
        binding.addListener(listener);
        bindings.add(binding);
        return binding;
    }

    public static void tick(Minecraft client) {
        for (EventBinding binding : bindings) {
            if (binding.checkPressed()) {
                return;
            }
            binding.checkHeld();
        }
    }
}
