package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigSection;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.editor.InventoryShaper;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PagedMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.tasks.plugin_tasks.TPSCalculator;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class TaskMenu extends PagedMenu {

    public TaskMenu(Plugin plugin) {
        super(MenuConfigurations.of(
                plugin,
                "tasks-menu",
                "lang:[tasks-menu.title]",
                6,
                null,
                true,
                Collections.emptyList(),
                5000L
                ), Slot.of(2,2), Slot.of(5,8));
    }
    @Override
    public List<Item> pagedItems(Player player) {

        Collection<TimerTask> tasks = XG7PluginsAPI.taskManager().getTimerTaskMap().values();

        List<Item> pagedItems = new ArrayList<>();

        ConfigSection lang = XG7PluginsAPI.langManager().getLangByPlayer(XG7Plugins.getInstance(), player).join().getSecond().getLangConfiguration();

        tasks.forEach(task -> {
            Item builder = Item.from(XMaterial.REPEATER.parseMaterial());
            builder.name("&e" + task.getId());
            builder.lore(lang.getList("tasks-menu.task-item", String.class).orElse(Collections.emptyList()));

            builder.setNBTTag("task-id", task.getPlugin().getName() + ":" + task.getId());
            builder.setNBTTag("task-state", task.getTaskState().name());

            builder.setBuildPlaceholders(
                    Pair.of("plugin", task.getPlugin().getName()),
                    Pair.of("id", task.getPlugin().getName() + ":" + task.getId()),
                    Pair.of("state", task.getTaskState().name()),
                    Pair.of("task_is_running", String.valueOf(task.getTaskState() == TaskState.RUNNING)),
                    Pair.of("task_is_not_running", String.valueOf(task.getTaskState() == TaskState.IDLE))
            );

            pagedItems.add(builder);
        });


        return pagedItems;
    }

    @Override
    public List<Item> getItems(Player player) {
        ConfigSection lang = XG7PluginsAPI.langManager().getLangByPlayer(XG7Plugins.getInstance(), player).join().getSecond().getLangConfiguration();

        InventoryShaper editor = new InventoryShaper(getMenuConfigs());

        editor.setItem(Slot.fromSlot(45), Item.from(XMaterial.ARROW).name("lang:[go-back-item]"));
        editor.setItem(Slot.fromSlot(48), Item.from(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR)).name("lang:[close-item]"));
        editor.setItem(Slot.fromSlot(53), Item.from(XMaterial.ARROW).name("lang:[go-next-item]"));
        editor.setItem(
                Slot.fromSlot(50),
                Item.from(Material.PAPER)
                        .name(" ")
                        .lore(lang.getList("tasks-menu.notes", String.class).orElse(Collections.emptyList()))
                        .setBuildPlaceholders(
                                Pair.of("tasks", String.valueOf(XG7PluginsAPI.taskManager().getTimerTaskMap().size())),
                                Pair.of("ram", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + " / " + Runtime.getRuntime().totalMemory() / 1024 / 1024),
                                Pair.of("tps", String.format("%.2f", ((TPSCalculator) XG7PluginsAPI.taskManager().getTimerTask(XG7Plugins.getInstance(), "tps-calculator")).getTPS()))
                        )
        );

        return editor.getItems();


    }

    @Override
    public void onClick(ActionEvent event) {
        event.setCancelled(true);

        Player player = event.getHolder().getPlayer();

        PagedMenuHolder holder = (PagedMenuHolder) event.getHolder();

        switch (event.getClickedSlot().get()) {
            case 48:
                player.closeInventory();
                break;
            case 45:
                holder.previousPage();
                break;
            case 53:
                holder.nextPage();
                break;
            default:

                if (event.getClickedItem() == null || event.getClickedItem().isAir()) return;

                String taskId = event.getClickedItem().getTag("task-id", String.class).orElse(null);
                TaskState taskState = event.getClickedItem().getTag("task-state", TaskState.class).orElse(null);

                if (taskId == null) return;

                if (!XG7PluginsAPI.taskManager().containsTimerTask(taskId)) {
                    Text.sendTextFromLang(player, XG7Plugins.getInstance(),"task-command.not-found");
                    return;
                }

                if (event.getMenuAction().isRightClick()) {
                    if (taskState == TaskState.RUNNING) {
                        XG7PluginsAPI.taskManager().cancelRepeatingTask(taskId);
                        Text.sendTextFromLang(player, XG7Plugins.getInstance(),"task-command.stopped");
                        refresh(holder);
                        return;
                    }
                    XG7PluginsAPI.taskManager().runTimerTask(XG7PluginsAPI.taskManager().getTimerTask(taskId));
                    Text.sendTextFromLang(player, XG7Plugins.getInstance(),"task-command.restarted");

                    refresh(holder);
                    return;
                }
                if (event.getMenuAction().isLeftClick()) {
                    Text.fromLang(player, XG7Plugins.getInstance(),"tasks-menu.copy-to-clipboard")
                            .thenAccept(text -> text.replace("id", taskId).send(player));
                }

                refresh(holder);

                break;
        }


    }

    @Override
    public void onRepeatingUpdate(BasicMenuHolder holder) {
        refresh((PagedMenuHolder) holder);
    }
}
