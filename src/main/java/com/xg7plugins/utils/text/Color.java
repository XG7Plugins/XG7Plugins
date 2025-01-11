package com.xg7plugins.utils.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("\\[G#([0-9a-fA-F]{6})\\](.*?)\\[/G#([0-9a-fA-F]{6})\\]");
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String hex(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color.substring(1)) + "");
            matcher = HEX_PATTERN.matcher(text);
        }
        return text;
    }

    private static double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }

    public static String gradient(String text) {
        Matcher matcher = GRADIENT_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            java.awt.Color from = java.awt.Color.decode("#" + matcher.group(1));
            java.awt.Color to = java.awt.Color.decode("#" + matcher.group(3));
            String textHex = matcher.group(2);

            double[] red = linear(from.getRed(), to.getRed(), textHex.length());
            double[] green = linear(from.getGreen(), to.getGreen(), textHex.length());
            double[] blue = linear(from.getBlue(), to.getBlue(), textHex.length());

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < textHex.length(); i++) {
                builder.append(net.md_5.bungee.api.ChatColor.of(new java.awt.Color(
                                (int) Math.round(red[i]),
                                (int) Math.round(green[i]),
                                (int) Math.round(blue[i]))))
                        .append(textHex.charAt(i));
            }
            matcher.appendReplacement(result, builder.toString());
        }
        matcher.appendTail(result);

        return result.toString() + net.md_5.bungee.api.ChatColor.RESET;
    }


}
