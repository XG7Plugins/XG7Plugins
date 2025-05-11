package com.xg7plugins.commands.core_commands.task_command.sub_commands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.modules.xg7menus.builders.MenuBuilder;
import com.xg7plugins.modules.xg7menus.item.ClickableItem;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.IBasicMenu;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.IMenuConfigurations;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.TaskState;
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
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandConfigurations().syntax());
            return;
        }

        TaskManager manager = XG7PluginsAPI.taskManager();

        String id = args.get(0, String.class);

        if (!manager.containsTask(id)) {
            Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.not-found");
            return;
        }
        Task task = manager.getTask(id);

        if (!(sender instanceof Player)) {
            sender.sendMessage("Task info: " + task.getName());
            sender.sendMessage("Task state: " + task.getState().name());
            sender.sendMessage("Task plugin: " + task.getPlugin().getName());
            sender.sendMessage("Task repeating: " + task.isRepeating());
            sender.sendMessage("Task executor: " + task.getExecutorName());
            sender.sendMessage("Task delay: " + task.getDelay());
            sender.sendMessage("Task async: " + task.isAsync());
            return;
        }

        Player player = (Player) sender;

        Config lang = XG7PluginsAPI.langManager().getLangByPlayer(XG7Plugins.getInstance(), player).join().getLangConfiguration();

        ClickableItem builder = Item.from(XMaterial.REPEATER.parseMaterial()).clickable().onClick(event -> {
            if (event.getMenuAction().isRightClick()) {
                if (task.getState() == TaskState.RUNNING) {
                    XG7PluginsAPI.taskManager().cancelTask(task.getPlugin().getName() + ":" + task.getName());
                    Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.stopped");
                    IBasicMenu.refresh(event.getHolder());
                    return;
                }
                if ((task.getPlugin().getName() + ":" + task.getName()).equals("TPS calculator")) {
                    XG7Plugins.getInstance().getTpsCalculator().start();
                    Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.stopped");
                    IBasicMenu.refresh(event.getHolder());
                    return;
                }
                XG7PluginsAPI.taskManager().runTask(XG7PluginsAPI.taskManager().getTask(task.getPlugin().getName() + ":" + task.getName()));
                Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "task-command.restarted");

                IBasicMenu.refresh(event.getHolder());
                return;
            }
            if (event.getMenuAction().isLeftClick()) {
                Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "tasks-menu.copy-to-clipboard", Pair.of("id", task.getPlugin().getName() + ":" + task.getName()));
            }
            IBasicMenu.refresh(event.getHolder());
        });
        builder.name("&e" + task.getName());
        builder.lore(lang.getList("tasks-menu.task-item", String.class).orElse(Collections.emptyList()));

        builder.setNBTTag("task-id", task.getPlugin().getName() + ":" + task.getName());
        builder.setNBTTag("task-state", task.getState().name());

        builder.setBuildPlaceholders(
                Pair.of("plugin", task.getPlugin().getName()),
                Pair.of("id", task.getPlugin().getName() + ":" + task.getName()),
                Pair.of("state", task.getState().name()),
                Pair.of("task_is_running", String.valueOf(task.getState() == TaskState.RUNNING)),
                Pair.of("task_is_not_running", String.valueOf(task.getState() == TaskState.IDLE))
        );
        builder.slot(13);

        MenuBuilder.inicialize(IMenuConfigurations.of(
                getPlugin(),
                "task-menu-for-task-" + id,
                "Task: " + id,
                3
        )).items(builder).build().open((Player) sender);

    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.ENDER_PEARL, this);
    }
}
