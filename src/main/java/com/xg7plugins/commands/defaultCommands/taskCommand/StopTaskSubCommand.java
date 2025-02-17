package com.xg7plugins.commands.defaultCommands.taskCommand;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;

import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.TaskState;
import org.bukkit.command.CommandSender;

@Command(
        name = "stop",
        description = "Stop Task",
        syntax = "/xg7plugins tasks stop <ID>",
        isAsync = true,
        permission = "xg7plugins.command.tasks.stop"
)
public class StopTaskSubCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            syntaxError(sender, "/xg7plugins tasks stop <ID>");
            return;
        }

        TaskManager manager = XG7Plugins.getInstance().getTaskManager();
        String id = args.get(0, String.class);

        if (!manager.getTasks().containsKey(id)) {
            Text.fromLang(sender,XG7Plugins.getInstance(),"task-command.not-found").thenAccept(text -> text.send(sender));
            return;
        }

        TaskState state = manager.getTasks().get(id).getState();

        if (state == TaskState.IDLE) {
            Text.fromLang(sender,XG7Plugins.getInstance(), "task-command.already-stopped").thenAccept(text -> text.send(sender));
            return;
        }

        manager.cancelTask(id);

        XG7Plugins.getInstance().getDebug().warn("tasks","Task " + id + " was stopped by " + sender.getName());
        XG7Plugins.getInstance().getDebug().warn("tasks","It can cause errors in the plugin of the task!");
        XG7Plugins.getInstance().getDebug().warn("tasks","To resume the task to execution use /xg7plugins tasks restart " + id + "!");

        Text.fromLang(sender,XG7Plugins.getInstance(), "task-command.stopped").thenAccept(text -> text.send(sender));
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.REDSTONE_TORCH, this);
    }
}
