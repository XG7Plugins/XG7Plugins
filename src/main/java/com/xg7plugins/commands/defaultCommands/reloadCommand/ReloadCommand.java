package com.xg7plugins.commands.defaultCommands.reloadCommand;

import com.xg7plugins.XG7Plugins;
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
        syntax = "/xg7plugins reload <type> (plugin)",
        permission = "xg7plugins.command.reload"
)
public class ReloadCommand implements ICommand {

    private final HashMap<String, ReloadExpansion> expansions = new HashMap<>();

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
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 2) {
            syntaxError(sender, this.getClass().getAnnotation(Command.class).syntax());
            return;
        }

        String expansion = args.get(0, String.class);
        String plugin = args.get(1, String.class);
        if (XG7Plugins.getInstance().getPlugins().containsKey(plugin)) {
            if (expansions.containsKey(expansion)) {
                expansions.get(expansion).onReload(sender, args);
                return;
            }
            syntaxError(sender, this.getClass().getAnnotation(Command.class).syntax());
            return;
        }
        sender.sendMessage("Plugin not found");

    }

    public void addExpansions(ReloadExpansion... expansions) {
        if (expansions == null) return;
        for (ReloadExpansion expansion : expansions) {
            this.expansions.put(expansion.getName(), expansion);
        }
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
            suggestions.addAll(expansions.keySet());
        }
        if (args.len() == 2) {
            String arg2 = args.get(1, String.class);
            if (expansions.containsKey(arg2)) return Collections.singletonList(expansions.get(arg2).getPlugin().getName().split(" ")[0]);

            suggestions.addAll(XG7Plugins.getInstance().getPlugins().keySet().stream().filter(s -> !s.equals("XG7Plugins")).collect(Collectors.toList()));
        }
        return suggestions;
    }


}
