package com.xg7plugins.libs.newxg7menus.menus.holders;

import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.newxg7menus.Slot;
import com.xg7plugins.libs.newxg7menus.item.Item;
import com.xg7plugins.libs.newxg7menus.menus.gui.PageMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class PageMenuHolder extends MenuHolder {


    private int currentPage;
    private final int maxPages;


    public PageMenuHolder(String id, Plugin plugin, Inventory inventory, Player player, int maxPages) {
        super(id, plugin, inventory,player);
        this.currentPage = 0;
        this.maxPages = maxPages;
    }

    public void nextPage() {
        goPage(currentPage + 1);
    }
    public void previousPage() {
        goPage(currentPage - 1);
    }

    public void goPage(int page) {
        currentPage = page;
        if (page < 0) return;
        if (page >= maxPages) return;
        CompletableFuture.runAsync(() -> {
            PageMenu menu = (PageMenu) XG7Plugins.getInstance().getNewMenuManagerTest().getRegistredMenus().get(id);

            int area = ((menu.getEndEdge().getRow() - menu.getStartEdge().getRow()) * (menu.getEndEdge().getColumn() - menu.getStartEdge().getColumn()));

            int index = page * area;

            List<Item> pageItems = menu.getItemList();

            for (int x = menu.getStartEdge().getColumn(); x < menu.getEndEdge().getColumn(); x++) {
                for (int y = menu.getStartEdge().getRow(); y < menu.getEndEdge().getRow(); y++) {
                    if (index >= pageItems.size()) {
                        inventory.setItem(Slot.get(y,x), new ItemStack(Material.AIR));
                        continue;
                    }

                    inventory.setItem(Slot.get(y,x), pageItems.get(index).getItemFor(player, plugin));
                    index++;
                }
            }


        }, XG7Plugins.getInstance().getTaskManager().getExecutor());
    }
}
