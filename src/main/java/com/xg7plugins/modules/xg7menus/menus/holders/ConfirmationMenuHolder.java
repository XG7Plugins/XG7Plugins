package com.xg7plugins.modules.xg7menus.menus.holders;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.menus.BaseMenu;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@Getter
public class ConfirmationMenuHolder extends MenuHolder {

    public ConfirmationMenuHolder(String id, Plugin plugin, String title, int size, InventoryType type, BaseMenu menu, Player player) {
        super(id, plugin, title, size, type, menu, player);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
