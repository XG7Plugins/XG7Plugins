package com.xg7plugins.commands.impl.task_command.sub_commands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@CommandSetup(
        name = "delete",
        description = "Delete Task",
        syntax = "/xg7plugins tasks delete <ID>",
        isAsync = true,
        permission = "xg7plugins.command.tasks.delete",
        pluginClass = XG7Plugins.class
)
public class DeleteTaskSubCommand implements Command {


    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandSetup().syntax());
            return;
        }

        TaskManager manager = XG7PluginsAPI.taskManager();

        String id = args.get(0, String.class);

        if (!manager.containsTimerTask(id)) {
            Text.fromLang(sender,XG7Plugins.getInstance(),"task-command.not-found").thenAccept(text -> text.send(sender));
            return;
        }
        manager.deleteRepeatingTask(id);

        XG7Plugins.getInstance().getDebug().warn("Task " + id + " was deleted by " + sender.getName());
        XG7Plugins.getInstance().getDebug().warn("To back up the task, you need to restart the plugin of the task!");

        Text.sendTextFromLang(sender,XG7Plugins.getInstance(),"task-command.deleted");
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR), this);
    }
}
