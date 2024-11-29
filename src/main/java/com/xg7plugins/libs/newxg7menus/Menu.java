package com.xg7plugins.libs.newxg7menus;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.events.ClickEvent;
import com.xg7plugins.libs.newxg7menus.events.MenuEvent;
import com.xg7plugins.libs.newxg7menus.item.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class Menu implements InventoryHolder {

    private HashMap<Integer, Consumer<ClickEvent>> clickActions;
    private Consumer<ClickEvent> defaultClick;
    private Consumer<MenuEvent> onOpen;
    private Consumer<MenuEvent> onClose;
    private Set<MenuPrevents> menuPermissions;
    private final Plugin plugin;
    private final String id;

    protected Menu(Plugin plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    public abstract List<Item> items();
    public abstract Set<MenuPrevents> permissions();

    public CompletableFuture<Void> putItems(Player player, Inventory inventory) {
        return CompletableFuture.runAsync(() -> items().forEach(item -> inventory.setItem(item.getSlot(), item.getItemFor(player, plugin))));
    }
    public CompletableFuture<Void> update(Player player, Item item, Inventory inventory) {
        return CompletableFuture.runAsync(() -> {
            items().set(item.getSlot(), item);
            inventory.setItem(item.getSlot(), item.getItemFor(player, plugin));
        });
    }

    public abstract void open(Player player);

}
