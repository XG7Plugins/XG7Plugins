package com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PagedMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.tasks.tasks.AsyncTask;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class PagedMenu extends Menu {

    private final Slot pos1;
    private final Slot pos2;

    public PagedMenu(MenuConfigurations menuConfigs, Slot pos1, Slot pos2) {
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

        PagedMenuHolder menuHolder = new PagedMenuHolder(this, player);
        refresh(menuHolder);

        XG7Menus.registerHolder(menuHolder);


    }

    public abstract List<Item> pagedItems(Player player);

    @SneakyThrows
    public void goPage(int page, PagedMenuHolder menuHolder) {


        XG7PluginsAPI.taskManager().scheduleAsync(AsyncTask.of(XG7Plugins.getInstance(), () -> {
            try {
                List<Item> pagedItems = pagedItems(menuHolder.getPlayer());

                if (page < 0) return;
                if (page * Slot.areaOf(pos1, pos2) >= pagedItems.size()) return;
                List<Item> itemsToAdd = pagedItems.subList(page * (Slot.areaOf(pos1, pos2)), pagedItems.size());

                int index = 0;

                InventoryUpdater inventory = menuHolder.getInventoryUpdater();

                for (int x = pos1.getRow(); x <= pos2.getRow(); x++) {
                    for (int y = pos1.getColumn(); y <= pos2.getColumn(); y++) {

                        if (index >= itemsToAdd.size()) {
                            if (inventory.hasItem(Slot.of(x, y))) inventory.setItem(Slot.of(x, y), Item.air());
                            continue;
                        }
                        inventory.setItem(Slot.of(x, y), itemsToAdd.get(index));

                        index++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }), 100L);
    }


    public static void refresh(PagedMenuHolder menuHolder) {
        BasicMenu.refresh(menuHolder).thenRun(() -> menuHolder.goPage(0));
    }
}
