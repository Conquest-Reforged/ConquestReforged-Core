package com.conquestrefabricated.client.gui.intro;

import com.conquestrefabricated.client.ModBinds;
import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.client.tutorial.Tutorials;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;


public class IntroScreen extends Screen {

    private static final Identifier LOGO = Identifier.parse("conquest:textures/gui/intro/logosmall.png");
    private static final int LOGO_FRAME_SIZE = 145;
    private static final int LOGO_FRAME_COUNT = 111;
    private static final int LOGO_FRAME_TICKS = 2;
    private static final int LOGO_DRAW_SIZE = 64;
    private static final int LOGO_MARGIN = 12;

    private static final Identifier PATREON_LOGO = Identifier.parse("conquest:textures/gui/intro/patreon.png");

    private static final int PATREON_SIZE = 52;
    private static final int PATREON_FRAME_SIZE = 145;
    private static final int PATREON_FRAME_COUNT = 14;
    private static final int PATREON_FRAME_TICKS = 4;

    // Packed ARGB tint. A multiplied color can't exceed white, so hover brightens
    // relative to a slightly dimmed resting state rather than "overbrightening" past 1.0.
    private static final int PATREON_COLOR_NORMAL = 0xFFE0E0E0;
    private static final int PATREON_COLOR_HOVER = 0xFFFFFFFF;

    private static final int TEXT_LINK_COLOR = 0xFFFFFFFF;
    private static final int TEXT_LINK_UNDERLINE_COLOR = 0x80FFFFFF;

    private static final String PATREON_URL = "https://www.patreon.com/c/ConquestReforged?vanity=user";

    private final Screen screen;
    private Checkbox check;

    private Button paletteKeybindWidget;
    private Button pickBlockKeybindWidget;
    private TextLinkWidget patreonHintWidget;


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
        Tutorials.intro = true;
        super.init();

        int center = width / 2;
        int bottomRowShift = (PATREON_SIZE + 12) / 2;
        int bottomRowCenter = center - bottomRowShift;

        addRenderableWidget(new Button.Builder(Component.translatableWithFallback("conquest.intro.close", "Continue"), b -> onClose())
                .bounds(bottomRowCenter - 50, height - 24, 100, 20)
                .build()
        );

        check = Checkbox.builder(
                        Component.translatableWithFallback("conquest.intro.checkbox", "Do not show again"),
                        font
                )
                .selected(false)
                .onValueChange((checkbox, checked) -> {
                })
                .build();
        check.setY(height - 24);
        check.setX(bottomRowCenter + 50 + 8);
        addRenderableWidget(check);

        PatreonButton patreonButton = new PatreonButton(
                width - PATREON_SIZE - 3,
                height - PATREON_SIZE - 3,
                PATREON_SIZE,
                PATREON_SIZE,
                b -> Util.getPlatform().openUri(PATREON_URL)
        );
        patreonButton.setTooltip(Tooltip.create(Component.translatableWithFallback(
                "conquest.intro.patreon.tooltip",
                "Your support helps us keep updating to new versions and adding more blocks and features!"
        )));
        addRenderableWidget(patreonButton);

        // Plain vanilla buttons - Button.Plain (via the builder) already gives the sprite
        // background + scrolling label, same look as Continue, no custom subclass needed.
        Component paletteKeyLabel = Component.literal(ModBinds.getPaletteBind().getTranslatedKeyMessage().getString().toUpperCase()).withStyle(ChatFormatting.GOLD)
                .append(Component.translatableWithFallback("conquest.intro.2.short", " - shows texture shape variants in the block palette").withStyle(ChatFormatting.WHITE));

        Component paletteTooltip = Component.literal("(Creative Mode only) ").withStyle(ChatFormatting.GREEN)
                .append(Component.translatableWithFallback("conquest.intro.3", "Works while hovering over a block in the creative menu or when selected in the hotbar.").withStyle(ChatFormatting.GRAY));

        paletteKeybindWidget = Button.builder(paletteKeyLabel, b -> {})
                .tooltip(Tooltip.create(paletteTooltip))
                .build();
        addRenderableWidget(paletteKeybindWidget);

        Component pickBlockKeyLabel = Component.translatableWithFallback("conquest.intro.pickblock", "CTRL+MIDDLE-MOUSE-BUTTON").withStyle(ChatFormatting.GOLD)
                .append(Component.translatableWithFallback("conquest.intro.4.short", " - press while looking at a block").withStyle(ChatFormatting.WHITE));

        Component pickBlockTooltip = Component.literal("(Creative Mode only) ").withStyle(ChatFormatting.GREEN)
                .append(Component.translatableWithFallback("conquest.intro.5", "Gives the exact shape you're looking at as a block item in your hotbar.").withStyle(ChatFormatting.GRAY));

