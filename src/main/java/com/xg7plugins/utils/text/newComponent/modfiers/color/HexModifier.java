package com.xg7plugins.utils.text.newComponent.modfiers.color;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.newComponent.Component;
import com.xg7plugins.utils.text.newComponent.modfiers.TextModifier;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
public class HexModifier implements TextModifier {

    private final String hex;

    public HexModifier(@NotNull String hex) {

        hex = hex.replace("#", "");

        if (hex.length() != 6) {
            throw new IllegalArgumentException("Hex tag must have 6 characters");
        }

        this.hex = hex;
    }

    @Override
    public void apply(Component component) {
        if (MinecraftVersion.isOlderThan(16)) return;

        String contentText = component.getContent();

        contentText = contentText.replace(hex, net.md_5.bungee.api.ChatColor.of(hex) + "");

        component.setContent(contentText);
    }

    public static HexModifier of(@NotNull String hex) {
        return new HexModifier(hex);
    }
}
