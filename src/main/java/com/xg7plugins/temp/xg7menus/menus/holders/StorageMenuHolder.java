package com.xg7plugins.temp.xg7menus.menus.holders;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.temp.xg7menus.Slot;
import com.xg7plugins.temp.xg7menus.item.Item;
import com.xg7plugins.temp.xg7menus.menus.gui.StorageMenu;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class StorageMenuHolder extends MenuHolder {


    public StorageMenuHolder(String id, Plugin plugin, String title, int size, InventoryType type, StorageMenu menu, Player player) {
        super(id, plugin, title,size,type, menu, player);
    }

    public CompletableFuture<List<Item>> getStorageItems() {
        return CompletableFuture.supplyAsync(() -> {

            List<Item> items = new ArrayList<>();

            StorageMenu menu = (StorageMenu) XG7Plugins.getInstance().getMenuManager().getMenu(plugin, id);

            for (int x = 0; x < menu.getStartEdge().getColumn(); x++) {
                for (int y = 0; y < menu.getEndEdge().getRow(); y++) {

                    ItemStack item = inventory.getItem(Slot.get(y, x));
                    if (item == null) continue;

                    items.add(Item.from(item));
                }
            }

            return items;
        });
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
