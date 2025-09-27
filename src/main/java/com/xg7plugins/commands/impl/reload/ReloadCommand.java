package com.xg7plugins.commands.impl.reload;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandSetup(
        name = "reload",
        description = "Reloads a plugin",
        permission = "xg7plugins.command.reload",
        syntax = "/xg7plugins reload (plugin) (cause)",
        pluginClass = XG7Plugins.class
)
public class ReloadCommand implements Command {

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() == 0) {
            XG7PluginsAPI.getAllXG7Plugins().forEach(plugin -> {
                Debug.of(XG7Plugins.getInstance()).info("Reloading " + plugin.getEnvironmentConfig().getCustomPrefix());
                plugin.onReload(ReloadCause.ALL);
            });
            Text.sendTextFromLang(sender,XG7Plugins.getInstance(), "reload-message.all-plugins");
            return;
        }
        Plugin plugin = args.get(0, Plugin.class);

        if (args.len() == 1) {
            if (plugin == null) return;
            Debug.of(XG7Plugins.getInstance()).info("Reloading " + plugin.getEnvironmentConfig().getCustomPrefix());
            plugin.onReload(ReloadCause.ALL);
            Text.sendTextFromLang(sender,XG7Plugins.getInstance(), "reload-message.without-cause", Pair.of("plugin", plugin.getEnvironmentConfig().getCustomPrefix()));

            return;
        }

        ReloadCause cause = ReloadCause.of(plugin, args.get(1,String.class));

        if (cause == null) return;

        Debug.of(XG7Plugins.getInstance()).info("Reloading " + plugin.getEnvironmentConfig().getCustomPrefix() + " with cause " + cause.getName());

        long msLoading = System.currentTimeMillis();

        plugin.getDebug().loading("Reloading: " + plugin.getName() + " with cause " + cause.getName() + "..." );

        plugin.onReload(cause);

        plugin.getDebug().loading(plugin.getName() + " reloaded in " + (System.currentTimeMillis() - msLoading) + "ms.");


        Text.sendTextFromLang(sender,XG7Plugins.getInstance(), "reload-message.with-cause",
                Pair.of("plugin", plugin.getEnvironmentConfig().getPrefix()),
                Pair.of("cause", cause.getName())
        );

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        if (args.len() == 1) return new ArrayList<>(XG7PluginsAPI.getAllXG7PluginsNames());

        if (args.len() == 2) {

            Plugin plugin = args.get(0,Plugin.class);

            if (plugin == null) return Collections.emptyList();

            List<String> completions = new ArrayList<>(Arrays.asList("all", "database", "langs", "events", "config", "tasks"));

            List<ReloadCause> causes = ReloadCause.getCausesOf(plugin);

            if (causes != null) completions.addAll(causes.stream().map(ReloadCause::getName).collect(Collectors.toList()));

            return completions;
        }

        return Command.super.onTabComplete(sender, args);
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.FEATHER,this);
    }
}
