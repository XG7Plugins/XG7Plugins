package com.xg7plugins.commands.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.api.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.loader.VersionChecker;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandSetup(
        name = "update",
        description = "Updates a plugin",
        syntax = "/xg7plugins update <check|(plugin)> (check:plugin)",
        permission = "xg7plugins.command.update",
        iconMaterial = XMaterial.ANVIL,
        pluginClass = XG7Plugins.class
)
public class UpdateCommand implements Command {

    @CommandConfig
    public CommandState onCommand(CommandSender sender, CommandArgs commandArgs) {
        if (commandArgs.len() == 0) {
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "plugin-checking-updates-all");

            XG7Plugins.getAPI().getAllXG7Plugins().forEach(plugin -> update(sender, plugin));
            return CommandState.FINE;
        }

        Plugin plugin = commandArgs.get(0, Plugin.class);

        if (plugin == null) {
            return CommandState.error("plugin-not-found");
        }

        Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "plugin-checking-updates", Pair.of("plugin", plugin.getPrefix()));

        update(sender, plugin);

        return CommandState.FINE;
    }

    private void update(CommandSender sender, Plugin plugin) {
        XG7Plugins.getAPI().getVersionChecker().updatePlugin(plugin).thenAccept(result -> {

            Text.sendTextFromLang(
                    sender,
                    XG7Plugins.getInstance(),
                    "plugin-update-" + (result.getFirst() == VersionChecker.UpdateSate.NO_UPDATE ? "no-update"
                            : result.getFirst()  == VersionChecker.UpdateSate.SUCCESS ? "success" : "failed"),
                    Pair.of("plugin", plugin.getPrefix()), Pair.of("version", result.getSecond())
            );

        });
    }

    @CommandConfig(name = "check", syntax = "/xg7plugins update check (plugin)", description = "Checks for updates for all plugins or a specific plugin", iconMaterial = XMaterial.DIAMOND)
    public CommandState onCheckCommand(CommandSender sender, CommandArgs commandArgs) {

        Plugin plugin = null;
        if (commandArgs.len() >= 1) {
            plugin = commandArgs.get(0, Plugin.class);

            if (plugin == null) {
                return CommandState.error("plugin-not-found");
            }
        }

        if (plugin == null) {
            XG7Plugins.getAPI().getVersionChecker().notify(Collections.singletonList(sender), XG7Plugins.getAPI().getAllXG7Plugins());
            return CommandState.FINE;
        }

        XG7Plugins.getAPI().getVersionChecker().notify(Collections.singletonList(sender), Collections.singleton(plugin));

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return Collections.emptyList();
        }

        if (!sender.hasPermission("xg7plugins.command.update")) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();

        completions.addAll(Command.super.onTabComplete(sender, args));
        completions.addAll(XG7Plugins.getAPI().getAllXG7PluginsNames());

        return completions;
    }


}
