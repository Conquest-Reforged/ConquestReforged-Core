package com.conquestrefabricated.core.client.input.neoforge;

import com.conquestrefabricated.core.client.input.BindListener;
import com.conquestrefabricated.core.client.input.EventBinding;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.LinkedList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT)
public class Bindings {

    private static final List<EventBinding> bindings = new LinkedList<>();

//    public static KeyBinding createBasic(String description, String input, String category) {
//        return new KeyBinding(description, InputUtil.fromTranslationKey(input).getCode(), category);
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

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        for (EventBinding binding : bindings) {
            if (binding.checkPressed()) {
                return;
            }
            binding.checkHeld();
        }
    }
}
