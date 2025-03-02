package com.xg7plugins.utils.text.component.serializer.tags;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.serializer.Tag;

import java.util.List;

public class GradientTag implements Tag {
    @Override
    public String name() {
        return "gradient";
    }

    @Override
    public void resolve(Component content, List<String> openArgs, List<String> closeArgs) {

        if (MinecraftVersion.isOlderThan(16)) return;

        if (openArgs.size() != 1) {
            throw new IllegalArgumentException("Gradient tag must have 1 open arguments");
        }
        if (closeArgs.size() != 1) {
            throw new IllegalArgumentException("Gradient tag must have 1 close arguments");
        }

        String contentText = content.content();

        String start = openArgs.get(0);
        String end = openArgs.get(1);

        start = start.replace("#", "");
        end = end.replace("#", "");

        if (start.length() != 6 || end.length() != 6) {
            throw new IllegalArgumentException("Gradient tag must have 6 characters in the open and close arguments");
        }

        java.awt.Color from = java.awt.Color.decode("#" + start);
        java.awt.Color to = java.awt.Color.decode("#" + end);

        double[] red = linear(from.getRed(), to.getRed(), contentText.length());
        double[] green = linear(from.getGreen(), to.getGreen(), contentText.length());
        double[] blue = linear(from.getBlue(), to.getBlue(), contentText.length());

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < contentText.length(); i++) {
            builder.append(net.md_5.bungee.api.ChatColor.of(new java.awt.Color(
                            (int) Math.round(red[i]),
                            (int) Math.round(green[i]),
                            (int) Math.round(blue[i]))))
                    .append(contentText.charAt(i));
        }

        content.setText(builder.toString());
    }
    private static double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }
}
