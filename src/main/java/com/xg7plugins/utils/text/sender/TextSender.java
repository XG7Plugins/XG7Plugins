package com.xg7plugins.utils.text.sender;

import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.Text;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import me.clip.placeholderapi.libs.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface TextSender {

    void send(CommandSender sender, Text text);

    default void defaultSend(CommandSender sender, Text text) {
        if (MinecraftVersion.isOlderThan(8) || !(sender instanceof Player)) {
            sender.sendMessage(text.getPlainText());
            return;
        }
        Text.getAudience().sender(sender).sendMessage((ComponentLike) text.getComponent());
    }

    String serialize();

    static TextSender defaultSender() {
        return new TextSender() {
            @Override
            public void send(CommandSender sender, Text text) {
                defaultSend(sender, text);
            }

            @Override
            public String serialize() {
                return "default";
            }
        };
    }

}
