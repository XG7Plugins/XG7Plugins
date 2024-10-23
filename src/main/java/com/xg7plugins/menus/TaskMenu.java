package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.libs.xg7menus.builders.menu.MenuBuilder;
import com.xg7plugins.libs.xg7menus.builders.menu.PageMenuBuilder;
import com.xg7plugins.libs.xg7menus.menus.gui.ItemsPageMenu;
import com.xg7plugins.libs.xg7menus.menus.gui.Menu;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TaskMenu {

    public static void create(Player player) {
        XG7Plugins plugin = XG7Plugins.getInstance();

        if (plugin.getMenuManager().cacheExistsPlayer("tasks", player)) {
            ItemsPageMenu menu = (ItemsPageMenu) XG7Plugins.getInstance().getMenuManager().getMenuByPlayer("tasks", player);
            menu.open();
            return;
        }
        Set<String> tasks = plugin.getTaskManager().getTasksRunning().keySet();

        List<BaseItemBuilder<?>> items = new ArrayList<>();

        YamlConfiguration lang = plugin.getLangManager().getLangByPlayer(player);
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

}
