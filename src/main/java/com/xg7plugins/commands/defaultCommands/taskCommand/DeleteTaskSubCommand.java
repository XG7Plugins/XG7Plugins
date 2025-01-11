package com.xg7plugins.commands.defaultCommands.taskCommand;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@Command(
        name = "delete",
        description = "Delete Task",
        syntax = "/xg7plugins tasks delete <ID>",
        isAsync = true,
        permission = "xg7plugins.command.tasks.delete"
)
public class DeleteTaskSubCommand implements ICommand {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            syntaxError(sender, "/xg7plugins tasks delete <ID>");
            return;
        }

        TaskManager manager = XG7Plugins.getInstance().getTaskManager();

        String id = args.get(0, String.class);

        if (!manager.getTasks().containsKey(id)) {
            Text.formatLang(XG7Plugins.getInstance(),sender,"task-command.not-found").thenAccept(text -> text.send(sender));
            return;
        }
        manager.deleteTask(id);

        XG7Plugins.getInstance().getLog().warn("Task " + id + " was deleted by " + sender.getName());
        XG7Plugins.getInstance().getLog().warn("To back up the task, you need to restart the plugin of the task!");

        Text.formatLang(XG7Plugins.getInstance(),sender,"task-command.deleted").thenAccept(text -> text.send(sender));
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR), this);
    }
}
