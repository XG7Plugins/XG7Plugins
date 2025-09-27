package com.xg7plugins.modules.xg7menus.menus;

import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.item.impl.ClickableItem;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import org.bukkit.entity.Player;

import java.util.List;

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
    static void refresh(BasicMenuHolder holder) {

        holder.getInventory().clear();

        for (Item item : holder.getMenu().getItems(holder.getPlayer())) {
            holder.getInventoryUpdater().addItem(item);
        }
        if (holder.getMenu().getDefaultClickableItems() == null) return;
        for (ClickableItem item : holder.getMenu().getDefaultClickableItems()) {
            holder.getInventoryUpdater().addItem(item);
        }

    }
}
