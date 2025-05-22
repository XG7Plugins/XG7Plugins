package com.xg7plugins.utils.text.newComponent.sender;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.newComponent.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface TextSender {

    void send(CommandSender sender, TextComponent component);

    default void defaultSend(CommandSender sender, TextComponent component) {
        if (MinecraftVersion.isOlderOrEqual(8) || !(sender instanceof Player)) {
            sender.sendMessage(component.getText());
            return;
        }
        sender.spigot().sendMessage(component.toBukkitComponent());
        return;
    }

    static TextSender defaultSender() {
        return new TextSender() {
            @Override
            public void send(CommandSender sender, TextComponent component) {
                defaultSend(sender, component);
            }
        };
    }

}
