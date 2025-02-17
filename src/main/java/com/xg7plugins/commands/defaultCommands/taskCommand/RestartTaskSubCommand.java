package com.xg7plugins.commands.defaultCommands.taskCommand;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.TaskState;
import org.bukkit.command.CommandSender;

@Command(
        name = "restart",
        description = "Restart Task",
        syntax = "/xg7plugins tasks restart <ID>",
        isAsync = true,
        permission = "xg7plugins.command.tasks.restart"
)
public class RestartTaskSubCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            syntaxError(sender, "/xg7plugins tasks restart <ID>");
            return;
        }

        TaskManager manager = XG7Plugins.getInstance().getTaskManager();

        String id = args.get(0, String.class);

        if (!manager.getTasks().containsKey(id)) {
            Text.fromLang(sender,XG7Plugins.getInstance(),"task-command.not-found").thenAccept(text -> text.send(sender));
            return;
        }

        Task task = manager.getTasks().get(id);

        if (task.getState() == TaskState.RUNNING) {
            Text.fromLang(sender,XG7Plugins.getInstance(),"task-command.already-running").thenAccept(text -> text.send(sender));
            return;
        }

        manager.runTask(task);

        XG7Plugins.getInstance().getDebug().warn("tasks","Task " + id + " was restarted by " + sender.getName());

        Text.fromLang(sender,XG7Plugins.getInstance(),"task-command.restarted").thenAccept(text -> text.send(sender));
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.COMPASS, this);
    }
}
