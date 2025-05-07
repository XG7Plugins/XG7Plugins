package com.xg7plugins.modules.xg7menus.menus.menus.gui.menus;

import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.holders.PlayerMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.IMenuConfigurations;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class PagedMenu extends Menu {

    private final Slot pos1;
    private final Slot pos2;

    public PagedMenu(IMenuConfigurations menuConfigs, Slot pos1, Slot pos2) {
        super(menuConfigs);
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public abstract List<Item> pagedItems(Player player);

    public CompletableFuture<Integer> goPage(int page, PlayerMenuHolder menuHolder) {
        return CompletableFuture.supplyAsync(() -> {

            List<Item> pagedItems = pagedItems(menuHolder.getPlayer());

            List<Item> itemsToAdd = pagedItems.subList(page * (pos1), pagedItems.size());

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
            // Logic to go to the specified page
        });
    }

    public static void refresh() {

    }
}
