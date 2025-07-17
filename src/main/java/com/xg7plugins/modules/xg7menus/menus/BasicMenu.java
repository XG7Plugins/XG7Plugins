package com.xg7plugins.modules.xg7menus.menus;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.item.ClickableItem;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BasicMenu {

    BasicMenuConfigs getMenuConfigs();

    List<Item> getItems(Player player);

    default List<ClickableItem> getDefaultClickableItems() {
        return null;
    }

    void open(Player player);
    void close(BasicMenuHolder menuHolder);

    default void onClick(ActionEvent event) {

    }
    default void onDrag(DragEvent event) {

    }
    default void onOpen(MenuEvent event) {

    }
    default void onClose(MenuEvent event) {

    }
    default void onUpdate(BasicMenuHolder holder, MenuUpdateActions actions) {

    }
    default void onRepeatingUpdate(BasicMenuHolder holder) {

    }
    static CompletableFuture<Void> refresh(BasicMenuHolder holder) {

        return CompletableFuture.runAsync(() -> {
            holder.getInventory().clear();

            for (Item item : holder.getMenu().getItems(holder.getPlayer())) {
                holder.getInventoryUpdater().addItem(item);
            }
            if (holder.getMenu().getDefaultClickableItems() == null) return;
            for (ClickableItem item : holder.getMenu().getDefaultClickableItems()) {
                holder.getInventoryUpdater().addItem(item);
            }

        }, XG7PluginsAPI.taskManager().getExecutor("menus"));
    }
}
