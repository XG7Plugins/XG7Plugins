package com.xg7plugins.commands.impl;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.menus.tasks.TaskItem;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.builders.MenuBuilder;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.AsyncTask;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.PluginKey;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CommandSetup(
        name = "tasks",
        description = "Task taskManager",
        syntax = "/xg7plugins tasks [<stop | delete | restart | see> <ID>]",
        permission = "xg7plugins.command.tasks",
        iconMaterial = XMaterial.REPEATER,
        pluginClass = XG7Plugins.class
)
public class TaskCommand implements Command {
    
    private final TaskManager taskManager = XG7Plugins.getAPI().taskManager();

    @CommandConfig
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        if (!(sender instanceof Player)) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        XG7Plugins.getAPI().menus().getMenu(XG7Plugins.getInstance(), "tasks-menu").open((Player) sender);
        return CommandState.FINE;
    }

    @CommandConfig(
            name = "delete",
            isAsync = true,
            syntax = "/xg7plugins tasks delete <id>",
            description = "Deletes a task from the tasks registry",
            iconMaterial = XMaterial.LAVA_BUCKET
    )
    public CommandState delete(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }

        final String id = args.get(0, String.class);

        return checkAndGet(id, sender)
                .map(task -> {
                    taskManager.deleteRepeatingTask(task);

                    XG7Plugins.getInstance().getDebug().warn("tasks", "Task " + id + " was deleted by " + sender.getName());
                    XG7Plugins.getInstance().getDebug().warn("tasks", "To back up the task, you need to restart the plugin of the task!");

                    Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.deleted");

                    return CommandState.FINE;
                })
                .orElse(CommandState.ERROR);
    }

    @CommandConfig(
            name = "restart",
            isAsync = true,
            syntax = "/xg7plugins tasks restart <id>",
            description = "Restarts a stopped task",
            iconMaterial = XMaterial.COMPASS
    )
    public CommandState restart(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }

        final String id = args.get(0, String.class);

        return checkAndGet(id, sender)
                .map(task -> {
                    if (task.getTaskState() == TaskState.RUNNING) {
                        Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.already-running");
                        return CommandState.ERROR;
                    }

                    taskManager.runTimerTask(task);

                    XG7Plugins.getInstance().getDebug().warn("tasks", "Task " + id + " was restarted by " + sender.getName());

                    Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.restarted");

                    return CommandState.fine();
                })
                .orElse(CommandState.ERROR);
    }

    @CommandConfig(
            name = "see",
            syntax = "/xg7plugins tasks see <id>",
            description = "Shows detailed information about a task",
            iconMaterial = XMaterial.ENDER_PEARL
    )
    public CommandState see(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }
        final String id = args.get(0, String.class);

        return checkAndGet(id, sender)
                .map(task -> {

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
                    )).items(new TaskItem(player, task, Slot.fromSlot(13))).build().open((Player) sender);

                    return CommandState.FINE;
                })
                .orElse(CommandState.ERROR);
    }

    @CommandConfig(
            name = "stop",
            isAsync = true,
            syntax = "/xg7plugins tasks stop <id>",
            description = "Stops a running task",
            iconMaterial = XMaterial.BARRIER
    )
    public CommandState stop(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }

        final String id = args.get(0, String.class);

        return checkAndGet(id, sender)
                .map(task -> {

                    TaskState state = task.getTaskState();

                    if (state == TaskState.IDLE) {
                        Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.already-stopped");
                        return CommandState.ERROR;
                    }

                    taskManager.cancelRepeatingTask(task);

                    XG7Plugins.getInstance().getDebug().warn("tasks", "Task " + id + " was stopped by " + sender.getName());
                    XG7Plugins.getInstance().getDebug().warn("tasks", "It can cause errors in the plugin of the task!");
                    XG7Plugins.getInstance().getDebug().warn("tasks", "To resume the task to execution use /xg7plugins tasks restart " + id + "!");

                    Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.stopped");

                    return CommandState.FINE;
                })
                .orElse(CommandState.ERROR);
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        List<String> suggestions = new ArrayList<>();

        if (args.len() == 1) {
            return Command.super.onTabComplete(sender, args);
        }

        if (args.len() == 2) {
            suggestions.addAll(taskManager.getTimerTaskMap().keySet().stream().map(PluginKey::toString).collect(Collectors.toList()));
        }
        return suggestions;
    }

    private Optional<TimerTask> checkAndGet(String id, CommandSender sender) {
        if (!taskManager.containsTimerTask(PluginKey.of(id))) {
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.not-found");
            return Optional.empty();
        }
        TimerTask task = taskManager.getTimerTask(PluginKey.of(id));
        return Optional.ofNullable(task);
    }


}
