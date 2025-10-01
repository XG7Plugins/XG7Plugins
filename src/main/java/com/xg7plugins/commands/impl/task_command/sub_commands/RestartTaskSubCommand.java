package com.xg7plugins.commands.impl.task_command.sub_commands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandState;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@CommandSetup(
        name = "restart",
        description = "Restart Task",
        syntax = "/xg7plugins tasks restart <ID>",
        isAsync = true,
        permission = "xg7plugins.command.tasks.restart",
        pluginClass = XG7Plugins.class
)
public class RestartTaskSubCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        TaskManager manager = XG7PluginsAPI.taskManager();

        String id = args.get(0, String.class);

        if (!manager.containsTimerTask(id)) {
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.not-found");
            return CommandState.ERROR;
        }

        TimerTask task = manager.getTimerTask(id);

        if (task.getTaskState() == TaskState.RUNNING) {
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.already-running");
            return CommandState.ERROR;
        }

        manager.runTimerTask(task);

        XG7Plugins.getInstance().getDebug().warn("Task " + id + " was restarted by " + sender.getName());

        Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.restarted");

        return CommandState.FINE;
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.COMPASS, this);
    }
}