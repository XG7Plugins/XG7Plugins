package com.xg7plugins.commands.defaultCommands.reloadCommand;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "reload", description = "Reloads a plugin", syntax = "/xg7plugins reload (plugin) (cause)")
public class ReloadCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() == 0) {
            XG7Plugins.getInstance().getPlugins().values().forEach(plugin -> {
                Debug.of(XG7Plugins.getInstance()).info("Reloading " + plugin.getPrefix());
                plugin.onReload(ReloadCause.ALL);
            });
            Text.fromLang(sender,XG7Plugins.getInstance(), "reload-message.all-plugins").join().send(sender);
            return;
        }
        Plugin plugin = args.get(0, Plugin.class);

        if (args.len() == 1) {
            if (plugin == null) return;
            Debug.of(XG7Plugins.getInstance()).info("Reloading " + plugin.getPrefix());
            plugin.onReload(ReloadCause.ALL);
            Text.fromLang(sender,XG7Plugins.getInstance(), "reload-message.without-cause").join().replace("plugin", plugin.getPrefix()).send(sender);

            return;
        }

        ReloadCause cause = ReloadCause.of(plugin, args.get(1,String.class));

        if (cause == null) {
            return;
        }

        Debug.of(XG7Plugins.getInstance()).info("Reloading " + plugin.getPrefix() + " with cause " + cause.getName());

        plugin.onReload(cause);

        Text.fromLang(sender,XG7Plugins.getInstance(), "reload-message.with-cause").join().replace("plugin", plugin.getPrefix()).replace("cause", cause.getName()).send(sender);

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        if (args.len() == 1) {
            return new ArrayList<>(XG7Plugins.getInstance().getPlugins().keySet());
        }
        if (args.len() == 2) {

            Plugin plugin = args.get(0,Plugin.class);

            if (plugin == null) return Collections.emptyList();

            List<String> completions = new ArrayList<>();

            completions.add("all");
            completions.add("database");
            completions.add("langs");
            completions.add("events");
            completions.add("config");
            completions.add("tasks");

            List<ReloadCause> causes = ReloadCause.getCausesOf(plugin);

            if (causes != null) completions.addAll(causes.stream().map(ReloadCause::getName).collect(Collectors.toList()));

            return completions;
        }

        return ICommand.super.onTabComplete(sender, args);
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.FEATHER,this);
    }
}
