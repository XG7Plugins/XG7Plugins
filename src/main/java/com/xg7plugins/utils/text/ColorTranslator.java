package com.xg7plugins.utils.text;

import java.util.*;
import java.util.regex.*;

/**
 * Complete translator for Legacy color codes (&) to MiniMessage
 * Supports basic codes, hexadecimal and complex combinations
 */
public class ColorTranslator {

    private static final Map<Character, String> LEGACY_COLORS = new HashMap<>();

    static {

        LEGACY_COLORS.put('0', "<black>");
        LEGACY_COLORS.put('1', "<dark_blue>");
        LEGACY_COLORS.put('2', "<dark_green>");
        LEGACY_COLORS.put('3', "<dark_aqua>");
        LEGACY_COLORS.put('4', "<dark_red>");
        LEGACY_COLORS.put('5', "<dark_purple>");
        LEGACY_COLORS.put('6', "<gold>");
        LEGACY_COLORS.put('7', "<gray>");
        LEGACY_COLORS.put('8', "<dark_gray>");
        LEGACY_COLORS.put('9', "<blue>");
        LEGACY_COLORS.put('a', "<green>");
        LEGACY_COLORS.put('b', "<aqua>");
        LEGACY_COLORS.put('c', "<red>");
        LEGACY_COLORS.put('d', "<light_purple>");
        LEGACY_COLORS.put('e', "<yellow>");
        LEGACY_COLORS.put('f', "<white>");

        LEGACY_COLORS.put('k', "<obfuscated>");
        LEGACY_COLORS.put('l', "<bold>");
        LEGACY_COLORS.put('m', "<strikethrough>");
        LEGACY_COLORS.put('n', "<underlined>");
        LEGACY_COLORS.put('o', "<italic>");
        LEGACY_COLORS.put('r', "<reset>");

    }

    private static final List<String> FORMATTING_TAGS = Arrays.asList("<bold>", "<italic>", "<underlined>",
            "<strikethrough>", "<obfuscated>");

    public static String translateLegacyToMini(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        Stack<String> openFormats = new Stack<>();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if ((c == '&' || c == '§') && i + 1 < text.length()) {
                char code = Character.toLowerCase(text.charAt(i + 1));

                if (code == '#' && i + 7 < text.length()) {
                    String hex = text.substring(i + 2, i + 8);
                    if (hex.matches("[a-fA-F0-9]{6}")) {
                        closeAllFormats(result, openFormats);
                        result.append("<#").append(hex).append(">");
                        i += 7; // Pula &#rrggbb
                        continue;
                    }
                }

                if (LEGACY_COLORS.containsKey(code)) {
                    String miniTag = LEGACY_COLORS.get(code);

                    if (miniTag.equals("<reset>")) {
                        closeAllFormats(result, openFormats);
                        result.append(miniTag);
                        i++;
                        continue;
                    }
                    if (!FORMATTING_TAGS.contains(miniTag)) {
                        closeAllFormats(result, openFormats);
                        result.append(miniTag);
                    }
                    if (FORMATTING_TAGS.contains(miniTag)) {
                        result.append(miniTag);
                        openFormats.push(miniTag);
                    }
                    i++;
                    continue;
                }
            }
            result.append(c);
        }

        closeAllFormats(result, openFormats);

        return result.toString();
    }

    public static String translateMiniToLegacy(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String result = text;

        for (Map.Entry<Character, String> entry : LEGACY_COLORS.entrySet()) {
            String miniTag = entry.getValue();
            char legacyCode = entry.getKey();

            if (FORMATTING_TAGS.contains(miniTag)) {
                String closeTag = miniTag.replace("<", "</");
                result = result.replace(closeTag, "");
            }

            result = result.replace(miniTag, "&" + legacyCode);
        }

        Pattern hexPattern = Pattern.compile("<#([a-fA-F0-9]{6})>");
        Matcher hexMatcher = hexPattern.matcher(result);
        result = hexMatcher.replaceAll("&#$1");

        return result;
    }
    private static void closeAllFormats(StringBuilder result, Stack<String> openFormats) {
        while (!openFormats.isEmpty()) {
            String tag = openFormats.pop();
            String closeTag = tag.replace("<", "</");
            result.append(closeTag);
        }
    }
}