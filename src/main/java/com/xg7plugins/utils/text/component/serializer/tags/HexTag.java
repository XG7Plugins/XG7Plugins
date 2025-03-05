package com.xg7plugins.utils.text.component.serializer.tags;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.serializer.Tag;

import java.util.List;

public class HexTag implements Tag {
    @Override
    public String name() {
        return "hex";
    }

    @Override
    public void resolve(Component content, List<String> openArgs, List<String> closeArgs) {

        if (MinecraftVersion.isOlderThan(16)) return;

        if (openArgs.size() != 1) {
            throw new IllegalArgumentException("Hex tag must have 1 open arguments");
        }

        String contentText = content.content();

        String hex = openArgs.get(0);

        hex = hex.replace("#", "");

        if (hex.length() != 6) {
            throw new IllegalArgumentException("Hex tag must have 6 characters in the open arguments");
        }

        contentText = contentText.replace(hex, net.md_5.bungee.api.ChatColor.of(hex) + "");

        content.setText(contentText);

    }

}
