package com.xg7plugins.utils.text.sender;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Interface for sending text messages to command senders.
 * Provides functionality to send formatted text messages to players and console.
 */
public interface TextSender {

    /**
     * Sends a text message to a command sender
     *
     * @param sender The receiver of the message
     * @param text   The text content to send
     */
    void send(CommandSender sender, Text text);

    void apply(CommandSender sender, Text text);

    /**
     * Default implementation for sending text messages
     *
     * @param sender The receiver of the message
     * @param text   The text content to send
     */
    default void defaultSend(CommandSender sender, Text text) {
        if (text == null || text.getText() == null || text.getText().isEmpty()) return;

        try {
            ReflectionObject.of(sender, CommandSender.class).getMethod("sendRichMessage", String.class).invoke(text.getTextRaw());
            return;
        } catch (Exception ignored) {

        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.spigot().sendMessage(BungeeComponentSerializer.get().serialize(text.getComponent()));
            return;
        }

        sender.sendMessage(text.getText());
    }

    /**
     * Creates a default TextSender implementation
     *
     * @return A new TextSender instance using the default implementation
     */
    static TextSender defaultSender() {
        return new TextSender() {
            @Override
            public void send(CommandSender sender, Text text) {
                defaultSend(sender, text);
            }

            @Override
            public void apply(CommandSender sender, Text text) {
            }
        };
    }

}
