package com.xg7plugins.commands.defaultCommands.taskCommand;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.tasks.TaskManager;
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
            Text.format("lang:[task-command.not-found]", XG7Plugins.getInstance()).send(sender);
            return;
        }

        manager.cancelTask(id);

        XG7Plugins.getInstance().getLog().warn("Task " + id + " was stopped by " + sender.getName());
        XG7Plugins.getInstance().getLog().warn("It can cause errors in the plugin of the task!");
        XG7Plugins.getInstance().getLog().warn("To resume the task to execution use /xg7plugins tasks restart " + id + "!");

        Text.format("lang:[task-command.stopped]", XG7Plugins.getInstance()).send(sender);

    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.REDSTONE_TORCH, this);
    }
}
