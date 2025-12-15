package com.xg7plugins.utils.text.sender;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        text.split("<br>").forEach(line -> {
            if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_8)) {
                sender.sendMessage(line.getText());
                return;
            }

            if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_9) && !(sender instanceof Player)) {
                sender.sendMessage(line.getText());
                return;
            }
            if (!(sender instanceof Player)) {
                sender.spigot().sendMessage(line.getComponent());
                return;
            }

            ((Player) sender).spigot().sendMessage(line.getComponent());
        });
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
