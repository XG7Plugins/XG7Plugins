package com.xg7plugins.commands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.PluginConfigurations;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
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
@Command(
        name = "",
        description = "",
        syntax = "/xg7plugins (command)",
        permission = "xg7plugins.command",
        pluginClass = Plugin.class
)
public class MainCommand implements ICommand {

    private final Plugin plugin;


    public void onCommand(CommandSender sender, CommandArgs args) {
        Config config = XG7Plugins.getInstance().getConfig("config");

        if (args.len() > 1){
            plugin.getHelpInChat().sendPage(args.get(1, String.class), sender);
            return;
        }

        if (!(sender instanceof Player)) {
            plugin.getHelpInChat().sendPage("index", sender);
            return;
        }

        Player player = (Player) sender;

        if (XG7Plugins.isDependencyEnabled("floodgate") && Config.mainConfigOf(XG7Plugins.getInstance()).get("enable-geyser-forms",Boolean.class).orElse(false)) {
            boolean commandFormEnabled = config.get("help-command-form", Boolean.class).orElse(false);
            if (commandFormEnabled && FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                plugin.getHelpCommandForm().getForm("index").send(player);
                return;
            }
        }

        if (config.get("help-command-in-gui", Boolean.class).orElse(false)) {
            plugin.getHelpCommandGUI().getMenu("index").open(player);
            return;
        }

        plugin.getHelpInChat().sendPage("index", player);
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
            suggestions.addAll(plugin.getCommandManager().getCommands().entrySet().stream().filter(cmd -> !cmd.getKey().isEmpty()).map(cmd -> {
                PluginConfigurations configurations = cmd.getValue().getCommandsConfigurations().pluginClass().getAnnotation(PluginConfigurations.class);
                return cmd.getKey().replace(configurations.mainCommandName(), "");
            }).collect(Collectors.toList()));
            return suggestions;
        }

        if (args.len() == 2 && args.get(0, String.class).equalsIgnoreCase("help")) {
            suggestions.addAll(plugin.getHelpInChat().getPages().keySet());
            return suggestions;
        }

        return Collections.emptyList();
    }
}
