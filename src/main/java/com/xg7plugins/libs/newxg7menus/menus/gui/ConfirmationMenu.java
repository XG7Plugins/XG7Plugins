package com.xg7plugins.libs.newxg7menus.menus.gui;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.menus.holders.ConfirmationMenuHolder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.function.Consumer;

public abstract class ConfirmationMenu extends Menu {

    public ConfirmationMenu(Plugin plugin, String id, String title, InventoryType type) {
        super(plugin, id, title, type);

    }

    public ConfirmationMenu(Plugin plugin, String id, String title, int size) {
        super(plugin, id, title, size);

    }

    public abstract void confirm(Player player);
    public abstract void cancel(Player player);


    @Override
    public void open(Player player) {
        ConfirmationMenuHolder holder = new ConfirmationMenuHolder(id, plugin, type == null ? Bukkit.createInventory(player, size, Text.getWithPlaceholders(plugin, title, player)) : Bukkit.createInventory(player, type, Text.getWithPlaceholders(plugin, title, player)), player, onConfirm, onCancel);
        player.openInventory(holder.getInventory());
        putItems(player, holder.getInventory());

    }



}
