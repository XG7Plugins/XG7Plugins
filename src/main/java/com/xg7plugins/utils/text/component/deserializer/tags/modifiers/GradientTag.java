package com.xg7plugins.utils.text.component.deserializer.tags.modifiers;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.modfiers.color.GradientModifier;
import com.xg7plugins.utils.text.component.modfiers.color.GradientStyle;
import com.xg7plugins.utils.text.component.deserializer.tags.TagType;
import com.xg7plugins.utils.text.component.deserializer.tags.TextTag;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

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
    public void resolve(Component component, List<String> args) {
        if (MinecraftVersion.isOlderThan(16)) return;

        if (args.size() < 2) {
            throw new IllegalArgumentException("Gradient tag must have at least 2 arguments");
        }

        GradientStyle style = GradientStyle.LINEAR;

        boolean failed = false;

        try {
            style = GradientStyle.valueOf(args.get(0).toUpperCase());
        } catch (IllegalArgumentException ignored) {
            failed = true;
        }

        List<Color> colors = args.stream().skip(failed ? 0 : 1)
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
