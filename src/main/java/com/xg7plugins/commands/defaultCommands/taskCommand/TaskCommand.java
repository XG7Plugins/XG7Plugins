package com.xg7plugins.commands.defaultCommands.taskCommand;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(
        name = "tasks",
        description = "Task Manager",
        syntax = "/xg7plugins tasks [<stop | delete | restart | see> <ID>]",
        permission = "xg7plugins.command.tasks"
)
public class TaskCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    private ICommand[] subCommands = new ICommand[]{new DeleteTaskSubCommand(), new SeeSubcommand(), new StopTaskSubCommand(), new RestartTaskSubCommand()};
//
//    @Override
//    public Item getIcon() {
//        return Item.commandIcon(XMaterial.REPEATER, this);
//    }

    @Override
    public ICommand[] getSubCommands() {
        return subCommands;
    }
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (!(sender instanceof  Player)) {
            syntaxError(sender,"/xg7plugins tasks [<stop> <ID>, <delete> <ID>, <restart> <ID>, <see> <ID>]");
            return;
        }

        //XG7Plugins.getInstance().getMenuManager().getMenu(XG7Plugins.getInstance(), "task-menu").open((Player) sender);
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
