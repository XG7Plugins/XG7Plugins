package com.xg7plugins.commands.utils;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;

/**
 * Represents the state of a command execution, indicating success or specific error conditions.
 */
@AllArgsConstructor
@Getter
public class CommandState {

    // Predefined CommandState instances for common scenarios

    public static final CommandState FINE = new CommandState(false, XG7Plugins.getInstance(), "fine", null);
    public static final CommandState ERROR = new CommandState(true, XG7Plugins.getInstance(), "internal-error", null);

    public static final CommandState SYNTAX_ERROR = new CommandState(false, XG7Plugins.getInstance(), "generic-syntax-error", null);

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

    /**
     * Sends the error message associated with this command state to the specified sender.
     * @param sender The command sender to whom the error message will be sent.
     */
    public void send(CommandSender sender) {
        if (!error) return;
        Text.sendTextFromLang(sender, origin, "command-errors." + errorPath, additionalReplacements);
    }

    /**
     * Creates a CommandState representing a successful command execution.
     * @return A CommandState indicating success.
     */
    public static CommandState fine() {
        return CommandState.FINE;
    }

    /**
     * Creates a CommandState representing an error with the specified error path and additional replacements.
     * @param errorPath The config path to the error message.
     * @param additionalReplacements Additional placeholders and their replacements for the error message.
     * @return A CommandState indicating an error.
     */
    @SafeVarargs
    public static CommandState error(String errorPath, Pair<String, String>... additionalReplacements) {
        return new CommandState(true, XG7Plugins.getInstance(), errorPath, additionalReplacements);
    }

    /**
     * Creates a CommandState representing an error originating from a specific plugin with the specified error path and additional replacements.
     * @param origin The plugin where the error originated.
     * @param errorPath The config path to the error message.
     * @param additionalReplacements Additional placeholders and their replacements for the error message.
     * @return A CommandState indicating an error.
     */
    @SafeVarargs
    public static CommandState error(Plugin origin, String errorPath, Pair<String, String>... additionalReplacements) {
        return new CommandState(true, origin, errorPath, additionalReplacements);
    }

    /**
     * Creates a CommandState representing a syntax error with the specified syntax.
     * @param syntax The correct syntax for the command.
     * @return A CommandState indicating a syntax error.
     */
    public static CommandState syntaxError(String syntax) {
        return error("syntax-error", Pair.of("syntax", syntax));
    }

    /**
     * Creates a CommandState representing a type error with the specified type.
     * @param type The expected type.
     * @return A CommandState indicating a type error.
     */
    public static CommandState typeError(String type) {
        return  error("illegal-type", Pair.of("type", type));
    }

    @Override
    public String toString() {
        return errorPath;
    }


}
