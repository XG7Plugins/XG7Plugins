package com.xg7plugins.commands.defaultCommands.taskCommand;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.builders.MenuBuilder;
import com.xg7plugins.libs.xg7menus.item.ClickableItem;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.libs.xg7menus.menus.BaseMenu;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Command(
        name = "see",
        description = "See Task",
        syntax = "/xg7plugins tasks see <ID>",
        isAsync = true,
        permission = "xg7plugins.command.tasks.see"
)
public class SeeSubcommand implements ICommand {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            syntaxError(sender, "/xg7plugins tasks see <ID>");
            return;
        }

        TaskManager manager = XG7Plugins.getInstance().getTaskManager();

        String id = args.get(0, String.class);

        System.out.println("Task-ID: " + id);

        if (!manager.getTasks().containsKey(id)) {
            Text.format("lang:[task-command.not-found]", XG7Plugins.getInstance()).send(sender);
            return;
        }
        Task task = manager.getTasks().get(id);

        if (sender instanceof Player) {

            Config lang = XG7Plugins.getInstance().getLangManager() == null ? XG7Plugins.getInstance().getConfig("messages") : Config.of(XG7Plugins.getInstance(), XG7Plugins.getInstance().getLangManager().getLangByPlayer(XG7Plugins.getInstance(), (Player) sender).join());


            ClickableItem builder = Item.from(XMaterial.REPEATER.parseMaterial()).clickable().onClick(event -> {
                if (event.getClickAction().isRightClick()) {
                    if (task.getState() == TaskState.RUNNING) {
                        XG7Plugins.taskManager().cancelTask(task.getPlugin().getName() + ":" + task.getName());
                        Text.format("lang:[task-command.stopped]", XG7Plugins.getInstance()).send(sender);
                        BaseMenu.refresh(event.getInventoryHolder());
                        return;
                    }
                    if ((task.getPlugin().getName() + ":" + task.getName()).equals("TPS calculator")) {
                        XG7Plugins.getInstance().getTpsCalculator().start();
                        Text.format("lang:[task-command.stopped]", XG7Plugins.getInstance()).send(sender);
                        BaseMenu.refresh(event.getInventoryHolder());
                        return;
                    }
                    XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(task.getPlugin().getName() + ":" + task.getName()));
                    Text.format("lang:[task-command.restarted]", XG7Plugins.getInstance()).send(sender);

                    BaseMenu.refresh(event.getInventoryHolder());
                    return;
                }
                if (event.getClickAction().isLeftClick()) {
                    Text.formatComponent("lang:[tasks-menu.copy-to-clipboard]", XG7Plugins.getInstance()).replace("[ID]", task.getPlugin().getName() + ":" + task.getName()).send(sender);
                }

                BaseMenu.refresh(event.getInventoryHolder());
            });;
            builder.name("&e" + task.getName());
            builder.lore(lang.get("tasks-menu.task-item", List.class).orElse(Collections.emptyList()));

            builder.setNBTTag("task-id", task.getPlugin().getName() + ":" + task.getName());
            builder.setNBTTag("task-state", task.getState().name());

            builder.setBuildPlaceholders(new HashMap<String, String>() {{
                        put("[PLUGIN]", task.getPlugin().getName());
                        put("[ID]", task.getPlugin().getName() + ":" + task.getName());
                        put("[STATE]", task.getState().name());
                        put("%task_is_running%", String.valueOf(task.getState() == TaskState.RUNNING));
                        put("%task_is_not_running%", String.valueOf(task.getState() == TaskState.IDLE));
                    }});
            builder.slot(13);

            MenuBuilder.create("task-menu-for-task-" + id, XG7Plugins.getInstance()).addItem(builder).title("Task: " + id).size(27).build().open((Player) sender);
            return;
        }

        XG7Plugins.getInstance().getLog().log("Task info: " + task.getName());
        XG7Plugins.getInstance().getLog().log("Task state: " + task.getState().name());
        XG7Plugins.getInstance().getLog().log("Task plugin: " + task.getPlugin().getName());
        XG7Plugins.getInstance().getLog().log("Task repeating: " + task.isRepeating());
        XG7Plugins.getInstance().getLog().log("Task executor: " + task.getExecutorName());
        XG7Plugins.getInstance().getLog().log("Task delay: " + task.getDelay());
        XG7Plugins.getInstance().getLog().log("Task async: " + task.isAsync());

    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.ENDER_PEARL, this);
    }
}
