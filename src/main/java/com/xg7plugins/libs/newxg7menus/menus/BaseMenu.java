package com.xg7plugins.libs.newxg7menus.menus;

import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.newxg7menus.MenuPrevents;
import com.xg7plugins.libs.newxg7menus.events.ClickEvent;
import com.xg7plugins.libs.newxg7menus.events.MenuEvent;
import com.xg7plugins.libs.newxg7menus.item.Item;
import com.xg7plugins.libs.newxg7menus.menus.holders.MenuHolder;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Getter
public abstract class BaseMenu {

    protected HashMap<Integer, Consumer<ClickEvent>> clickActions;
    protected Consumer<ClickEvent> defaultClick;
    protected Consumer<MenuEvent> onOpen;
    protected Consumer<MenuEvent> onClose;
    protected Set<MenuPrevents> menuPermissions;
    protected final Plugin plugin;
    protected final String id;


    protected BaseMenu(Plugin plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }
    public abstract boolean isEnabled();

    protected abstract List<Item> items();
    protected abstract Set<MenuPrevents> permissions();

    protected void putItems(Player player, Inventory inventory) {
        CompletableFuture.runAsync(() -> {
            items().forEach(item -> {
                        if (item.getOnClick() != null) clickActions.put(item.getSlot(), item.getOnClick());
                        inventory.setItem(item.getSlot(), item.getItemFor(player, plugin));
                    }
            );
        }, XG7Plugins.getInstance().getTaskManager().getExecutor());
    }
    public static CompletableFuture<Void> update(Player player, Item item, MenuHolder inventory) {
        return CompletableFuture.runAsync(() -> {
            if (item.getOnClick() != null) inventory.getUpdatedClickActions().put(item.getSlot(), item.getOnClick());
            inventory.getInventory().setItem(item.getSlot(), item.getItemFor(player, inventory.getPlugin()));
        }, XG7Plugins.getInstance().getTaskManager().getExecutor());
    }

    public abstract void open(Player player);

}
