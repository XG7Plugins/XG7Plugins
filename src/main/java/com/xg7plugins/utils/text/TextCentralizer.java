package com.xg7plugins.utils.text;

import lombok.Getter;
import org.bukkit.ChatColor;

public class TextCentralizer {

    @Getter
    public enum PixelsSize {

        CHAT(157),
        MOTD(127),
        INV(75);

        final int pixels;

        PixelsSize (int pixels) {
            this.pixels = pixels;
        }

    }

    private static int getCharSize(char c, boolean isBold) {
        String[] chars = new String[]{"~@", "1234567890ABCDEFGHJKLMNOPQRSTUVWXYZabcedjhmnopqrsuvxwyz/\\+=-_^?&%$#", "{}fk*\"<>()", "It[] ", "'l`", "!|:;,.i", "¨´"};
        for (int i = 0; i < chars.length; i++) {
            if (chars[i].contains(String.valueOf(c))) {
                return isBold && c != ' ' ? 8 - i : 7 - i;
            }
        }

        return 4;
    }

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


    public static String getCentralizedText(PixelsSize size, String rawTextWithColors) {

        String spaces = getSpacesCentralized(size.getPixels(), rawTextWithColors);

        return spaces + rawTextWithColors;
    }



}