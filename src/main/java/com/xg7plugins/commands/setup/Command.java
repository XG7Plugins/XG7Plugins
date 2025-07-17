package com.xg7plugins.commands.setup;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a command in the plugin system.
 * This interface provides the basic structure for implementing commands,
 * including sub-commands support, command execution, tab completion, and configuration.
 */
public interface Command {

    /**
     * Gets the list of sub-commands associated with this command.
     *
     * @return A list of sub-commands, empty by default
     */
    default List<Command> getSubCommands() {
        return new ArrayList<>();
    }

    /**
     * Handles the execution of the command.
     * By default, sends a syntax error message to the sender.
     *
     * @param sender The command sender
     * @param args   The command arguments
     */
    default void onCommand(CommandSender sender, CommandArgs args) {
        CommandMessages.SYNTAX_ERROR.send(sender, getCommandSetup().syntax());
    }

    /**
     * Provides tab completion suggestions for the command.
     *
     * @param sender The command sender requesting tab completion
     * @param args   The current command arguments
     * @return A list of tab completion suggestions, empty by default
     */
    default List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (args.len() == 1 && getSubCommands() != null && !getSubCommands().isEmpty()) {

            return getSubCommands().stream()
                        .map(Command::getCommandSetup)
                        .filter(commandConfigurations -> sender.hasPermission(commandConfigurations.permission()) || sender.hasPermission("xg7plugins.command.anti-tab-bypass"))
                        .map(CommandSetup::name)
                        .collect(Collectors.toList()
                    );
        }

        return Collections.emptyList();
    }

    /**
     * Gets the icon representation of this command for help systems.
     *
     * @return The item representing this command's icon
     */
    Item getIcon();

    /**
     * Retrieves the command configuration from the CommandSetup annotation.
     *
     * @return The command configuration settings
     */
    default CommandSetup getCommandSetup() {
        return getClass().getAnnotation(CommandSetup.class);
    }

    /**
     * Gets the plugin instance that this command belongs to.
     *
     * @return The plugin instance
     */
    default Plugin getPlugin() {
        return XG7PluginsAPI.getXG7Plugin(getCommandSetup().pluginClass());
    }
}
