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
        boolean cCode = false;
        boolean isBold = false;
        boolean isRgb = false;
        int rgbCount = 0;
        int cCodeCount = 0;
        int rgbToAdd = 0;

        for (char c : text.toCharArray()) {

            if (isRgb) {
                if (rgbCount == 6) {
                    isRgb = false;
                    continue;
                }
                if ("0123456789aAbBcCdDeEfF".contains(String.valueOf(c))) {
                    rgbToAdd = getCharSize(c, isBold);
                    rgbCount++;
                    continue;
                }
                rgbCount = 0;
                textWidth += rgbToAdd;
                continue;
            }

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
                if (c == '#') {
                    cCode = false;
                    isRgb = true;
                    continue;
                }
                while (cCodeCount != 0) {
                    cCodeCount--;
                    textWidth += getCharSize('&', isBold);
                }
            }

            textWidth += getCharSize(c, isBold);
        }

        textWidth /= 2;

        if (textWidth > pixels) {
            return text;
        }

        StringBuilder builder = new StringBuilder();
        int compensated = 0;

        while (compensated < pixels - textWidth) {
            builder.append(ChatColor.COLOR_CHAR + "r ");
            compensated += 4;
        }

        String result = builder.toString();
        return result;
    }


    public static String getCentralizedText(PixelsSize size, String text) {

        String spaces = getSpacesCentralized(size.getPixels(), text);

        return spaces + text;
    }



}