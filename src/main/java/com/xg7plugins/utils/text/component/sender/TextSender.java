package com.xg7plugins.utils.text.component.sender;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.component.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface TextSender {

    void send(CommandSender sender, TextComponent component);

    default void defaultSend(CommandSender sender, TextComponent component) {
        if (MinecraftVersion.isOlderThan(8) || !(sender instanceof Player)) {
            sender.sendMessage(component.getText());
            return;
        }
        ((Player)sender).spigot().sendMessage(component.toBukkitComponent());
    }

    String serialize();

    static TextSender defaultSender() {
        return new TextSender() {
            @Override
            public void send(CommandSender sender, TextComponent component) {
                defaultSend(sender, component);
            }

            @Override
            public String serialize() {
                return "default";
            }
        };
    }

}
