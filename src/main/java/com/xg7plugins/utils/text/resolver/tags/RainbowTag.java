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

        component.setText("");
        component.getExtra().clear();

        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_16)) {
            // Cores padrões de arco-íris (limitadas ao ChatColor)
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

                TextComponent part = new TextComponent(String.valueOf(c));
                part.setColor(color);
                component.addExtra(part);
            }

            return;

        }
        float saturation = 1.0f;
        float brightness = 1.0f;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            float hue = (float) i / content.length();
            java.awt.Color color = java.awt.Color.getHSBColor(hue, saturation, brightness);

            TextComponent part = new TextComponent(String.valueOf(c));
            part.setColor(net.md_5.bungee.api.ChatColor.of(color));
            component.addExtra(part);
        }
    }
}
