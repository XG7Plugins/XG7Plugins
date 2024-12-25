package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.libs.xg7menus.menus.gui.Menu;
import com.xg7plugins.libs.xg7menus.menus.gui.PageMenu;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.*;

public class TaskMenu extends PageMenu {

    public TaskMenu(Plugin plugin) {
        super(plugin, "task-menu", "lang:[tasks-menu.title]", 54, Slot.of(2,2), Slot.of(5,8));
    }

    public static void create(Player player) {

        Set<String> tasks = plugin.getTaskManager().getTasksRunning().keySet();

        List<BaseItemBuilder<?>> items = new ArrayList<>();

        YamlConfiguration lang = plugin.getLangManager() == null ? plugin.getConfigsManager().getConfig("messages").getConfig() : plugin.getLangManager().getLangByPlayer(player);
        tasks.forEach(task -> {

            String[] split = task.split(":");

            String pluginName = split[0];
            String taskName = split[1];
            String id = split[2];

            ItemBuilder builder = ItemBuilder.from(XMaterial.REPEATER.parseMaterial(), plugin);
            builder.name(taskName);
            builder.lore(lang.getStringList("tasks-menu.item-click"), new HashMap<String, String>() {{
                put("[PLUGIN]", pluginName);
                put("[ID]", id);
            }});
            builder.click(event -> {
                if (event.getClickAction().isRightClick()) {
                    plugin.getTaskManager().cancelTask(task);
                    plugin.getMenuManager().removePlayer("tasks", player);
                    create(player);
                    return;
                }
                ((Menu) event.getClickedMenu()).close();
                Text.formatComponent("lang:[tasks-menu.copy-to-clipboard]", plugin)
                        .replace("[ID]", id)
                        .replace("[PLUGIN]", pluginName)
                        .send(player);
            });
            items.add(builder);
        });
        PageMenuBuilder builder = MenuBuilder.page("tasks")
                .title("lang:[tasks-menu.title]")
                .rows(6)
                .setArea(Slot.of(2,2), Slot.of(5,8))
                .setItems(items)
                .setItem(49, ItemBuilder.from(XMaterial.BARRIER.parseItem(), plugin).name("lang:[close-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).close()))
                .setItem(50, ItemBuilder.from(Material.PAPER, plugin).name(" ").lore(lang.getStringList("tasks-menu.notes")));
        if (tasks.size() > 24) {
            builder.setItem(45, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-back-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).previousPage()));
            builder.setItem(53, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-next-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).nextPage()));
        }
        builder.build(player, plugin).open();
    }

    @Override
    public List<Item> pagedItems(Player player) {

        Collection<Task> tasks = XG7Plugins.taskManager().getTasks().values();

        List<Item> pagedItems = new ArrayList<>();

        Config lang = XG7Plugins.getInstance().getLangManager() == null ? XG7Plugins.getInstance().getConfig("messages") : Config.of(XG7Plugins.getInstance(), XG7Plugins.getInstance().getLangManager().getLangByPlayer(XG7Plugins.getInstance(), player).join());

        tasks.forEach(task -> {
            Item builder = Item.from(XMaterial.REPEATER.parseMaterial(), plugin);
            builder.name(task.getName());
            builder.lore(lang.getStringList("tasks-menu.item-click"), new HashMap<String, String>() {{
                put("[PLUGIN]", task.getPlugin().getName());
                put("[ID]", task.getName());
            }});
            builder.click(event -> {
                if (event.getClickAction().isRightClick()) {
                    XG7Plugins.taskManager().cancelTask(task);
                    XG7Plugins.menuManager().removePlayer("tasks", player);
                    create(player);
                    return;
                }
                ((Menu) event.getClickedMenu()).close();
                Text.formatComponent("lang:[tasks-menu.copy-to-clipboard]", plugin)
                        .replace("[ID]", task.getName())
                        .replace("[PLUGIN]", task.getPlugin().getName())
                        .send(player);
            });
            pagedItems.add(builder);
        });


        return List.of();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected List<Item> items(Player player) {
        return List.of();
    }
}
