package com.xg7plugins.utils.text.resolver.tags;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.resolver.Tag;
import com.xg7plugins.utils.text.resolver.TagResolver;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class HexTag implements Tag {
    @Override
    public String name() {
        return "hex";
    }

    @Override
    public void resolve(TextComponent component, List<String> openArgs, List<String> closeArgs) {

        if (MinecraftVersion.isOlderThan(16)) return;

        if (openArgs.size() != 1) {
            throw new IllegalArgumentException("Hex tag must have 1 open arguments");
        }

        String content = ChatColor.stripColor(component.toPlainText());
        String hex = openArgs.get(0);

        hex = hex.replace("#", "");

        if (hex.length() != 6) {
            throw new IllegalArgumentException("Hex tag must have 6 characters in the open arguments");
        }

        ChatColor hexColor = ChatColor.of("#" + hex);

        component.getExtra().clear();

        component.setColor(hexColor);
        component.setText(content);
    }

}
