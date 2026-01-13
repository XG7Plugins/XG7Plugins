package com.xg7plugins.utils.textattempt.sender;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.textattempt.Text;
import net.kyori.adventure.text.Component;
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
            System.out.println("SENDING: " + line.getTextRaw());
            System.out.println("COMPONENT: " + line.getComponent());

            try {
                ReflectionObject.of(sender, CommandSender.class).getMethod("sendRichMessage", String.class).invoke(line.getTextRaw());
            } catch (Exception ignored) {
                ignored.printStackTrace();

                if (!(sender instanceof Player)) {
                    XG7Plugins.getAPI().getAdventure().sender(sender).sendMessage(line.getComponent());
                    return;
                }

                XG7Plugins.getAPI().getAdventure().player((Player) sender).sendMessage(line.getComponent());
            }
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
