package com.xg7plugins.commands.defaultCommands.taskCommand;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.defaultCommands.taskCommand.subCommands.DeleteTaskSubCommand;
import com.xg7plugins.commands.defaultCommands.taskCommand.subCommands.RestartTaskSubCommand;
import com.xg7plugins.commands.defaultCommands.taskCommand.subCommands.SeeSubcommand;
import com.xg7plugins.commands.defaultCommands.taskCommand.subCommands.StopTaskSubCommand;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(
        name = "tasks",
        description = "Task Manager",
        syntax = "/xg7plugins tasks [<stop | delete | restart | see> <ID>]",
        permission = "xg7plugins.command.tasks",
        pluginClass = XG7Plugins.class
)
public class TaskCommand implements ICommand {

    private final ICommand[] subCommands = new ICommand[]{new DeleteTaskSubCommand(), new SeeSubcommand(), new StopTaskSubCommand(), new RestartTaskSubCommand()};

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.REPEATER, this);
    }

    @Override
    public ICommand[] getSubCommands() {
        return subCommands;
    }
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (!(sender instanceof  Player)) {
            CommandMessages.SYNTAX_ERROR.send(getPlugin(), sender, getCommandsConfigurations().syntax());
            return;
        }

        XG7Menus.getInstance().getMenu(XG7Plugins.getInstance(), "task-menu").open((Player) sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        List<String> suggestions = new ArrayList<>();

        if (args.len() == 1) {
            suggestions.add("stop");
            suggestions.add("delete");
            suggestions.add("restart");
            suggestions.add("see");
        }
        if (args.len() == 2) {
            suggestions.addAll(XG7Plugins.taskManager().getTasks().keySet());
        }
        return suggestions;
    }


}
