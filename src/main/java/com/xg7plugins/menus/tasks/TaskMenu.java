package com.xg7plugins.menus.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigSection;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.editor.InventoryShaper;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.ChangePageItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.CloseInventoryItem;
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
import java.util.stream.Collectors;

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

        return tasks.stream().map(t -> new TaskItem(player, t)).collect(Collectors.toList());
    }

    @Override
    public List<Item> getItems(Player player) {
        InventoryShaper editor = new InventoryShaper(getMenuConfigs());

        editor.setItem(Slot.fromSlot(45), ChangePageItem.nextPageItem().name("lang:[go-back-item]"));
        editor.setItem(Slot.fromSlot(48), CloseInventoryItem.get().name("lang:[close-item]"));
        editor.setItem(Slot.fromSlot(53), ChangePageItem.previousPageItem().name("lang:[go-next-item]"));

        return editor.getItems();
    }

    @Override
    public void onRepeatingUpdate(BasicMenuHolder holder) {
        ConfigSection lang = XG7PluginsAPI.langManager().getLangByPlayer(XG7Plugins.getInstance(), holder.getPlayer()).join().getSecond().getLangConfiguration();

        InventoryUpdater updater = holder.getInventoryUpdater();

        updater.setItem(Slot.fromSlot(50),
                Item.from(Material.PAPER)
                        .name(" ")
                        .lore(lang.getList("tasks-menu.notes", String.class).orElse(Collections.emptyList()))
                        .setBuildPlaceholders(
                                Pair.of("tasks", String.valueOf(XG7PluginsAPI.taskManager().getTimerTaskMap().size())),
                                Pair.of("ram", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + " / " + Runtime.getRuntime().totalMemory() / 1024 / 1024),
                                Pair.of("tps", String.format("%.2f", ((TPSCalculator) XG7PluginsAPI.taskManager().getTimerTask(XG7Plugins.getInstance(), "tps-calculator")).getTPS()))
                        ));
    }
}
