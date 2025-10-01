package com.xg7plugins.commands.impl.task_command.sub_commands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandState;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@CommandSetup(
        name = "stop",
        description = "Stop Task",
        syntax = "/xg7plugins tasks stop <ID>",
        isAsync = true,
        permission = "xg7plugins.command.tasks.stop",
        pluginClass = XG7Plugins.class
)
public class StopTaskSubCommand implements Command {

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

        TaskState state = manager.getTimerTask(id).getTaskState();

        if (state == TaskState.IDLE) {
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.already-stopped");
            return CommandState.ERROR;
        }

        manager.cancelRepeatingTask(id);

        XG7Plugins.getInstance().getDebug().warn("Task " + id + " was stopped by " + sender.getName());
        XG7Plugins.getInstance().getDebug().warn("It can cause errors in the plugin of the task!");
        XG7Plugins.getInstance().getDebug().warn("To resume the task to execution use /xg7plugins tasks restart " + id + "!");

        // mantém o Text
        Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.stopped");
        return CommandState.FINE;
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.REDSTONE_TORCH, this);
    }
}
