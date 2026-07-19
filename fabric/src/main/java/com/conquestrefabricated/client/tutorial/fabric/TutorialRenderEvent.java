package com.conquestrefabricated.client.tutorial.fabric;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.client.gui.dependency.Dependency;
import com.conquestrefabricated.client.gui.dependency.DependencyList;
import com.conquestrefabricated.client.gui.dependency.screen.DependencyScreen;
import com.conquestrefabricated.client.gui.intro.IntroScreen;
import com.conquestrefabricated.client.tutorial.Tutorials;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import java.util.List;

@Environment(EnvType.CLIENT)
public class TutorialRenderEvent {

    private final DependencyList dependencies = DependencyList.load();
    private boolean hasRenderedIntro = false;
    private boolean hasRenderedDependency = false;

    public TutorialRenderEvent() {
    }

   // @SubscribeEvent
    public void render(Screen currentScreen, Minecraft client) {
        if (currentScreen instanceof TitleScreen) {
            List<Dependency> missing = dependencies.getMissingDependencies();
            IntroScreen introScreen = new IntroScreen(currentScreen);
            DependencyScreen dependencyScreen = new DependencyScreen(currentScreen, missing);
            DependencyScreen dependencyScreen2 = new DependencyScreen(introScreen, missing);

            if ((ConquestConfig.INSTANCE.ignore_dependencies.get() && ConquestConfig.INSTANCE.ignore_intro.get())
                    || (missing.isEmpty() && Tutorials.intro)
                    || (Tutorials.dependencies && Tutorials.intro)
            ) {
                //MinecraftForge.EVENT_BUS.unregister(this);
                return;
            }

            if (!Tutorials.intro) {
                if (ConquestConfig.INSTANCE.ignore_intro.get()) {
                    if (!missing.isEmpty()) {
                        client.setScreen(dependencyScreen);
                    }
                    if (Tutorials.dependencies || missing.isEmpty()) {
                       return;
                        // MinecraftForge.EVENT_BUS.unregister(this);
                    }
                } else if (ConquestConfig.INSTANCE.ignore_dependencies.get() || missing.isEmpty()) {
                    client.setScreen(introScreen);
                } else {
                    client.setScreen(dependencyScreen2);
                }
            }
        }
    }
}
