package com.xg7plugins.temp.xg7menus.menuhandler;

import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.temp.xg7menus.MenuPrevents;
import com.xg7plugins.temp.xg7menus.events.ClickEvent;
import com.xg7plugins.temp.xg7menus.events.DragEvent;
import com.xg7plugins.temp.xg7menus.events.MenuEvent;
import com.xg7plugins.temp.xg7menus.item.Item;
import com.xg7plugins.temp.xg7menus.menus.holders.MenuHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.stream.Collectors;

public class MenuHandler implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

        Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof MenuHolder) {

            MenuHolder holder = (MenuHolder) inventory.getHolder();

            MenuEvent menuEvent = new MenuEvent(holder.getPlayer(), MenuEvent.ClickAction.UNKNOWN, holder, null);
            holder.getMenu().onOpen(menuEvent);

            if (menuEvent.isCancelled()) event.setCancelled(true);

        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof MenuHolder) {

            MenuHolder holder = (MenuHolder) inventory.getHolder();

            MenuEvent menuEvent = new MenuEvent(holder.getPlayer(), MenuEvent.ClickAction.UNKNOWN, holder, null);
            holder.getMenu().onClose(menuEvent);

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        if (inventory.getHolder() instanceof MenuHolder) {

            MenuHolder holder = (MenuHolder) inventory.getHolder();

            event.setCancelled(holder.getMenu().getMenuPrevents().contains(MenuPrevents.CLICK));

            ClickEvent clickEvent = new ClickEvent(holder.getPlayer(), MenuEvent.ClickAction.valueOf(event.getClick().name()), holder, event.getSlot(), event.getRawSlot(), Item.from(event.getCurrentItem()), null);
            if (holder.getUpdatedClickEvents().containsKey(event.getSlot())) {
                holder.getUpdatedClickEvents().get(event.getSlot()).accept(clickEvent);
                if (clickEvent.isCancelled()) event.setCancelled(true);
                return;
            }
            holder.getMenu().onClick(clickEvent);

            if (clickEvent.isCancelled()) event.setCancelled(true);

        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof MenuHolder) {

            MenuHolder holder = (MenuHolder) inventory.getHolder();

            event.setCancelled(holder.getMenu().getMenuPrevents().contains(MenuPrevents.DRAG));
            DragEvent dragEvent = new DragEvent(holder.getPlayer(), holder, event.getNewItems().entrySet().stream().map(entry -> new Item(entry.getValue()).slot(entry.getKey())).collect(Collectors.toList()), event.getInventorySlots(),event.getRawSlots());

            holder.getMenu().onClick(dragEvent);

        }
    }


}
