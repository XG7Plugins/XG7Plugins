package com.xg7plugins.commands.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.Variables;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandSetup(
        name = "variable",
        description = "Manages a globar or player variable",
        syntax = "/variable <set|get|delete|list|clear> [name] [value]",
        permission = "xg7plugins.command.variable",
        iconMaterial = XMaterial.CHEST,
        pluginClass = XG7Plugins.class
)
public class VariableCommand implements Command {

    @CommandConfig
    public CommandState onCommand(CommandSender sender) {

        Text.send("&8--- &bGlobal Vars&a:", sender);
        Variables.getAllGlobalVariables().forEach((pair) -> {
            Text.send("&7- &d" + pair.getFirst() + " &8= &f" +  pair.getSecond(), sender);
        });
        Text.send("&8--- &eAll players vars&a:", sender);
        XG7Plugins.getAPI().getAllPlayerUUIDs().forEach(uuid -> {
            List<Pair<String, String>> vars = Variables.getAllPlayerVariables(uuid);
            if (vars.isEmpty()) return;
            Player player = Bukkit.getPlayer(uuid);
            Text.send("&8- &f" + player.getName() + "&a:", sender);
            vars.forEach(pair -> {
                Text.send("&7- &d" + pair.getFirst() + " &8= &f" + pair.getSecond(), sender);
            });
        });

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "set",
            description = "Sets a variable",
            syntax = "/variable set <global|player> <name> <value>",
            permission = "xg7plugins.command.variable.set",
            iconMaterial = XMaterial.CHEST
    )
    public CommandState set(CommandSender sender, CommandArgs args) {

        if (args.len() < 3) return CommandState.SYNTAX_ERROR;

        String name = args.get(1, String.class);
        String value = args.join(2);

        Player player = Bukkit.getPlayer(args.get(0, String.class));

        if (player == null && args.get(0, String.class).equalsIgnoreCase("global")) {
            Variables.setGlobal(name, value);
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "variables.set",
                    Pair.of("var", name),
                    Pair.of("value", value)
            );
        } else {
            Variables.setPlayer(player.getUniqueId(), name, value);
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "variables.set-player",
                    Pair.of("target", player.getName()),
                    Pair.of("var", name),
                    Pair.of("value", value)
            );
        }

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "delete",
            description = "Deletes a variable",
            syntax = "/variable delete <global|player> <name>",
            permission = "xg7plugins.command.variable.delete",
            iconMaterial = XMaterial.LAVA_BUCKET
    )
    public CommandState delete(CommandSender sender, CommandArgs args) {

        if (args.len() != 2) return CommandState.SYNTAX_ERROR;

        String name = args.get(1, String.class);

        Player player = Bukkit.getPlayer(args.get(0, String.class));

        if (player == null && args.get(0, String.class).equalsIgnoreCase("global")) {
            Variables.removeGlobal(name);
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "variables.delete", Pair.of("var", name));
        } else {
            Variables.removePlayer(player.getUniqueId(), name);
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "variables.delete-player",
                    Pair.of("target", player.getName()),
                    Pair.of("var", name)
            );
        }

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "get",
            description = "Gets a variable",
            syntax = "/variable get <global|player> <name>",
            permission = "xg7plugins.command.variable.get",
            iconMaterial = XMaterial.NETHER_STAR
    )
    public CommandState get(CommandSender sender, CommandArgs args) {
        if (args.len() != 2) return CommandState.SYNTAX_ERROR;

        String name = args.get(1, String.class);

        Player player = Bukkit.getPlayer(args.get(0, String.class));

        if (player == null && args.get(0, String.class).equalsIgnoreCase("global")) {
            Text.send(Variables.getGlobal(name, "null"), sender);
        } else {
            Text.send(Variables.getPlayer(player.getUniqueId(), name, "null"), sender);
        }

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "clear",
            description = "Clears all variables",
            syntax = "/variable clear <global|player|all>",
            permission = "xg7plugins.command.variable.clear",
            iconMaterial = XMaterial.BARRIER
    )
    public CommandState clear(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) return CommandState.SYNTAX_ERROR;

        Player player = Bukkit.getPlayer(args.get(0, String.class));

        String option = args.get(0, String.class);

        if (option.equalsIgnoreCase("all")) {
            Variables.clearGlobal();
            XG7Plugins.getAPI().getAllPlayerUUIDs().forEach(Variables::clearPlayer);
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "variables.clear-all");
        }
        if (player == null && option.equalsIgnoreCase("global")) {
            Variables.clearGlobal();
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "variables.clear");
        } else {
            Variables.clearPlayer(player.getUniqueId());
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "variables.clear-player",
                    Pair.of("target", player.getName())
            );
        }


        return CommandState.FINE;
    }

    @CommandConfig(
            name = "list",
            description = "Lists all variables",
            syntax = "/variable list",
            permission = "xg7plugins.command.variable",
            iconMaterial = XMaterial.BOOK
    )
    public CommandState list(CommandSender sender, CommandArgs args) {
        return onCommand(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        if (args.len() == 0) {
            return Command.super.onTabComplete(sender, args);
        }

        CommandNode root = XG7Plugins.getInstance().getCommandManager().getRootCommandNode("xg7pluginsvariable");
        CommandNode cmd1 = root.getChild(args.get(0, String.class));

        if (cmd1 == null) {
            return Command.super.onTabComplete(sender, args);
        }

        switch (cmd1.getName()) {
            case "set":
            case "delete":
            case "get":
            case "clear":
                if (args.len() == 2) {

                    List<String> options = new ArrayList<>();
                    options.add("global");
                    if (cmd1.getName().equals("clear")) options.add("all");
                    options.addAll(XG7Plugins.getAPI().getAllPlayerNames());

                    return options;

                }

                if (args.len() == 3 && !cmd1.getName().equals("clear")) {
                    return Collections.singletonList("name");
                }

                if (args.len() > 4 && cmd1.getName().equals("set")) {
                    return Collections.singletonList("value");
                }

                break;
            default:
                return Command.super.onTabComplete(sender, args);
        }

        return Collections.emptyList();
    }

}