        pickBlockKeybindWidget = Button.builder(pickBlockKeyLabel, b -> {})
                .tooltip(Tooltip.create(pickBlockTooltip))
                .build();
        addRenderableWidget(pickBlockKeybindWidget);

        Component patreonHint = Component.translatableWithFallback("conquest.intro.patreon", "Enjoying the mod? Support continued development on our Patreon!").withStyle(ChatFormatting.LIGHT_PURPLE);
        patreonHintWidget = new TextLinkWidget(0, 0, 0, 10, patreonHint, b -> Util.getPlatform().openUri(PATREON_URL));
        addRenderableWidget(patreonHintWidget);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor drawContext, int mx, int my, float ticks) {
        super.extractRenderState(drawContext, mx, my, ticks);

        Component welcomeString = Component.translatableWithFallback("conquest.intro.welcome", "Welcome to Conquest Reforged!").withStyle(ChatFormatting.GOLD);
        Component intro = Component.translatableWithFallback("conquest.intro.1", "This screen will introduce you to keybinds for making building faster.");

        int dist = 12;

        int logoFrame = (int) ((System.currentTimeMillis() / (LOGO_FRAME_TICKS * 50L)) % LOGO_FRAME_COUNT);
        int logoV = logoFrame * LOGO_FRAME_SIZE;

        drawContext.blit(
                RenderPipelines.GUI_TEXTURED, LOGO,
                (width - LOGO_DRAW_SIZE) / 2, LOGO_MARGIN,
                0, logoV,
                LOGO_DRAW_SIZE, LOGO_DRAW_SIZE,
                LOGO_FRAME_SIZE, LOGO_FRAME_SIZE,
                LOGO_FRAME_SIZE, LOGO_FRAME_SIZE * LOGO_FRAME_COUNT,
                0xFFFFFFFF
        );

        int y = LOGO_MARGIN + LOGO_DRAW_SIZE + dist;

        y = drawCentered(drawContext, welcomeString, y, dist);
        y = drawCentered(drawContext, intro, y, dist);
        y += dist;

        y = positionButtonWidget(paletteKeybindWidget, y);
        y += 6;

        y = positionButtonWidget(pickBlockKeybindWidget, y);
        y += dist;

        positionTextWidget(patreonHintWidget, y);
    }

    private int drawCentered(GuiGraphicsExtractor drawContext, Component text, int y, int dist) {
        int titleWidth = font.width(text);
        drawContext.text(font, text.getVisualOrderText(), (width - titleWidth) / 2, y, 0xFFFFFFFF);
        return y + dist;
    }

    private int positionButtonWidget(AbstractWidget widget, int y) {
        int padding = 16;
        int textWidth = font.width(widget.getMessage());
        int widgetWidth = textWidth + padding;
        widget.setWidth(widgetWidth);
        widget.setX((width - widgetWidth) / 2);
        widget.setY(y);
        return y + widget.getHeight();
    }

    private void positionTextWidget(AbstractWidget widget, int y) {
        int textWidth = font.width(widget.getMessage());
        widget.setWidth(textWidth);
        widget.setX((width - textWidth) / 2);
        widget.setY(y);
    }

    /**
     * Custom-textured button - draws the animated Patreon badge instead of the vanilla
     * sprite background. Overrides extractContents (the AbstractButton hook), NOT
     * extractWidgetRenderState (that's final on AbstractButton) or renderWidget (old API).
     */
    private static class PatreonButton extends Button {

        protected PatreonButton(int x, int y, int width, int height, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            int frame = (int) ((System.currentTimeMillis() / (PATREON_FRAME_TICKS * 50L)) % PATREON_FRAME_COUNT);
            int v = frame * PATREON_FRAME_SIZE;

            int color = isHovered() ? PATREON_COLOR_HOVER : PATREON_COLOR_NORMAL;

            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, PATREON_LOGO,
                    getX(), getY(),
                    0, v,
                    width, height,
                    PATREON_FRAME_SIZE, PATREON_FRAME_SIZE,
                    PATREON_FRAME_SIZE, PATREON_FRAME_SIZE * PATREON_FRAME_COUNT,
                    color
            );
        }
    }

    /**
     * Borderless, no-background text widget - opens a URL on click, thin underline on hover.
     * No sprite/label from AbstractButton's defaults - fully custom extractContents.
     */
    private static class TextLinkWidget extends Button {

        protected TextLinkWidget(int x, int y, int width, int height, Component message, OnPress onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            graphics.text(Minecraft.getInstance().font, getMessage().getVisualOrderText(), getX(), getY(), TEXT_LINK_COLOR);

            if (isHovered()) {
                graphics.fill(getX(), getY() + getHeight(), getX() + getWidth(), getY() + getHeight() + 1, TEXT_LINK_UNDERLINE_COLOR);
            }
        }
    }
}