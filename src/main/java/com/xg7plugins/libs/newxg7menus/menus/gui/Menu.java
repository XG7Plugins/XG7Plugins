package com.xg7plugins.libs.newxg7menus.menus.gui;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.menus.BaseMenu;
import com.xg7plugins.libs.newxg7menus.menus.holders.MenuHolder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public abstract class Menu extends BaseMenu {

    protected final InventoryType type;
    protected final String title;
    protected final int size;

    public Menu(Plugin plugin, String id, String title, InventoryType type) {
        super(plugin, id);
        this.type = type;
        this.size = 0;
        this.title = title;
    }
    public Menu(Plugin plugin, String id, String title, int size) {
        super(plugin, id);
        this.type = null;
        this.size = size;
        this.title = title;
    }

    @Override
    public void open(Player player) {
        MenuHolder holder = new MenuHolder(id, plugin, type == null ? Bukkit.createInventory(player, size, Text.getWithPlaceholders(plugin, title, player)) : Bukkit.createInventory(player, type, Text.getWithPlaceholders(plugin, title, player)), player);
        player.openInventory(holder.getInventory());
        putItems(player, holder.getInventory());
    }
}
