package com.xg7plugins.commands.executors;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.commands.node.CommandConfig;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The main command handler for all XG7Plugins.
 * <p>
 * This class serves as the entry point for all plugin commands.
 * It acts as a dynamic dispatcher for subcommands.
 * <p>
 * Example usage: <code>/[main-command-name] [subcommand]</code>
 */
@AllArgsConstructor
@CommandSetup(
        name = "",
        description = "",
        syntax = "/xg7plugins (command)",
        permission = "xg7plugins.command",
        pluginClass = Plugin.class
)
public class MainCommand implements Command {

    private final Plugin plugin;

    @CommandConfig
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() > 1){
            plugin.getHelpMessenger().sendChat(sender, args.get(1, String.class));
            return CommandState.FINE;
        }
        plugin.getHelpMessenger().send(sender);

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        boolean antiTab = ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("anti-tab");

        List<String> suggestions = new ArrayList<>();
        if (args.len() == 1) {
            suggestions.addAll(XG7PluginsAPI.commandManager(plugin)
                    .getCommandList().stream()
                    .filter(cmd -> sender.hasPermission(cmd.getCommandSetup().permission()) || sender.hasPermission("xg7plugins.command.anti-tab-bypass") && antiTab)
                    .map(cmd -> cmd.getCommandSetup().name())
                    .collect(Collectors.toList()));
            if (sender.hasPermission("xg7plugins.command.help") || sender.hasPermission("xg7plugins.command.anti-tab-bypass") && antiTab) {
                suggestions.add("help");
            }
            return suggestions;
        }

        if (args.len() == 2 && args.get(0, String.class).equalsIgnoreCase("help") && (sender.hasPermission("xg7plugins.command.help") || sender.hasPermission("xg7plugins.command.anti-tab-bypass") && antiTab)) {
            suggestions.addAll(plugin.getHelpMessenger().getChat().getPages().keySet());
            return suggestions;
        }

        return Collections.emptyList();
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
