package com.xg7plugins.libs.xg7menus.listeners;

import com.xg7plugins.libs.xg7menus.MenuPermissions;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.DragEvent;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.libs.xg7menus.menus.gui.Menu;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.PlayerInventory;

public class MenuListener implements Event {
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu)) return;

        if (event.getClickedInventory() instanceof PlayerInventory) return;

        Menu baseMenu = (Menu) event.getInventory().getHolder();

        ClickEvent clickEvent = new ClickEvent(
                (Player) event.getWhoClicked(),
                ClickEvent.ClickAction.valueOf(event.getClick().name()),
                event.getSlot(),
                event.getCurrentItem(),
                baseMenu,
                null
        );

        if (baseMenu.getClickEvents().containsKey(event.getSlot())) {
            baseMenu.getClickEvents().get(event.getSlot()).accept(clickEvent);
            event.setCancelled(clickEvent.isCancelled());
            return;
        }
        if (baseMenu.getDefaultClickEvent() != null) {
            baseMenu.getDefaultClickEvent().accept(clickEvent);
            event.setCancelled(clickEvent.isCancelled());
            return;
        }
        event.setCancelled(!baseMenu.getPermissions().contains(MenuPermissions.CLICK));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu)) return;

        Menu baseMenu = (Menu) event.getInventory().getHolder();

        DragEvent dragEvent = new DragEvent(
                (Player) event.getWhoClicked(),
                event.getInventorySlots(),
                event.getNewItems(),
                baseMenu
        );
        if (baseMenu.getDefaultClickEvent() != null) {
            baseMenu.getDefaultClickEvent().accept(dragEvent);
            event.setCancelled(dragEvent.isCancelled());
            return;
        }
        event.setCancelled(!baseMenu.getPermissions().contains(MenuPermissions.DRAG));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu)) return;
        Menu baseMenu = (Menu) event.getInventory().getHolder();
        if (baseMenu.getCloseEvent() != null) baseMenu.getCloseEvent().accept(new MenuEvent(baseMenu, (Player) event.getPlayer()));
    }
    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu)) return;
        Menu baseMenu = (Menu) event.getInventory().getHolder();
        if (baseMenu.getOpenEvent() != null) baseMenu.getOpenEvent().accept(new MenuEvent(baseMenu, (Player) event.getPlayer()));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
