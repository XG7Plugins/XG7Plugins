package com.xg7plugins.libs.newxg7menus;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.events.MenuEvent;
import com.xg7plugins.libs.newxg7menus.item.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class Menu<M extends Menu<M>> implements InventoryHolder {

    private HashMap<Integer, Consumer<MenuEvent>> clickActions;
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

    public CompletableFuture<List<ItemStack>> buildItems(Player player) {
        return CompletableFuture.supplyAsync(() -> items().stream().map(item -> item.getItemFor(player, plugin)).collect(Collectors.toList()));
    }

    public abstract void open(Player player);

}
