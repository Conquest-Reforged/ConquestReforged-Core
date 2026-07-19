package com.conquestrefabricated.core.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.injectables.annotations.ExpectPlatform;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class EventBinding extends KeyMapping {

    private static final Map<String, Category> CATEGORIES = new ConcurrentHashMap<>();

    private final List<BindListener> listeners = new LinkedList<>();

    private boolean down = false;

    // String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, final InputMappings.Type inputType, final int keyCode, String category
    public EventBinding(String description, InputConstants.Key input, String category) {
        this(description, input, category, InputConstants.Type.KEYSYM);
    }

    public EventBinding(String description, InputConstants.Key input, String category, InputConstants.Type context) {
        super(description, context, input.getValue(), resolveCategory(category));
        registerBinding(this);
        //KeyBindingHelper.registerKeyBinding(this);
    }

    private static KeyMapping.Category resolveCategory(String category) {
        return CATEGORIES.computeIfAbsent(category,
                name -> KeyMapping.Category.register(Identifier.parse(name)));
    }

    @ExpectPlatform
    public static void registerBinding(EventBinding binding) {
        throw new AssertionError("This method should be replaced by platform implementations!");
    }

    public EventBinding addListener(BindListener listener) {
        listeners.add(listener);
        return this;
    }

    public boolean checkPressed() {
        boolean pressed = super.consumeClick();
        if (pressed) {
            BindEvent event = new BindEvent(this);
            listeners.forEach(l -> l.onPress(event));
        }
        return pressed;
    }

    public boolean checkHeld() {
        boolean down = super.isDown();
        if (down) {
            BindEvent event = new BindEvent(this);
            listeners.forEach(l -> l.onHold(event));
        } else if (this.down) {
            BindEvent event = new BindEvent(this);
            listeners.forEach(l -> l.onRelease(event));
        }
        return this.down = down;
    }
/*
    private static IKeyConflictContext context = new IKeyConflictContext() {
        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return false;
        }
    };*/
}
