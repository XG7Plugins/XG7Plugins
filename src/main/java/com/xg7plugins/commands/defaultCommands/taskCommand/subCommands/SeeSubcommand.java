package com.xg7plugins.commands.defaultCommands.taskCommand.subCommands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.modules.xg7menus.item.ClickableItem;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.BaseMenu;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

@Command(
        name = "see",
        description = "See Task",
        syntax = "/xg7plugins tasks see <ID>",
        permission = "xg7plugins.command.tasks.see"
)
public class SeeSubcommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            syntaxError(sender, "/xg7plugins tasks see <ID>");
            return;
        }

        TaskManager manager = XG7Plugins.getInstance().getTaskManager();

        String id = args.get(0, String.class);

        if (!manager.getTasks().containsKey(id)) {
            Text.fromLang(sender, XG7Plugins.getInstance(), "task-command.not-found").thenAccept(text -> text.send(sender));
            return;
        }
        Task task = manager.getTasks().get(id);

        if (sender instanceof Player) {

            Config lang = XG7Plugins.getInstance().getLangManager() == null ? XG7Plugins.getInstance().getConfig("messages") : XG7Plugins.getInstance().getLangManager().getLangByPlayer(XG7Plugins.getInstance(), (Player) sender).join().getLangConfiguration();

            ClickableItem builder = Item.from(XMaterial.REPEATER.parseMaterial()).clickable().onClick(event -> {
                if (event.getMenuAction().isRightClick()) {
                    if (task.getState() == TaskState.RUNNING) {
                        XG7Plugins.taskManager().cancelTask(task.getPlugin().getName() + ":" + task.getName());
                        Text.fromLang(sender, XG7Plugins.getInstance(), "task-command.stopped").thenAccept(text -> text.send(sender));
                        BaseMenu.refresh(event.getInventoryHolder());
                        return;
                    }
                    if ((task.getPlugin().getName() + ":" + task.getName()).equals("TPS calculator")) {
                        XG7Plugins.getInstance().getTpsCalculator().start();
                        Text.fromLang(sender, XG7Plugins.getInstance(), "task-command.stopped").thenAccept(text -> text.send(sender));
                        BaseMenu.refresh(event.getInventoryHolder());
                        return;
                    }
                    XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(task.getPlugin().getName() + ":" + task.getName()));
                    Text.fromLang(sender, XG7Plugins.getInstance(), "task-command.restarted").thenAccept(text -> text.send(sender));

                    BaseMenu.refresh(event.getInventoryHolder());
                    return;
                }
                if (event.getMenuAction().isLeftClick()) {
                    Text.fromLang(sender, XG7Plugins.getInstance(), "tasks-menu.copy-to-clipboard")
                            .thenAccept(text -> {
                                text.replace("id", task.getPlugin().getName() + ":" + task.getName()).send(sender);
                            });
                }

                BaseMenu.refresh(event.getInventoryHolder());
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

            MenuBuilder.create("task-menu-for-task-" + id, XG7Plugins.getInstance()).addItem(builder).title("Task: " + id).size(27).build().open((Player) sender);
            return;
        }

        sender.sendMessage("Task info: " + task.getName());
        sender.sendMessage("Task state: " + task.getState().name());
        sender.sendMessage("Task plugin: " + task.getPlugin().getName());
        sender.sendMessage("Task repeating: " + task.isRepeating());
        sender.sendMessage("Task executor: " + task.getExecutorName());
        sender.sendMessage("Task delay: " + task.getDelay());
        sender.sendMessage("Task async: " + task.isAsync());

    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.ENDER_PEARL, this);
    }
}
