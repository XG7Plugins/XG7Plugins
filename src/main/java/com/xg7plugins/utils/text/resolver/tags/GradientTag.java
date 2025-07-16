package com.xg7plugins.utils.text.resolver.tags;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.resolver.Tag;
import com.xg7plugins.utils.text.resolver.TagResolver;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class GradientTag implements Tag {
    @Override
    public String name() {
        return "gradient";
    }

    @Override
    public void resolve(TextComponent component, List<String> openArgs, List<String> closeArgs) {

        if (MinecraftVersion.isOlderThan(16)) return;

        if (openArgs.size() != 1) {
            throw new IllegalArgumentException("Gradient tag must have 1 open arguments");
        }
        if (closeArgs.size() != 1) {
            throw new IllegalArgumentException("Gradient tag must have 1 close arguments");
        }

        String content = ChatColor.stripColor(component.toPlainText());

        String start = openArgs.get(0).replace("#", "");
        String end = closeArgs.get(0).replace("#", "");

        if (start.length() != 6 || end.length() != 6)
            throw new IllegalArgumentException("Start and end hex must have 6 characters");

        java.awt.Color from = java.awt.Color.decode("#" + start);
        java.awt.Color to = java.awt.Color.decode("#" + end);

        double[] red = linear(from.getRed(), to.getRed(), content.length());
        double[] green = linear(from.getGreen(), to.getGreen(), content.length());
        double[] blue = linear(from.getBlue(), to.getBlue(), content.length());

        component.setText("");
        component.getExtra().clear();

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            java.awt.Color color = new java.awt.Color(
                    (int) Math.round(red[i]),
                    (int) Math.round(green[i]),
                    (int) Math.round(blue[i])
            );

            TextComponent part = new TextComponent(String.valueOf(c));
            part.setColor(net.md_5.bungee.api.ChatColor.of(color));

            component.addExtra(part);
        }
    }
    private static double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }
}
