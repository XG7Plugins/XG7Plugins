package com.xg7plugins.utils.text.sender;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.core.MainConfigSection;
import com.xg7plugins.server.MinecraftVersion;
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

        if (MinecraftVersion.isOlderThan(8)) {
            sender.sendMessage(text.getText());
            return;
        }

        if (MinecraftVersion.is(8) && !(sender instanceof Player)) {
            sender.sendMessage(text.getText());
            return;
        }

        ((Player) sender).spigot().sendMessage(text.getComponent());
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
