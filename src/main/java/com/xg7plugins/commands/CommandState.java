package com.xg7plugins.commands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
@Getter
public class CommandState {

    public static final CommandState FINE = new CommandState(false, XG7Plugins.getInstance(), "fine", null);
    public static final CommandState ERROR = new CommandState(false, XG7Plugins.getInstance(), "error", null);

    public static final CommandState PLAYER_NOT_FOUND = new CommandState(true, XG7Plugins.getInstance(), "player-not-found", null);
    public static final CommandState NO_PERMISSION = new CommandState(true, XG7Plugins.getInstance(), "no-permission", null);
    public static final CommandState DISABLED_WORLD = new CommandState(true, XG7Plugins.getInstance(), "disabled-world", null);
    public static final CommandState NOT_A_PLAYER = new CommandState(true, XG7Plugins.getInstance(), "not-a-player", null);
    public static final CommandState IS_A_PLAYER = new CommandState(true, XG7Plugins.getInstance(), "is-a-player", null);
    public static final CommandState NOT_ONLINE = new CommandState(true, XG7Plugins.getInstance(), "not-online", null);
    public static final CommandState COMMAND_NOT_FOUND = new CommandState(true, XG7Plugins.getInstance(), "command-not-found", null);

    private final boolean error;
    private final Plugin origin;
    private final String errorPath;
    private final Pair<String, String>[] additionalReplacements;

    public void send(CommandSender sender) {
        if (!error) return;
        Text.sendTextFromLang(sender, origin, "command-errors." + errorPath, additionalReplacements);
    }

    public static CommandState fine() {
        return CommandState.FINE;
    }

    @SafeVarargs
    public static CommandState error(String errorPath, Pair<String, String>... additionalReplacements) {
        return new CommandState(true, XG7Plugins.getInstance(), errorPath, additionalReplacements);
    }

    @SafeVarargs
    public static CommandState error(Plugin origin, String errorPath, Pair<String, String>... additionalReplacements) {
        return new CommandState(true, origin, errorPath, additionalReplacements);
    }

    public static CommandState syntaxError(String syntax) {
        return error("syntax-error", Pair.of("syntax", syntax));
    }

    public static CommandState typeError(String type) {
        return  error("illegal-type", Pair.of("type", type));
    }

    @Override
    public String toString() {
        return errorPath;
    }


}
