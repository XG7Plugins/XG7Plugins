package com.xg7plugins.commands.defaultCommands.taskCommand;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.utils.text.Text;
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
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            syntaxError(sender, "/xg7plugins tasks stop <ID>");
            return;
        }

        TaskManager manager = XG7Plugins.getInstance().getTaskManager();
        String id = args.get(0, String.class);

        if (!manager.getTasks().containsKey(id)) {
            Text.formatLang(XG7Plugins.getInstance(),sender,"task-command.not-found").thenAccept(text -> text.send(sender));
            return;
        }

        TaskState state = manager.getTasks().get(id).getState();

        if (state == TaskState.IDLE) {
            Text.formatLang(XG7Plugins.getInstance(), sender, "task-command.already-stoppe]").thenAccept(text -> text.send(sender));
            return;
        }

        manager.cancelTask(id);

        XG7Plugins.getInstance().getLog().warn("Task " + id + " was stopped by " + sender.getName());
        XG7Plugins.getInstance().getLog().warn("It can cause errors in the plugin of the task!");
        XG7Plugins.getInstance().getLog().warn("To resume the task to execution use /xg7plugins tasks restart " + id + "!");

        Text.formatLang(XG7Plugins.getInstance(), sender, "task-command.stopped").thenAccept(text -> text.send(sender));
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.REDSTONE_TORCH, this);
    }
}
