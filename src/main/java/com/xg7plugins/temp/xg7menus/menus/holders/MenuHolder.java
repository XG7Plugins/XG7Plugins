package com.xg7plugins.temp.xg7menus.menus.holders;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.temp.xg7menus.events.ClickEvent;
import com.xg7plugins.temp.xg7menus.menus.BaseMenu;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Consumer;

@Getter
public class MenuHolder implements InventoryHolder {

    protected final String id;
    protected final Plugin plugin;
    protected Inventory inventory;
    protected final BaseMenu menu;
    protected final Player player;
    protected final HashMap<Integer, Consumer<ClickEvent>> updatedClickEvents = new HashMap<>();

    public MenuHolder(String id, Plugin plugin, String title, int size, InventoryType type, BaseMenu menu, Player player) {
        this.id = id;
        this.plugin = plugin;
        if (title != null) this.inventory = type == null ? Bukkit.createInventory(this, size, title) : Bukkit.createInventory(this, type, title);
        this.menu = menu;
        this.player = player;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
