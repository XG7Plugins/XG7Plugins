package com.xg7plugins.utils.text.resolver.tags;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.text.resolver.Tag;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.awt.*;
import java.util.List;
import java.util.function.ToIntFunction;

public class GradientTag implements Tag {
    @Override
    public String name() {
        return "gradient";
    }

    @Override
    public void resolve(TextComponent component, List<String> openArgs) {

        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_16)) return;

        if (openArgs.size() < 2) {
            throw new IllegalArgumentException("Gradient tag must have at least 2 colors");
        }

        String content = ChatColor.stripColor(component.toPlainText());

        Color[] colors = new Color[openArgs.size()];
        for (int i = 0; i < openArgs.size(); i++) {
            String colorHex = openArgs.get(i).replace("#", "");
            if (colorHex.length() != 6) {
                throw new IllegalArgumentException("Color hex must have 6 characters: " + openArgs.get(i));
            }
            colors[i] = Color.decode("#" + colorHex);
        }

        double[] red = multiColorLinear(colors, Color::getRed, content.length());
        double[] green = multiColorLinear(colors, Color::getGreen, content.length());
        double[] blue = multiColorLinear(colors, Color::getBlue, content.length());

        StringBuilder rebuilt = new StringBuilder();


        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            Color color = new Color(
                    (int) Math.round(red[i]),
                    (int) Math.round(green[i]),
                    (int) Math.round(blue[i])
            );

            rebuilt.append(ChatColor.of(color).toString()).append(c);
        }

        component.setText(rebuilt.toString());
    }

    private double[] multiColorLinear(Color[] colors, ToIntFunction<Color> componentExtractor, int steps) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("Must have at least 2 colors");
        }

        if (steps <= 1) {
            return new double[]{componentExtractor.applyAsInt(colors[0])};
        }

        double[] result = new double[steps];

        int segments = colors.length - 1;

        double stepsPerSegment = (double) (steps - 1) / segments;

        for (int i = 0; i < steps; i++) {
            double position = i / stepsPerSegment;
            int segmentIndex = Math.min((int) position, segments - 1);

            double segmentPosition = position - segmentIndex;

            Color startColor = colors[segmentIndex];
            Color endColor = colors[segmentIndex + 1];

            int startValue = componentExtractor.applyAsInt(startColor);
            int endValue = componentExtractor.applyAsInt(endColor);

            result[i] = startValue + (endValue - startValue) * segmentPosition;
        }

        return result;
    }
}
