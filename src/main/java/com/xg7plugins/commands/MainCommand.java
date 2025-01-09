package com.xg7plugins.commands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.libs.xg7menus.item.Item;
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
        syntax = "/xg7plugins (command)"
)
public class MainCommand implements ICommand {

    private final Plugin plugin;


    public void onCommand(CommandSender sender, CommandArgs args) {
        Config config = XG7Plugins.getInstance().getConfig("config");

        if (args.len() > 1) {
            plugin.getHelpInChat().sendPage(args.get(1, String.class), sender);
            return;
        }

        if (!(sender instanceof Player)) {
            plugin.getHelpInChat().sendPage("index", sender);
            return;
        }

        Player player = (Player) sender;

        if (XG7Plugins.isGeyserFormEnabled()) {
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
            suggestions.addAll(XG7Plugins.getInstance().getCommandManager().getCommands().keySet().stream().map(s -> s.replace("xg7plugins", "")).collect(Collectors.toList()));
            return suggestions;
        }

        if (args.len() == 2 && args.get(0, String.class).equalsIgnoreCase("help")) {
            suggestions.addAll(XG7Plugins.getInstance().getHelpInChat().getPages().keySet());
            return suggestions;
        }

        return Collections.emptyList();
    }
}
