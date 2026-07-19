package com.conquestrefabricated.client.gui.dependency.screen;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.client.gui.dependency.Dependency;
import com.conquestrefabricated.client.gui.dependency.DependencyType;
import com.conquestrefabricated.client.tutorial.Tutorials;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import static com.conquestrefabricated.client.utils.TextUtils.wrapComplexTextToList;

public class DependencyScreen extends Screen {

    private static final Identifier CTM = Identifier.parse("conquest:textures/gui/dependency/ctm.png");
    private static final int CTM_HEIGHT = 256;
    private static final int CTM_WIDTH = 432;

    private static final int LIST_HEIGHT = 64;
    private static final int TITLE_HEIGHT = 22;
    private static final int MARGIN_TOP = 10;
    private static final int MARGIN_BOTTOM = 28;

    private final Screen screen;
    private final List<Dependency> missing;
    private Checkbox check;

    public DependencyScreen(Screen parent, List<Dependency> missing) {
        super(Component.nullToEmpty("Dependencies"));
        this.screen = parent;
        this.missing = missing;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        ConquestConfig.INSTANCE.ignore_dependencies.set(check.selected());
        ConquestConfig.INSTANCE.save();
        Minecraft.getInstance().setScreen(screen);
    }

    @Override
    protected void init() {
        check = Checkbox.builder(
                        Component.translatableWithFallback("conquest.dependency.checkbox", "Do not show again"),
                        font
                )
                .selected(false)
                .onValueChange((checkbox, checked) -> {
                })
                .build();

        Tutorials.dependencies = true;
        super.init();

        int center = width / 2;

        int imageHeight = getImageHeight();
        int imageWidth = getImageWidth(imageHeight);
        int paddingTop = getPaddingTop(imageHeight);
        int listTop = paddingTop + imageHeight;
        int listBottom = listTop + TITLE_HEIGHT + LIST_HEIGHT;

        // Add the close button
        addRenderableWidget(new Button.Builder(Component.translatable("conquest.dependency.close", "Continue"), b -> onClose())
                .bounds(center - 50, height - 24, 100, 20)
                .build()
        );

        // Add the checkbox
        check.setY(height - 24);
        check.setX(center + 50 + 8);
        addRenderableWidget(check);

        // Calculate modpack button position
        if (!ConquestConfig.INSTANCE.using_modpack.get()) {
            int topDist = 7;

            // Calculate the top distance by simulating text rendering
            Component[] recommendationOriginal = new Component[]{
                    Component.translatableWithFallback("conquest.dependency.1", "It appears you're not using the Conquest Reforged Modpack!").withStyle(ChatFormatting.GOLD),
                    Component.literal(""),
                    Component.translatableWithFallback("conquest.dependency.2", "Our modpack adds all of the required dependencies,"),
                    Component.translatableWithFallback("conquest.dependency.3", "along with optimization mods and proper configs for the best experience."),
                    Component.translatableWithFallback("conquest.dependency.4", "Getting all of the right versions of every mod is hard, this takes care of that for you."),
                    Component.translatableWithFallback("conquest.dependency.5", "If you're making your own modpack, you can use ours as a base."),
                    Component.translatableWithFallback("conquest.dependency.6", "Otherwise, this screen will show you which of the most essential dependencies are missing.")
            };

            int maxWidth = (int) (width * 0.95);
            List<Component> wrappedLines = new ArrayList<>();

            for (Component line : recommendationOriginal) {
                if (font.width(line) <= maxWidth) {
                    wrappedLines.add(line);
                } else {
                    wrapComplexTextToList(line, font, maxWidth, wrappedLines);
                }
            }

            int dist = 12;
            for (int i = 0; i < wrappedLines.size(); i++) {
                topDist += dist;
            }

            topDist += 12;

            // Add the modpack button
            addRenderableWidget(new Button.Builder(Component.translatableWithFallback("conquest.dependency.modpack", "Modpack"), btn -> {
                        try {
                            Util.getPlatform().openUri(new URI("https://modrinth.com/modpack/conquest-reforged-modpack"));
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    })
                            .bounds(center - 85, topDist, 170, 20)
                            .tooltip(Tooltip.create(Component.translatableWithFallback("conquest.dependency.tooltip.modpack", "Page for the Modpack is here, install via ATLauncher or Modrinth launcher!")))
                            .build()
            );
        }

        // Create and position dependency buttons
        int bottomDist = height - 50;
        int buttonsPerRow = 3;
        int buttonWidth = 120;

        if (missing.size() > buttonsPerRow) {
            bottomDist -= 24;
        }

        for (int i = 0; i < missing.size(); i++) {
            int row = i / buttonsPerRow; // Determine which row
            int col = i % buttonsPerRow; // Position within row (0, 1, or 2)

            // Calculate how many buttons are in this row
            int buttonsInThisRow = Math.min(buttonsPerRow, missing.size() - row * buttonsPerRow);

            // Calculate total width of buttons in this row
            int rowWidth = buttonsInThisRow * buttonWidth;

            // Find left edge of this row to center it
            int rowLeftEdge = center - (rowWidth / 2);

            // Calculate x and y positions
            int xPosition = rowLeftEdge + (col * buttonWidth) + (buttonWidth / 2);
            int yPosition = bottomDist + (row * 24);

            addRenderableWidget(createButton(missing.get(i), yPosition, xPosition));
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        //renderBackground(drawContext, mx, my, ticks);

        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int center = width / 2;
        int imageHeight = getImageHeight();
        int imageWidth = getImageWidth(imageHeight);
        int imageLeft = getImageLeft(imageWidth);
        int paddingTop = getPaddingTop(imageHeight);

        // Render recommendation text
        if (ConquestConfig.INSTANCE.using_modpack.get()) {
            Component[] recommendationOriginal = new Component[]{
                    Component.translatableWithFallback("conquest.dependency.1", "It appears you're not using the Conquest Reforged Modpack!").withStyle(ChatFormatting.GOLD),
                    Component.literal(""),
                    Component.translatableWithFallback("conquest.dependency.2", "Our modpack adds all of the required dependencies,"),
                    Component.translatableWithFallback("conquest.dependency.3", "along with optimization mods and proper configs for the best experience."),
                    Component.translatableWithFallback("conquest.dependency.4", "Getting all of the right versions of every mod is hard, this takes care of that for you."),
                    Component.translatableWithFallback("conquest.dependency.5", "If you're making your own modpack, you can use ours as a base."),
                    Component.translatableWithFallback("conquest.dependency.6", "Otherwise, this screen will show you which of the most essential dependencies are missing.")
            };

            int maxWidth = (int) (width * 0.95);
            List<Component> wrappedLines = new ArrayList<>();

            for (Component line : recommendationOriginal) {
                if (font.width(line) <= maxWidth) {
                    // Line fits, add it as is
                    wrappedLines.add(line);
                } else {
                    // Line needs wrapping - use alternate approach to avoid duplication
                    wrapComplexTextToList(line, font, maxWidth, wrappedLines);
                }
            }

            // Render the recommendation text
            int topDist = 7;
            int dist = 12;
            for (int i = 0; i < wrappedLines.size(); i++) {
                int paragraphWidth = font.width(wrappedLines.get(i));
                int paragraphOffset = paragraphWidth / 2;
                topDist += dist;
                graphics.text(font, wrappedLines.get(i).getVisualOrderText(), (int) (width / 2F - paragraphOffset), topDist, 0xFFFFFF);
            }
        }

        // Render the "Missing Dependencies" title
        int bottomDist = height - 50;
        if (missing.size() > 3) {
            bottomDist -= 24;
        }

        Component message = Component.translatableWithFallback("conquest.dependency.missing", "Missing Dependencies:").withStyle(ChatFormatting.GOLD);
        int titleWidth = font.width(message);
        int titleOffset = titleWidth / 2;
        graphics.text(font, message, (int) ((width / 2F) - titleOffset), bottomDist - 14, 0xFFFFFF);


    }

    private int getImageHeight() {
        // scale the image to the remaining vertical height after subtracting static height elements
        return Math.min(CTM_HEIGHT, height - MARGIN_TOP - TITLE_HEIGHT - LIST_HEIGHT - MARGIN_BOTTOM);
    }

    private int getImageWidth(int imageHeight) {
        // scale image width proportionally to the image height
        return Math.round(CTM_WIDTH * (((float) imageHeight) / CTM_HEIGHT));
    }

    private int getImageLeft(int imageWidth) {
        // find the left (x) pos of the image so that it is centered on screen
        return (width / 2) - (imageWidth / 2);
    }

    private int getPaddingTop(int imageHeight) {
        // adjust the top margin to ensure all content fits on screen without overlapping
        // attempt to center content vertically before receding upwards to accommodate larger gui scales

        int elementsHeight = imageHeight + TITLE_HEIGHT + LIST_HEIGHT;
        int paddingTop = (height - elementsHeight) / 2;
        int dif = height - (paddingTop + elementsHeight);
        if (dif < MARGIN_BOTTOM) {
            paddingTop -= dif;
            paddingTop = Math.max(paddingTop, 2);
        }
        return paddingTop;
    }

    private static Button createButton(Dependency dependency, int heightIn, int center) {
        return new Button.Builder(Component.translatable(dependency.getDisplayName()), btn -> {
            try {
                if (dependency.getType() == DependencyType.RESOURCEPACK) return;
                Util.getPlatform().openUri(new URI(dependency.getURL()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        })
                .bounds(center - 60, heightIn, 120, 20)
                .tooltip(Tooltip.create(Component.translatable("conquest.dependency.tooltip." + dependency.getId())))
                .build();
    }
}
