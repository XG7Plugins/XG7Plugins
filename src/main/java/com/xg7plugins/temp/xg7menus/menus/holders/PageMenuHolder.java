package com.xg7plugins.temp.xg7menus.menus.holders;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.temp.xg7menus.Slot;
import com.xg7plugins.temp.xg7menus.item.ClickableItem;
import com.xg7plugins.temp.xg7menus.item.Item;
import com.xg7plugins.temp.xg7menus.menus.gui.PageMenu;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class PageMenuHolder extends MenuHolder {

    protected int currentPage;
    private final int maxPages;
    private final int area;


    public PageMenuHolder(String id, Plugin plugin, String title, int size, InventoryType type, PageMenu pageMenu, Player player) {
        super(id, plugin, title,size,type,pageMenu,player);
        this.currentPage = 0;
        this.area = ((pageMenu.getEndEdge().getRow() + 1 - pageMenu.getStartEdge().getRow()) * (pageMenu.getEndEdge().getColumn() + 1 - pageMenu.getStartEdge().getColumn()));
        this.maxPages = pageMenu.pagedItems(player).size() / area;
    }

    public void nextPage() {
        goPage(currentPage + 1);
    }
    public void previousPage() {
        goPage(currentPage - 1);
    }

    public void goPage(int page) {
        if (page < 0) return;
        if (page > maxPages) return;
        this.currentPage = page;

        CompletableFuture.runAsync(() -> {

            PageMenu pageMenu = (PageMenu) this.menu;

            List<Item> pagedItems = pageMenu.pagedItems(player);

            List<Item> itemsToAdd = pagedItems.subList(page * area, pagedItems.size());

            int index = 0;

            for (int x = pageMenu.getStartEdge().getRow(); x <= pageMenu.getEndEdge().getRow(); x++) {
                for (int y = pageMenu.getStartEdge().getColumn(); y <= pageMenu.getEndEdge().getColumn(); y++) {
                    if (index >= itemsToAdd.size()) {
                        if (inventory.getItem(Slot.get(x,y)) != null) inventory.setItem(Slot.get(x,y), new ItemStack(Material.AIR));
                        continue;
                    }
                    inventory.setItem(Slot.get(x,y), itemsToAdd.get(index).getItemFor(player, plugin));

                    if (itemsToAdd.get(index) instanceof ClickableItem) {
                        int finalIndexToAdd = index;
                        this.updatedClickEvents.compute(Slot.get(x,y), (k, v) -> ((ClickableItem)itemsToAdd.get(finalIndexToAdd)).getOnClick());
                        index++;
                        continue;
                    }
                    this.updatedClickEvents.remove(Slot.get(x,y));
                    index++;
                }
            }


        }, XG7Plugins.taskManager().getAsyncExecutors().get("menus"));
    }
}
