package com.xg7plugins.modules.xg7menus.menuhandler;

import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.holders.MenuHolder;
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

        MenuEvent menuEvent = new MenuEvent(holder);
        holder.getMenu().onOpen(menuEvent);

        if (menuEvent.isCancelled()) event.setCancelled(true);


    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof MenuHolder)) return;

        MenuHolder holder = (MenuHolder) inventory.getHolder();

        MenuEvent menuEvent = new MenuEvent(holder);
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
        Slot slotClicked = Slot.fromSlot(event.getSlot());

        if (holder.getMenu().getMenuConfigs().allowedActions() != null) event.setCancelled(!holder.getMenu().getMenuConfigs().allowedActions().contains(menuAction));

        ActionEvent actionEvent = new ActionEvent(holder, menuAction, event.getRawSlot(), slotClicked, Item.from(event.getCurrentItem()).slot(slotClicked));

        if (holder.getInventoryUpdater().hasClickActionOn(slotClicked)) {
            holder.getInventoryUpdater().getClickAction(slotClicked).accept(actionEvent);
            if (actionEvent.isCancelled()) event.setCancelled(true);
            return;
        }
        holder.getMenu().onClick(actionEvent);

        if (actionEvent.isCancelled()) event.setCancelled(true);

    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof MenuHolder)) return;

        MenuHolder holder = (MenuHolder) inventory.getHolder();

        List<Item> draggedItems = event.getNewItems().entrySet().stream().map((e) -> Item.from(e.getValue()).slot(e.getKey())).collect(Collectors.toList());

        Set<Slot> slotsClicked = event.getInventorySlots().stream().map(Slot::fromSlot).collect(Collectors.toSet());
        Set<Integer> rawSlots = event.getRawSlots();

        if (holder.getMenu().getMenuConfigs().allowedActions() != null) event.setCancelled(!holder.getMenu().getMenuConfigs().allowedActions().contains(MenuAction.DRAG));

        DragEvent dragEvent = new DragEvent(holder, draggedItems, slotsClicked, rawSlots);

        holder.getMenu().onDrag(dragEvent);

        if (dragEvent.isCancelled()) event.setCancelled(true);

    }


}
