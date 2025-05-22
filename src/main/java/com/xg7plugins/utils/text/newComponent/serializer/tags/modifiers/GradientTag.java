package com.xg7plugins.utils.text.newComponent.serializer.tags.modifiers;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.newComponent.Component;
import com.xg7plugins.utils.text.newComponent.modfiers.color.GradientModifier;
import com.xg7plugins.utils.text.newComponent.modfiers.color.GradientStyle;
import com.xg7plugins.utils.text.newComponent.serializer.tags.TagType;
import com.xg7plugins.utils.text.newComponent.serializer.tags.TextTag;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GradientTag implements TextTag {
    @Override
    public String name() {
        return "gradient";
    }

    @Override
    public TagType getType() {
        return TagType.MODIFIER;
    }

    @Override
    public void resolve(Component component, List<String> openArgs, List<String> closeArgs) {
        if (MinecraftVersion.isOlderThan(16)) return;

        if (openArgs.isEmpty()) {
            throw new IllegalArgumentException("Gradient tag must have at least 1 open arguments");
        }
        if (closeArgs.isEmpty()) {
            throw new IllegalArgumentException("Gradient tag must have at least 1 close arguments");
        }

        GradientStyle style = GradientStyle.LINEAR;

        try {
            style = GradientStyle.valueOf(openArgs.get(0).toUpperCase());
        } catch (IllegalArgumentException ignored) {}

        List<Color> colors = Stream.concat(
                        openArgs.stream().skip(1), // Pula o primeiro elemento de openArgs
                        closeArgs.stream()
                )
                .map(hex-> {
                    String cleanHex = hex.startsWith("#") ? hex.substring(1) : hex;

                    if (cleanHex.length() != 6) throw new IllegalArgumentException(String.format("Invalid hex color '%s': must have exactly 6 characters", hex));

                    if (!cleanHex.matches("[0-9A-Fa-f]{6}")) throw new IllegalArgumentException(String.format("Invalid hex color '%s': must contain only hexadecimal characters", hex));

                    return cleanHex.toUpperCase();
                })
                .map(hex-> {
                    int r = Integer.parseInt(hex.substring(0, 2), 16);
                    int g = Integer.parseInt(hex.substring(2, 4), 16);
                    int b = Integer.parseInt(hex.substring(4, 6), 16);
                    return new Color(r, g, b);
                })
                .collect(Collectors.toList());

        component.addModifier(GradientModifier.of(colors, style));

    }
}
