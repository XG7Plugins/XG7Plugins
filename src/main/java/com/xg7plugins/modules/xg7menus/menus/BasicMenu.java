package com.xg7plugins.modules.xg7menus.menus;

import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import org.bukkit.entity.Player;

import java.util.List;

public interface BasicMenu {

    BasicMenuConfigs getMenuConfigs();

    List<Item> getItems(Player player);

    void open(Player player);
    void close(BasicMenuHolder menuHolder);

    default void onClick(ActionEvent event) {
        event.setCancelled(true);
        InventoryUpdater updater = event.getHolder().getInventoryUpdater();

        Item item = updater.getItem(event.getClickedSlot());

        if (item instanceof ClickableItem) {
            ClickableItem clickableItem = (ClickableItem) item;
            clickableItem.onClick(event);
        }

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

        holder.getInventoryUpdater().refresh();

        

    }
}
