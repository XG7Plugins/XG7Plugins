package com.xg7plugins.commands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

/**
 * Enum containing command-related message paths and utility methods for sending messages to command senders.
 * This enum manages common command messages like errors, permissions, and player-related notifications.
 */
@Getter
public enum CommandMessages {

    SYNTAX_ERROR("commands.syntax-error"),
    PLAYER_NOT_FOUND("commands.player-not-found"),
    NO_PERMISSION("commands.no-permission"),
    DISABLED_WORLD("commands.disabled-world"),
    NOT_A_PLAYER("commands.not-a-player"),
    IS_A_PLAYER("commands.is-a-player"),
    NOT_ONLINE("commands.not-online"),
    COMMAND_NOT_FOUND("commands.command-not-found");

    /**
     * The configuration path where the message text is stored in the language file.
     */
    private final String path;

    CommandMessages(String path) {
        this.path = path;
    }

    /**
     * Sends the message associated with this enum constant to the specified command sender.
     *
     * @param sender the recipient of the message
     * @return a CompletableFuture that completes when the message is sent
     */
    public CompletableFuture<Void> send(CommandSender sender) {
        return Text.sendTextFromLang(sender, XG7Plugins.getInstance(), path);
    }

    /**
     * Sends the message associated with this enum constant to the specified command sender,
     * replacing the syntax placeholder with the provided value.
     *
     * @param sender the recipient of the message
     * @param syntax the syntax string to replace in the message
     * @return a CompletableFuture that completes when the message is sent
     */
    public CompletableFuture<Void> send(CommandSender sender, String syntax) {
        return Text.sendTextFromLang(sender,XG7Plugins.getInstance(), path, Pair.of("syntax", syntax));
    }

}
