package com.xg7plugins.utils.text.sender;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface TextSender {

    void send(CommandSender sender, Text text);

    default void defaultSend(CommandSender sender, Text text) {
        if (MinecraftVersion.isOlderThan(8) || !(sender instanceof Player)) {
            sender.sendMessage(text.getPlainText());
            return;
        }

        System.out.println("Enviando componente: " + text.getComponent());

        Text.getAudience().sender(sender).sendMessage(text.getComponent());
    }



    static TextSender defaultSender() {
        return new TextSender() {
            @Override
            public void send(CommandSender sender, Text text) {
                defaultSend(sender, text);
            }
        };
    }

}
