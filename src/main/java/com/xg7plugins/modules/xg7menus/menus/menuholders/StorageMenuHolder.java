package com.xg7plugins.modules.xg7menus.menus.menuholders;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.StorageMenu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StorageMenuHolder extends MenuHolder {

    public StorageMenuHolder(StorageMenu menu, Player player) {
        super(menu, player);
    }

    @Override
    public StorageMenu getMenu() {
        return (StorageMenu) super.getMenu();
    }

    public List<InventoryItem> retrieveItems() {

        List<InventoryItem> items = new ArrayList<>();

        StorageMenu menu = getMenu();

        Slot pos1 = menu.getPos1();
        Slot pos2 = menu.getPos2();

        for (int x = pos1.getRow(); x <= pos2.getRow(); x++) {
            for (int y = pos1.getColumn(); y <= pos2.getColumn(); y++) {
                InventoryItem item = getInventoryUpdater().getItem(Slot.of(x, y));
                if (!item.isAir()) items.add(item);
            }
        }

        return items;

    }
}
