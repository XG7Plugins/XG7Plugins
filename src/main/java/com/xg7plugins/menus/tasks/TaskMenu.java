package com.xg7plugins.menus.tasks;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.editor.InventoryShaper;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.ChangePageItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.CloseInventoryItem;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.tasks.plugin_tasks.TPSCalculator;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Pair;
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
    public List<InventoryItem> pagedItems(Player player) {

        Collection<TimerTask> tasks = XG7Plugins.getAPI().taskManager().getTimerTaskMap().values();

        return tasks.stream().map(t -> new TaskItem(player, t, null)).collect(Collectors.toList());
    }

    @Override
    public List<InventoryItem> getItems(Player player) {
        InventoryShaper editor = new InventoryShaper(getMenuConfigs());

        editor.setItem(ChangePageItem.nextPageItem(Slot.fromSlot(45)).name("lang:[go-back-item]"));
        editor.setItem(CloseInventoryItem.get(Slot.fromSlot(48)).name("lang:[close-item]"));
        editor.setItem(ChangePageItem.previousPageItem(Slot.fromSlot(53)).name("lang:[go-next-item]"));

        return editor.getItems();
    }

    @Override
    public void onRepeatingUpdate(BasicMenuHolder holder) {
        ConfigSection lang = XG7Plugins.getAPI().langManager().getLangByPlayer(XG7Plugins.getInstance(), holder.getPlayer()).getSecond().getLangConfiguration();

        InventoryUpdater updater = holder.getInventoryUpdater();

        updater.setItem(Slot.fromSlot(50),
                InventoryItem.from(Material.PAPER)
                        .name(" ")
                        .lore(lang.getList("tasks-menu.notes", String.class).orElse(Collections.emptyList()))
                        .setBuildPlaceholders(
                                Pair.of("tasks", String.valueOf(XG7Plugins.getAPI().taskManager().getTimerTaskMap().size())),
                                Pair.of("ram", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + " / " + Runtime.getRuntime().totalMemory() / 1024 / 1024),
                                Pair.of("tps", String.format("%.2f", ((TPSCalculator) XG7Plugins.getAPI().taskManager().getTimerTask(XG7Plugins.getPluginID("tps-calculator"))).getTPS()))
                        ));
    }
}
