package com.xg7plugins.commands.impl.task_command;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.impl.task_command.sub_commands.DeleteTaskSubCommand;
import com.xg7plugins.commands.impl.task_command.sub_commands.RestartTaskSubCommand;
import com.xg7plugins.commands.impl.task_command.sub_commands.SeeSubcommand;
import com.xg7plugins.commands.impl.task_command.sub_commands.StopTaskSubCommand;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandSetup(
        name = "tasks",
        description = "Task Manager",
        syntax = "/xg7plugins tasks [<stop | delete | restart | see> <ID>]",
        permission = "xg7plugins.command.tasks",
        pluginClass = XG7Plugins.class
)
public class TaskCommand implements Command {

    private final List<Command> subCommands = Arrays.asList(new DeleteTaskSubCommand(), new SeeSubcommand(), new StopTaskSubCommand(), new RestartTaskSubCommand());

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.REPEATER, this);
    }

    @Override
    public List<Command> getSubCommands() {
        return subCommands;
    }
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (!(sender instanceof  Player)) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandSetup().syntax());
            return;
        }

        XG7Menus.getInstance().getMenu(XG7Plugins.getInstance(), "tasks-menu").open((Player) sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        List<String> suggestions = new ArrayList<>();

        if (args.len() == 2) {
            Command subCommand = getSubCommands().stream().filter(cmd -> cmd.getCommandSetup().name().equalsIgnoreCase(args.get(0, String.class))).findFirst().orElse(null);
            if (subCommand == null) return suggestions;
            if (!sender.hasPermission(subCommand.getCommandSetup().permission()) || !sender.hasPermission("xg7plugins.command.anti-tab-bypass")) return suggestions;
            suggestions.addAll(XG7PluginsAPI.taskManager().getTimerTaskMap().keySet());
        }
        return suggestions;
    }


}
