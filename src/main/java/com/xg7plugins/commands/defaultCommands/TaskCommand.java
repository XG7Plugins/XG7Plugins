package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.menus.TaskMenu;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Command(
        name = "xg7pluginstasks",
        description = "Task Manager",
        syntax = "/xg7pluginstasks [<delete> <UUID>]",
        aliasesPath = "tasks",
        perm = "xg7plugins.command.tasks"
)
public class TaskCommand implements ICommand {
    @Override
    public ItemBuilder getIcon() {
        return ItemBuilder.commandIcon(XMaterial.REPEATER, this, XG7Plugins.getInstance());
    }

    public ISubCommand[] getSubCommands() {
        return new ISubCommand[]{new DeleteTaskSubCommand()};
    }
    @Override
    public void onCommand(org.bukkit.command.Command command, CommandSender sender, String label) {
        if (!(sender instanceof  Player)) {
            syntaxError(sender,"/xg7pluginstasks <On Console: [delete, see]> <On Console: UUID>");
            return;
        }

        TaskMenu.create(((Player) sender));
    }

    @Override
    public List<String> onTabComplete(org.bukkit.command.Command command, CommandSender sender, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) suggestions.add("delete");
        if (args.length == 2) {
            suggestions.addAll(
                    XG7Plugins.getInstance().getTaskManager().getTasksRunning().keySet().stream().map(
                            s -> s.split(":")[2]
                    ).collect(Collectors.toList())
            );
        }
        return suggestions;
    }

    static class DeleteTaskSubCommand implements ISubCommand {

        @Override
        public void onSubCommand(CommandSender sender, String[] args, String label) {

            if (args.length != 2) {
                syntaxError(sender, "/xg7pluginstasks <delete> <UUID>");
                return;
            }

            TaskManager manager = XG7Plugins.getInstance().getTaskManager();

            UUID taskId = UUID.fromString(args[1]);

            if (!manager.exists(taskId)) {
                Text.format("lang:[task-command.not-found]", XG7Plugins.getInstance()).send(sender);
                return;
            }
            manager.cancelTask(UUID.fromString(args[1]));

            XG7Plugins.getInstance().getLog().warn("Task " + taskId + " was deleted by " + sender.getName());
            XG7Plugins.getInstance().getLog().warn("To back up the task, you need to restart the plugin of the task!");

            Text.format("lang:[task-command.deleted]", XG7Plugins.getInstance()).send(sender);
        }

        @Override
        public ItemBuilder getIcon() {
            return ItemBuilder.subCommandIcon(XMaterial.BARRIER, this, XG7Plugins.getInstance());
        }
    }

}
