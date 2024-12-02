package com.xg7plugins.libs.newxg7menus.menus.gui;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.Slot;
import com.xg7plugins.libs.newxg7menus.item.Item;
import com.xg7plugins.libs.newxg7menus.menus.holders.MenuHolder;
import com.xg7plugins.libs.newxg7menus.menus.holders.StorageMenuHolder;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

@Getter
public abstract class StorageMenu extends Menu {

    private final Slot startEdge;
    private final Slot endEdge;


    public StorageMenu(Plugin plugin, String id, String title, InventoryType type, Slot pos1, Slot pos2) {
        super(plugin, id, title, type);

        int startX = Math.min(pos1.getColumn(), pos2.getColumn());
        int finalX = Math.max(pos1.getColumn(), pos2.getColumn());
        int startY = Math.min(pos1.getRow(), pos2.getRow());
        int finalY = Math.min(pos1.getRow(), pos2.getRow());

        this.startEdge = new Slot(startX, startY);
        this.endEdge = new Slot(finalX, finalY);
    }

    public StorageMenu(Plugin plugin, String id, String title, int size, Slot pos1, Slot pos2) {
        super(plugin, id, title, size);

        int startX = Math.min(pos1.getColumn(), pos2.getColumn());
        int finalX = Math.max(pos1.getColumn(), pos2.getColumn());
        int startY = Math.min(pos1.getRow(), pos2.getRow());
        int finalY = Math.min(pos1.getRow(), pos2.getRow());

        this.startEdge = new Slot(startX, startY);
        this.endEdge = new Slot(finalX, finalY);
    }

    public abstract List<Item> getStoredItems(Player player);

    @Override
    public void open(Player player) {
        StorageMenuHolder holder = new StorageMenuHolder(id, plugin, type == null ? Bukkit.createInventory(player, size, Text.getWithPlaceholders(plugin, title, player)) : Bukkit.createInventory(player, type, Text.getWithPlaceholders(plugin, title, player)), player);
        player.openInventory(holder.getInventory());
        putItems(player, holder.getInventory());
    }
}
