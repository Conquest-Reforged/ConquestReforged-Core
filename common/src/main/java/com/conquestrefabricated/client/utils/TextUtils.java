package com.conquestrefabricated.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class TextUtils {

    /**
     * Wraps a complex Text object into multiple lines based on width
     * This version uses OrderedText for accurate width measurements
     *
     * @param text       The Text to wrap
     * @param maxWidth   Maximum width in pixels
     * @param resultList List to add the wrapped Text objects to
     */
    public static void wrapComplexTextToList(Component text, Font textRenderer, int maxWidth, List<Component> resultList) {
        // Convert to a list of styled text segments
        List<StyledTextSegment> segments = new ArrayList<>();
        text.visit((style, string) -> {
            segments.add(new StyledTextSegment(string, style));
            return Optional.empty();
        }, Style.EMPTY);

        // Now wrap the segments
        MutableComponent currentLine = Component.literal("");
        int currentLineWidth = 0;

        for (StyledTextSegment segment : segments) {
            String content = segment.text;
            Style style = segment.style;

            // Split content by words, preserving spaces
            List<String> words = splitIntoWords(content);

            for (String word : words) {
                int wordWidth = getWidth(word, textRenderer, style);

                // Check if adding this word would exceed max width
                if (currentLineWidth + wordWidth > maxWidth) {
                    // Current line is full, add it to results and start a new line
                    if (!currentLine.getString().isEmpty()) {
                        resultList.add(currentLine);
                        currentLine = Component.literal("");
                        currentLineWidth = 0;
                    }

                    // Check if the word itself is too long for a single line
                    if (wordWidth > maxWidth) {
                        // Split the word into multiple lines
                        splitLongWord(word, textRenderer, style, maxWidth, resultList);
                    } else {
                        // Add word to the new line
                        currentLine = currentLine.append(Component.literal(word).setStyle(style));
                        currentLineWidth = wordWidth;
                    }
                } else {
                    // Word fits on current line
                    currentLine = currentLine.append(Component.literal(word).setStyle(style));
                    currentLineWidth += wordWidth;
                }
            }
        }

        // Add the last line if not empty
        if (!currentLine.getString().isEmpty()) {
            resultList.add(currentLine);
        }
    }

    /**
     * Helper class to store text with its style
     */
    private static class StyledTextSegment {
        public final String text;
        public final Style style;

        public StyledTextSegment(String text, Style style) {
            this.text = text;
            this.style = style;
        }
    }

    /**
     * Split text into words, preserving spaces
     */
    private static List<String> splitIntoWords(String text) {
        List<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        boolean inWhitespace = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean isWhitespace = Character.isWhitespace(c);

            if (isWhitespace != inWhitespace && currentWord.length() > 0) {
                words.add(currentWord.toString());
                currentWord = new StringBuilder();
            }

            currentWord.append(c);
            inWhitespace = isWhitespace;
        }

        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }

        return words;
    }

    /**
     * Split a word that's too long for a single line
     */
    private static void splitLongWord(String word, Font textRenderer, Style style, int maxWidth, List<Component> resultList) {
        int start = 0;
        while (start < word.length()) {
            // Find how many characters can fit
            int end = findFittingCharacters(word.substring(start), textRenderer, style, maxWidth);
            String part = word.substring(start, start + end);
            resultList.add(Component.literal(part).setStyle(style));
            start += end;
        }
    }

    /**
     * Find how many characters from a string can fit within maxWidth
     */
    private static int findFittingCharacters(String text, Font textRenderer, Style style, int maxWidth) {
        if (text.isEmpty()) return 0;

        int low = 0;
        int high = text.length();

        while (low < high) {
            int mid = (low + high + 1) / 2;
            int width = getWidth(text.substring(0, mid), textRenderer, style);

            if (width <= maxWidth) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }

        return Math.max(1, low); // Ensure at least one character is included
    }

    /**
     * Helper method to get the width of text with a specified style
     */
    private static int getWidth(String text, Font textRenderer, Style style) {
        return textRenderer.width(Component.literal(text).setStyle(style));
    }
}
