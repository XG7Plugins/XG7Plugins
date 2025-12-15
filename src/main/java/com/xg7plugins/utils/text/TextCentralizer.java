package com.xg7plugins.utils.text;

import lombok.Getter;
import org.bukkit.ChatColor;

/**
 * Utility class for centralizing text in various Minecraft contexts by calculating
 * pixel-based character widths and adding appropriate padding.
 */
public class TextCentralizer {

    /**
     * Calculates the required padding spaces to center a text within a given pixel width.
     * Handles Minecraft color codes, hex colors and formatting.
     *
     * @param pixels Maximum pixel width
     * @param text   Text to be centered
     * @return String containing the required spaces for centering
     */
    public static String getSpacesCentralized(int pixels, String text) {
        int textWidth = Text.getTextWidth(text);
        int halfWidth = textWidth / 2;
        if (halfWidth >= pixels) return "";

        StringBuilder padding = new StringBuilder();
        int compensated = 0;

        while (compensated < pixels - halfWidth) {
            padding.append(" ");
            compensated += Text.getCharSize(' ', false);
        }

        return padding.toString();
    }


    /**
     * Centers a text string for a given context by adding appropriate padding.
     *
     * @param size              The context where the text will be displayed
     * @param rawTextWithColors The text to center, including color codes
     * @return The centered text with padding
     */
    public static String getCentralizedText(Text.PixelsSize size, String rawTextWithColors) {

        String spaces = getSpacesCentralized(size.getPixels(), rawTextWithColors);

        return spaces + rawTextWithColors;
    }

}