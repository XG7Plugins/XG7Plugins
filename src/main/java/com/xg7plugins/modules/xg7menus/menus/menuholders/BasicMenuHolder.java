package com.xg7plugins.modules.xg7menus.menus.menuholders;

import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Getter
@AllArgsConstructor
public abstract class BasicMenuHolder {
    private BasicMenu menu;
    private Player player;

    public abstract Inventory getInventory();
    public abstract InventoryUpdater getInventoryUpdater();
}
