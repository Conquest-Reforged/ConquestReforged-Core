package com.conquestrefabricated.client.gui.intro;

import com.conquestrefabricated.client.ModBinds;
import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.client.tutorial.Tutorials;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static com.conquestrefabricated.client.utils.TextUtils.wrapComplexTextToList;


public class IntroScreen extends Screen {

    private static final Identifier LOGO = Identifier.parse("conquest:textures/gui/intro/logosmall.png");
    private static final int LOGO_HEIGHT = 211;
    private static final int LOGO_WIDTH = 211;

    private final Screen screen;
    private Checkbox check;


    public IntroScreen(Screen parent) {
        super(Component.literal("Intro"));
        this.screen = parent;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        ConquestConfig.INSTANCE.ignore_intro.set(check.selected());
        ConquestConfig.INSTANCE.save();
        Minecraft.getInstance().setScreen(screen);
    }

    @Override
    protected void init() {
        check = Checkbox.builder(
                        Component.translatableWithFallback("conquest.intro.checkbox", "Do not show again"),
                        font
                )
                .selected(false)
                .onValueChange((checkbox, checked) -> {
                })
                .build();

        Tutorials.intro = true;
        super.init();

        int center = width / 2;

        addRenderableWidget(new Button.Builder(Component.translatableWithFallback("conquest.intro.close", "Continue"), b -> onClose())
                .bounds(center - 50, height - 24, 100, 20)
                .build()
        );

        check.setY(height - 24);
        check.setX(center + 50 + 8);
        addRenderableWidget(check);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor drawContext, int mx, int my, float ticks) {
        super.extractRenderState(drawContext, mx, my, ticks);

        Component paletteKeyLetter = Component.literal(ModBinds.getPaletteBind().getTranslatedKeyMessage().getString().toUpperCase()).withStyle(ChatFormatting.GOLD);
        Component blockstateSelectorKeyLetter = Component.translatableWithFallback("conquest.intro.pickblock","CTRL+MIDDLE-MOUSE-BUTTON").withStyle(ChatFormatting.GOLD);
        Component welcomeString = Component.translatableWithFallback("conquest.intro.welcome","Welcome to Conquest Reforged!").withStyle(ChatFormatting.GOLD);

        Component[] messageOriginal = new Component[]{welcomeString,
                Component.translatableWithFallback("conquest.intro.1","This screen will introduce you to keybinds for making building faster."),
                Component.literal(""),
                Component.literal("\"").append(paletteKeyLetter).append("\"").append(Component.translatableWithFallback("conquest.intro.2", " - (Creative Mode only) shows texture shape variants in the block palette.")),
                Component.translatableWithFallback("conquest.intro.3","Works while hovering over a block in the creative menu or when selected in the hotbar."),
                Component.literal(""),
                Component.literal("\"").append(blockstateSelectorKeyLetter).append("\"").append(Component.translatableWithFallback("conquest.intro.4", " - (Creative Mode only) press while looking at a block.")),
                Component.translatableWithFallback("conquest.intro.5","This gives the exact shape you're looking at as a block item in your hotbar. Holding ALT as well will copy the exact direction of the block too.")
        };

        int maxWidth = (int)(width * 0.95);
        List<Component> wrappedLines = new ArrayList<>();

        for (Component line : messageOriginal) {
            if (font.width(line) <= maxWidth) {
                wrappedLines.add(line);
            } else {
                wrapComplexTextToList(line, font, maxWidth, wrappedLines);
            }
        }

        Component[] message = wrappedLines.toArray(new Component[0]);

        int dist = 12;

        drawContext.blit(RenderPipelines.GUI_TEXTURED, LOGO, getImageLeft(35), 15, 0, 0, 35, 35, LOGO_WIDTH, LOGO_HEIGHT, LOGO_WIDTH, LOGO_HEIGHT, 0xFFFFFFFF);

        for(int i = 0; i < message.length; i++) {
            int titleWidth = font.width(message[i]);
            int titleOffset = titleWidth / 2;
            drawContext.text(font, message[i].getVisualOrderText(), (int) (width / 2F - titleOffset), 70 + i * dist, 0xFFFFFFFF);
        }
    }

    private int getImageLeft(int imageWidth) {
        return (width / 2) - (imageWidth / 2);
    }
}