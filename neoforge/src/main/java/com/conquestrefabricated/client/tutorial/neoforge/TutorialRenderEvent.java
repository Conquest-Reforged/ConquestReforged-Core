package com.conquestrefabricated.client.tutorial.neoforge;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.client.gui.dependency.Dependency;
import com.conquestrefabricated.client.gui.dependency.DependencyList;
import com.conquestrefabricated.client.gui.dependency.screen.DependencyScreen;
import com.conquestrefabricated.client.gui.intro.IntroScreen;
import com.conquestrefabricated.client.tutorial.Tutorials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

public class TutorialRenderEvent {

    private final DependencyList dependencies = DependencyList.load();

    public TutorialRenderEvent() {
    }

    @SubscribeEvent
    public void render(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof TitleScreen) {
            List<Dependency> missing = dependencies.getMissingDependencies();
            IntroScreen introScreen = new IntroScreen(event.getScreen());
            DependencyScreen dependencyScreen = new DependencyScreen(event.getScreen(), missing);
            DependencyScreen dependencyScreen2 = new DependencyScreen(introScreen, missing);

            if ((ConquestConfig.INSTANCE.ignore_dependencies.get() && ConquestConfig.INSTANCE.ignore_intro.get())
                    || (missing.isEmpty() && Tutorials.intro)
                    || (Tutorials.dependencies && Tutorials.intro)
            ) {
                NeoForge.EVENT_BUS.unregister(this);
                return;
            }

            if (!Tutorials.intro) {
                if (ConquestConfig.INSTANCE.ignore_intro.get()) {
                    if (!missing.isEmpty()) {
                        Minecraft.getInstance().setScreen(dependencyScreen);
                    }
                    if (Tutorials.dependencies || missing.isEmpty()) {
                        NeoForge.EVENT_BUS.unregister(this);
                    }
                } else if ((ConquestConfig.INSTANCE.ignore_dependencies.get()) || missing.isEmpty()) {
                    Minecraft.getInstance().setScreen(introScreen);
                } else {
                    Minecraft.getInstance().setScreen(dependencyScreen2);
                }
            }
        }
    }
}