package com.xg7plugins.menus.tasks;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.MenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PagedMenuHolder;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.AsyncTask;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TaskItem extends ClickableItem {

    private final TimerTask task;

    public TaskItem(Player player, TimerTask task, Slot slot) {
        super(null, slot);
        this.task = task;

        ConfigSection lang = XG7Plugins.getAPI().langManager().getLangByPlayer(XG7Plugins.getInstance(), player).getSecond().getLangConfiguration();

        InventoryItem builder = Item.from(XMaterial.REPEATER).toInventoryItem(slot);
        builder.name("&e" + task.getId());
        builder.lore(lang.getList("tasks-menu.task-item", String.class).orElse(Collections.emptyList()));

        builder.setBuildPlaceholders(
                Pair.of("plugin", task.getPlugin().getName()),
                Pair.of("id", task.getPlugin().getName() + ":" + task.getId()),
                Pair.of("state", task.getTaskState().name()),
                Pair.of("is_bukkit_task", String.valueOf(task.getTask() instanceof BukkitTask)),
                Pair.of("is_async_task", String.valueOf(task.getTask() instanceof AsyncTask || (task.getTask() instanceof BukkitTask && ((BukkitTask) task.getTask()).isAsync()))),
                Pair.of("task_is_running", String.valueOf(task.getTaskState() == TaskState.RUNNING)),
                Pair.of("task_is_not_running", String.valueOf(task.getTaskState() == TaskState.IDLE))
        );

        itemStack = builder.getItemFor(player, XG7Plugins.getInstance());

    }

    @Override
    public void onClick(ActionEvent event) {

        Player player = event.getHolder().getPlayer();

        MenuHolder holder = (MenuHolder) event.getHolder();

        InventoryUpdater updater = holder.getInventoryUpdater();

        String taskId = task.getId();

        if (event.getMenuAction().isRightClick()) {

            updater.setItem(new TaskItem(player, task, event.getClickedSlot()));

            if (task.getTaskState() == TaskState.RUNNING) {

                if (!player.hasPermission( "xg7plugins.command.tasks.stop")) {
                    CommandState.NO_PERMISSION.send(player);
                    return;
                }

                XG7Plugins.getAPI().taskManager().cancelRepeatingTask(task);
                Text.sendTextFromLang(player, XG7Plugins.getInstance(),"task-command.stopped");
                if (holder instanceof PagedMenuHolder) {
                    PagedMenu.refresh((PagedMenuHolder) holder);
                    return;
                }
                BasicMenu.refresh(holder);
                return;
            }

            if (!player.hasPermission( "xg7plugins.command.tasks.restart")) {
                CommandState.NO_PERMISSION.send(player);
                return;
            }

            XG7Plugins.getAPI().taskManager().runTimerTask(task);
            Text.sendTextFromLang(player, XG7Plugins.getInstance(),"task-command.restarted");

            if (holder instanceof PagedMenuHolder) {
                PagedMenu.refresh((PagedMenuHolder) holder);
                return;
            }
            BasicMenu.refresh(holder);
            return;
        }
        if (event.getMenuAction().isLeftClick()) {
            Text.sendTextFromLang(player, XG7Plugins.getInstance(),"tasks-menu.copy-to-clipboard", Pair.of("id", task.getPlugin().getName() + ":" + taskId));
        }

        if (holder instanceof PagedMenuHolder) {
            PagedMenu.refresh((PagedMenuHolder) holder);
            return;
        }
        BasicMenu.refresh(holder);
    }
}
