package com.xg7plugins.utils.text.sender;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface TextSender {

    void send(CommandSender sender, Text text);

    default void defaultSend(CommandSender sender, Text text) {
        if (text == null || text.getText() == null || text.getText().isEmpty()) return;
        if (MinecraftVersion.isOlderThan(8)) {
            sender.sendMessage(text.getPlainText());
            return;
        }

        

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
