package com.xg7plugins.commands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

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

    private final String path;

    CommandMessages(String path) {
        this.path = path;
    }

    public CompletableFuture<Void> send(CommandSender sender) {
        return Text.fromLang(sender, XG7Plugins.getInstance(),path).thenAccept(t -> t.send(sender));
    }
    public CompletableFuture<Void> send(CommandSender sender, String syntax) {
        return Text.fromLang(sender,XG7Plugins.getInstance(),path).thenAccept(t -> t.replace("syntax", syntax).send(sender));
    }

    public static CompletableFuture<Void> send(CommandSender sender, Plugin plugin, String path) {
        return Text.fromLang(sender, plugin, path).thenAccept(t -> t.send(sender));
    }

}
