package com.xg7plugins.libs.newxg7menus.menus.holders;

import com.xg7plugins.Plugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class MenuHolder implements InventoryHolder {

    private final String id;
    private final Plugin plugin;
    private final Inventory inventory;

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
