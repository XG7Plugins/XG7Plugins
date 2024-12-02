package com.xg7plugins.libs.newxg7menus.menus.holders;

import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.newxg7menus.Slot;
import com.xg7plugins.libs.newxg7menus.events.ClickEvent;
import com.xg7plugins.libs.newxg7menus.item.Item;
import com.xg7plugins.libs.newxg7menus.menus.gui.StorageMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Getter
public class StorageMenuHolder extends MenuHolder {


    public StorageMenuHolder(String id, Plugin plugin, Inventory inventory, Player player) {
        super(id, plugin, inventory, player);
    }

    public CompletableFuture<List<Item>> getStorageItems() {
        return CompletableFuture.supplyAsync(() -> {

            List<Item> items = new ArrayList<>();

            StorageMenu menu = (StorageMenu) XG7Plugins.getInstance().getNewMenuManagerTest().getRegistredMenus().get(id);

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
