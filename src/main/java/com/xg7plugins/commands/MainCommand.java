package com.xg7plugins.commands;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.PluginSetup;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.modules.xg7menus.item.Item;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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


    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() > 1){
            plugin.getHelpMessenger().sendChat(sender, args.get(1, String.class));
            return;
        }
        plugin.getHelpMessenger().send(sender);
     }

    @Override
    public Item getIcon() {
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        List<String> suggestions = new ArrayList<>();
        if (args.len() == 1) {
            suggestions.add("help");
            suggestions.addAll(XG7PluginsAPI.commandManager(plugin).getCommands().entrySet().stream().filter(cmd -> !cmd.getKey().isEmpty()).map(cmd -> {
                PluginSetup configurations = cmd.getValue().getPlugin().getConfigurations();
                return cmd.getKey().replace(configurations.mainCommandName(), "");
            }).collect(Collectors.toList()));
            return suggestions;
        }

        if (args.len() == 2 && args.get(0, String.class).equalsIgnoreCase("help")) {
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
