package com.xg7plugins.commands.defaultCommands.taskCommand.subCommands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@Command(
        name = "delete",
        description = "Delete Task",
        syntax = "/xg7plugins tasks delete <ID>",
        isAsync = true,
        permission = "xg7plugins.command.tasks.delete",
        pluginClass = XG7Plugins.class
)
public class DeleteTaskSubCommand implements ICommand {


    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandsConfigurations().syntax());
            return;
        }

        TaskManager manager = XG7Plugins.getInstance().getTaskManager();

        String id = args.get(0, String.class);

        if (!manager.getTasks().containsKey(id)) {
            Text.fromLang(sender,XG7Plugins.getInstance(),"task-command.not-found").thenAccept(text -> text.send(sender));
            return;
        }
        manager.deleteTask(id);

        XG7Plugins.getInstance().getDebug().warn("Task " + id + " was deleted by " + sender.getName());
        XG7Plugins.getInstance().getDebug().warn("To back up the task, you need to restart the plugin of the task!");

        Text.fromLang(sender,XG7Plugins.getInstance(),"task-command.deleted").thenAccept(text -> text.send(sender));
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR), this);
    }
}
