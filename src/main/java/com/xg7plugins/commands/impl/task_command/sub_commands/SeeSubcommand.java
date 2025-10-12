package com.xg7plugins.commands.impl.task_command.sub_commands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandState;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.lang.Lang;
import com.xg7plugins.menus.tasks.TaskItem;
import com.xg7plugins.modules.xg7menus.builders.MenuBuilder;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.tasks.tasks.AsyncTask;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

@CommandSetup(
        name = "see",
        description = "See Task",
        syntax = "/xg7plugins tasks see <ID>",
        permission = "xg7plugins.command.tasks.see",
        pluginClass = XG7Plugins.class
)
public class SeeSubcommand implements Command {

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

        if (!(sender instanceof Player)) {
            sender.sendMessage("Task info: " + task.getId());
            sender.sendMessage("Task state: " + task.getTaskState().name());
            sender.sendMessage("Task plugin: " + task.getPlugin().getName());
            sender.sendMessage("Task executor: " + (task.getTask() instanceof AsyncTask ? ((AsyncTask) task.getTask()).getExecutorName() : ""));
            sender.sendMessage("Task delay: " + task.getDelay());
            sender.sendMessage("Task async: " + (task.getTask() instanceof AsyncTask));
            return CommandState.FINE;
        }

        Player player = (Player) sender;

        MenuBuilder.inicialize(MenuConfigurations.of(
                getPlugin(),
                "task-menu-for-task-" + id,
                "&0Task: " + id,
                3
        )).items(new TaskItem(player, task).slot(13)).build().open((Player) sender);

        return CommandState.FINE;
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.ENDER_PEARL, this);
    }
}
