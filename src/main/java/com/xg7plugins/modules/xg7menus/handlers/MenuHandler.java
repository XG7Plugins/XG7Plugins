package com.xg7plugins.modules.xg7menus.handlers;

import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.menuholders.MenuHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MenuHandler implements Listener {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof MenuHolder)) return;

        MenuHolder holder = (MenuHolder) inventory.getHolder();

        MenuEvent menuEvent = new MenuEvent(holder, event.isCancelled());
        holder.getMenu().onOpen(menuEvent);

        event.setCancelled(menuEvent.isCancelled());

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof MenuHolder)) return;

        MenuHolder holder = (MenuHolder) inventory.getHolder();

        MenuEvent menuEvent = new MenuEvent(holder, false);
        holder.getMenu().onClose(menuEvent);

        XG7Menus.removeHolder(holder.getPlayer().getUniqueId());

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        if (!(inventory.getHolder() instanceof MenuHolder)) return;

        MenuHolder holder = (MenuHolder) inventory.getHolder();

        MenuAction menuAction = MenuAction.from(event.getClick());
        Slot slotClicked = Slot.fromSlot(event.getSlot(), true);

        if (holder.getMenu().getMenuConfigs().allowedActions() != null) event.setCancelled(!holder.getMenu().getMenuConfigs().allowedActions().contains(menuAction));

        ActionEvent actionEvent = new ActionEvent(holder, menuAction, event.getRawSlot(), slotClicked, InventoryItem.from(event.getCurrentItem()).toInventoryItem(slotClicked), event.isCancelled());

        holder.getMenu().onClick(actionEvent);

        event.setCancelled(actionEvent.isCancelled());

    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof MenuHolder)) return;

        MenuHolder holder = (MenuHolder) inventory.getHolder();

        List<InventoryItem> draggedItems = event.getNewItems().entrySet().stream().map((e) -> InventoryItem.from(e.getValue()).toInventoryItem(e.getKey())).collect(Collectors.toList());

        Set<Slot> slotsClicked = event.getInventorySlots().stream().map(Slot::fromSlot).collect(Collectors.toSet());
        Set<Integer> rawSlots = event.getRawSlots();

        if (holder.getMenu().getMenuConfigs().allowedActions() != null) event.setCancelled(!holder.getMenu().getMenuConfigs().allowedActions().contains(MenuAction.DRAG));

        DragEvent dragEvent = new DragEvent(holder, draggedItems, slotsClicked, rawSlots, event.isCancelled());

        holder.getMenu().onDrag(dragEvent);

        event.setCancelled(dragEvent.isCancelled());

    }


}
