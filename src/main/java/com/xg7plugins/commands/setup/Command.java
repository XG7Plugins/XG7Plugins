package com.xg7plugins.commands.setup;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.node.CommandNode;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a command in the plugin system.
 * This interface provides the basic structure for implementing commands,
 * including sub-commands support, command execution, tab completion, and configuration.
 */
public interface Command {

    /**
     * Provides tab completion suggestions for the command.
     *
     * @param sender The command sender requesting tab completion
     * @param args   The current command arguments
     * @return A list of tab completion suggestions, empty by default
     */
    default List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        CommandManager manager = XG7Plugins.getInstance().getManagerRegistry().getManager(CommandManager.class);

        CommandNode chosen = manager.getRootCommandNode(getPlugin().getPluginSetup().mainCommandName() + getCommandSetup().name());

        for (int i = 0; i < args.len(); i++) {
            String sub = args.get(i, String.class);
            CommandNode child = chosen.getChild(sub);

            if (child == null) break;

            chosen = child;
        }

        return chosen.getMappedChildren().entrySet().stream()
                .filter(c -> sender.hasPermission(c.getValue().getCommandMethod().getAnnotation(CommandConfig.class).permission()) || sender.hasPermission("xg7plugins.command.anti-tab-bypass"))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

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
