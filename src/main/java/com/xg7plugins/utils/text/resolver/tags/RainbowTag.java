package com.xg7plugins.utils.text.resolver.tags;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.text.resolver.Tag;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class RainbowTag implements Tag {
    @Override
    public String name() {
        return "rainbow";
    }

    @Override
    public void resolve(TextComponent component, List<String> openArgs) {

        String content = ChatColor.stripColor(component.toPlainText());

        StringBuilder rebuilt = new StringBuilder();

        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_16)) {
            ChatColor[] rainbowColors = new ChatColor[]{
                    ChatColor.RED,
                    ChatColor.GOLD,
                    ChatColor.YELLOW,
                    ChatColor.GREEN,
                    ChatColor.AQUA,
                    ChatColor.BLUE,
                    ChatColor.LIGHT_PURPLE
            };

            for (int i = 0; i < content.length(); i++) {
                char c = content.charAt(i);
                ChatColor color = rainbowColors[i % rainbowColors.length];

                rebuilt.append(color.toString()).append(c);
            }

            return;

        }
        float saturation = 1.0f;
        float brightness = 1.0f;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            float hue = (float) i / content.length();
            java.awt.Color color = java.awt.Color.getHSBColor(hue, saturation, brightness);

            rebuilt.append(net.md_5.bungee.api.ChatColor.of(color).toString()).append(c);
        }

        component.setText(rebuilt.toString());
    }
}
