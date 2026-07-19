package com.conquestrefabricated.core.client.input;

import java.util.Optional;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class BindEvent {

    public final KeyMapping binding;
    public final boolean inGame;
    public final boolean inGui;
    public final Optional<Player> player;

    public BindEvent(KeyMapping binding) {
        this.binding = binding;
        this.inGame = Minecraft.getInstance().player != null;
        //currentScreen = screen?
        this.inGui = Minecraft.getInstance().screen != null;
        this.player = Optional.ofNullable(Minecraft.getInstance().player);
    }
}
