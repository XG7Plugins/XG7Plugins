package com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.StorageMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public abstract class StorageMenu extends Menu {
    private final Slot pos1;
    private final Slot pos2;

    public StorageMenu(MenuConfigurations menuConfigs, Slot pos1, Slot pos2) {
        super(menuConfigs);

        int startRow = Math.min(pos1.getRow(), pos2.getRow());
        int finalRow = Math.max(pos1.getRow(), pos2.getRow());
        int startColumn = Math.min(pos1.getColumn(), pos2.getColumn());
        int finalColumn = Math.max(pos1.getColumn(), pos2.getColumn());

        this.pos1 = Slot.of(startRow, startColumn);
        this.pos2 = Slot.of(finalRow, finalColumn);
    }

    @Override
    public void open(Player player) {
        StorageMenuHolder menuHolder = new StorageMenuHolder(this, player);
        refresh(menuHolder);
        XG7Menus.registerHolder(menuHolder);

    }

    public abstract List<InventoryItem> getStorageItems(Player player);

    public static void refresh(StorageMenuHolder menuHolder) {
        BasicMenu.refresh(menuHolder);
        List<InventoryItem> storageItems = menuHolder.getMenu().getStorageItems(menuHolder.getPlayer());

        if (storageItems.isEmpty()) return;

        int index = 0;

        Slot pos1 = menuHolder.getMenu().getPos1();
        Slot pos2 = menuHolder.getMenu().getPos2();

        for (int x = pos1.getRow(); x <= pos2.getRow(); x++) {
            for (int y = pos1.getColumn(); y <= pos2.getColumn(); y++) {

                if (index >= storageItems.size()) {
                    if (menuHolder.getInventoryUpdater().hasItem(Slot.of(x, y))) {
                        menuHolder.getInventoryUpdater().setItem(Slot.of(x, y), InventoryItem.air());
                    }
                    continue;
                }

                InventoryItem item = storageItems.get(index);
                menuHolder.getInventoryUpdater().setItem(Slot.of(x, y), item);
                index++;
            }
        }
    }
}
