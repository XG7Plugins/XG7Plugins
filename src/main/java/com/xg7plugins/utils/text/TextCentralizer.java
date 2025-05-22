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

    public static String getSpacesCentralized(int pixels, String rawTextWithColors) {

        int textWidth = 0;
        boolean cCode = false;
        boolean isBold = false;
        int cCodeCount = 0;

        for (char c : rawTextWithColors.toCharArray()) {

            if (c == '&' || c == '§') {
                cCode = true;
                cCodeCount++;
                continue;
            }

            if (cCode && net.md_5.bungee.api.ChatColor.ALL_CODES.contains(String.valueOf(c))) {
                cCode = false;
                cCodeCount = 0;
                isBold = c == 'l' || c == 'L';
                continue;
            }

            if (cCode) {
                while (cCodeCount != 0) {
                    cCodeCount--;
                    textWidth += getCharSize('&', isBold);
                }
            }

            textWidth += getCharSize(c, isBold);
        }

        textWidth /= 2;

        if (textWidth > pixels) {
            return rawTextWithColors;
        }

        StringBuilder builder = new StringBuilder();
        int compensated = 0;

        while (compensated < pixels - textWidth) {
            builder.append(ChatColor.COLOR_CHAR + "r ");
            compensated += 4;
        }

        return builder.toString();
    }


    public static String getCentralizedText(PixelsSize size, String rawTextWithColors) {

        String spaces = getSpacesCentralized(size.getPixels(), rawTextWithColors);

        return spaces + rawTextWithColors;
    }



}