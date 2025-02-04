package com.xg7plugins.commands.defaultCommands.reloadCommand;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.defaultCommands.reloadCommand.subcommands.*;
import com.xg7plugins.commands.setup.*;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.libs.xg7menus.item.Item;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

@Command(
        name = "reload",
        description = "Reloads the plugin",
        syntax = "/xg7plugins reload <jsoncache or [config, lang, database, tasks, events, all]> (plugin)",
        permission = "xg7plugins.command.reload"
)
public class ReloadCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }


    private final ICommand[] subCommands = new ICommand[]{new JsonSubCommand(), new ConfigSubCommand(), new TaskSubCommand(), new LangSubCommand(), new DatabaseSubCommand(), new EventsSubCommand(), new AllSubCommand()};

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.STONE_BUTTON, this);
    }
    @Override
    public ICommand[] getSubCommands() {
        return subCommands;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        List<String> suggestions = new ArrayList<>();

        if (args.len() == 1) {
            suggestions.add("config");
            suggestions.add("lang");
            suggestions.add("database");
            suggestions.add("events");
            suggestions.add("tasks");
            suggestions.add("all");
            suggestions.add("invalidatejsoncache");
        }
        if (args.len() == 2) suggestions.addAll(XG7Plugins.getInstance().getPlugins().keySet().stream().filter(s -> !s.equals("XG7Plugins")).collect(Collectors.toList()));
        return suggestions;
    }


}
