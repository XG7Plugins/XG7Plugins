package com.xg7plugins.utils.text;

import lombok.Getter;
import org.bukkit.ChatColor;

/**
 * Utility class for centralizing text in various Minecraft contexts by calculating
 * pixel-based character widths and adding appropriate padding.
 */
public class TextCentralizer {

    /**
     * Enum representing different contexts where a text can be centralized,
     * with their corresponding pixel widths.
     */
    @Getter
    public enum PixelsSize {

        CHAT(157), // Chat message width
        MOTD(127), // Server MOTD width  
        INV(75);   // Inventory name width

        final int pixels;

        PixelsSize(int pixels) {
            this.pixels = pixels;
        }

    }

    /**
     * Gets the pixel width of a character, accounting for bold formatting.
     * Characters are grouped by their width in pixels.
     *
     * @param c      The character to measure
     * @param isBold Whether the character is boldly formatted
     * @return The pixel width of the character
     */
    private static int getCharSize(char c, boolean isBold) {
        String[] chars = new String[]{"~@", "1234567890ABCDEFGHJKLMNOPQRSTUVWXYZabcedjhmnopqrsuvxwyz/\\+=-_^?&%$#", "{}fk*\"<>()", "It[] ", "'l`", "!|:;,.i", "¨´"};
        for (int i = 0; i < chars.length; i++) {
            if (chars[i].contains(String.valueOf(c))) {
                return isBold && c != ' ' ? 8 - i : 7 - i;
            }
        }

        return 4;
    }

    /**
     * Calculates the required padding spaces to center a text within a given pixel width.
     * Handles Minecraft color codes, hex colors and formatting.
     *
     * @param pixels Maximum pixel width
     * @param text   Text to be centered
     * @return String containing the required spaces for centering
     */
    public static String getSpacesCentralized(int pixels, String text) {

        int textWidth = 0;
        boolean isBold = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '§' || c == '&') {
                if (i + 1 >= text.length()) continue;
                char next = text.charAt(i + 1);

                if (next == 'x' && i + 13 < text.length()) {
                    i += 13;
                    continue;
                }

                if (next == '#' && i + 7 < text.length()) {
                    String hex = text.substring(i + 2, i + 8);
                    if (hex.matches("[0-9a-fA-F]{6}")) {
                        i += 7;
                        continue;
                    }
                }

                if (next == 'l' || next == 'L') {
                    isBold = true;
                } else if (next == 'r' || next == 'R') {
                    isBold = false;
                }

                i++;
                continue;
            }

            textWidth += getCharSize(c, isBold);
        }

        int halfWidth = textWidth / 2;
        if (halfWidth >= pixels) return "";

        StringBuilder padding = new StringBuilder();
        int compensated = 0;
        while (compensated < pixels - halfWidth) {
            padding.append(" ");
            compensated += getCharSize(' ', false);
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
    public static String getCentralizedText(PixelsSize size, String rawTextWithColors) {

        String spaces = getSpacesCentralized(size.getPixels(), rawTextWithColors);

        return spaces + rawTextWithColors;
    }

}