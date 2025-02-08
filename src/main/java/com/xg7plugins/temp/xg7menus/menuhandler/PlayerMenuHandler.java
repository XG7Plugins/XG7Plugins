package com.xg7plugins.temp.xg7menus.menuhandler;

import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.temp.xg7menus.MenuManager;
import com.xg7plugins.temp.xg7menus.MenuPrevents;
import com.xg7plugins.temp.xg7menus.events.ClickEvent;
import com.xg7plugins.temp.xg7menus.events.DragEvent;
import com.xg7plugins.temp.xg7menus.events.MenuEvent;
import com.xg7plugins.temp.xg7menus.item.Item;
import com.xg7plugins.temp.xg7menus.menus.holders.PlayerMenuHolder;
import com.xg7plugins.temp.xg7menus.menus.player.PlayerMenu;
import lombok.AllArgsConstructor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.stream.Collectors;

@AllArgsConstructor
public class PlayerMenuHandler implements Listener {

    private final MenuManager menuManager;

    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (menuManager.hasPlayerMenu(event.getPlayer().getUniqueId())) {

            PlayerMenuHolder holder = menuManager.getPlayerMenusMap().get(event.getPlayer().getUniqueId());

            event.setCancelled(holder.getMenu().getMenuPrevents().contains(MenuPrevents.PLAYER_INTERACT));

            int slot = event.getPlayer().getInventory().getHeldItemSlot();

            ClickEvent clickEvent = new ClickEvent(holder.getPlayer(), MenuEvent.ClickAction.valueOf(event.getAction().name()), holder, slot, event.getPlayer().getInventory().getHeldItemSlot(), Item.from(event.getItem()),event.getClickedBlock() == null ? null : event.getClickedBlock().getLocation());
            if (holder.getUpdatedClickEvents().containsKey(slot)) {
                holder.getUpdatedClickEvents().get(slot).accept(clickEvent);
                if (clickEvent.isCancelled()) event.setCancelled(true);
                return;
            }
            holder.getMenu().onClick(clickEvent);

            if (clickEvent.isCancelled()) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMenuClick(InventoryClickEvent event) {
        if (menuManager.hasPlayerMenu(event.getWhoClicked().getUniqueId()) && event.getInventory() instanceof PlayerInventory) {

            PlayerMenuHolder holder = menuManager.getPlayerMenusMap().get(event.getWhoClicked().getUniqueId());

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
        if (menuManager.hasPlayerMenu(event.getWhoClicked().getUniqueId()) && event.getInventory() instanceof PlayerInventory) {

            PlayerMenuHolder holder = menuManager.getPlayerMenusMap().get(event.getWhoClicked().getUniqueId());

            event.setCancelled(holder.getMenu().getMenuPrevents().contains(MenuPrevents.DRAG));
            DragEvent dragEvent = new DragEvent(holder.getPlayer(), holder, event.getNewItems().entrySet().stream().map(entry -> new Item(entry.getValue()).slot(entry.getKey())).collect(Collectors.toList()), event.getInventorySlots(),event.getRawSlots());

            holder.getMenu().onClick(dragEvent);

            if (dragEvent.isCancelled()) event.setCancelled(true);

        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (menuManager.hasPlayerMenu(event.getPlayer().getUniqueId())) {

            PlayerMenuHolder holder = menuManager.getPlayerMenusMap().get(event.getPlayer().getUniqueId());

            event.setCancelled(holder.getMenu().getMenuPrevents().contains(MenuPrevents.PLAYER_DROP));

            MenuEvent menuEvent = new MenuEvent(event.getPlayer(), MenuEvent.ClickAction.KEYBOARD, holder, null);

            ((PlayerMenu) holder.getMenu()).onDrop(menuEvent);

            if (menuEvent.isCancelled()) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        if (menuManager.hasPlayerMenu(event.getPlayer().getUniqueId())) {

            PlayerMenuHolder holder = menuManager.getPlayerMenusMap().get(event.getPlayer().getUniqueId());

            event.setCancelled(holder.getMenu().getMenuPrevents().contains(MenuPrevents.PLAYER_PICKUP));

            MenuEvent menuEvent = new MenuEvent(event.getPlayer(), MenuEvent.ClickAction.KEYBOARD, holder, null);

            ((PlayerMenu) holder.getMenu()).onPickup(menuEvent);

            if (menuEvent.isCancelled()) event.setCancelled(true);

        }
    }

    @EventHandler
    public void onBreakBlocks(BlockBreakEvent event) {
        if (menuManager.hasPlayerMenu(event.getPlayer().getUniqueId())) {

            PlayerMenuHolder holder = menuManager.getPlayerMenusMap().get(event.getPlayer().getUniqueId());

            event.setCancelled(holder.getMenu().getMenuPrevents().contains(MenuPrevents.PLAYER_BREAK_BLOCKS));

            MenuEvent menuEvent = new MenuEvent(event.getPlayer(), MenuEvent.ClickAction.KEYBOARD, holder, null);

            ((PlayerMenu) holder.getMenu()).onBreak(menuEvent);

            if (menuEvent.isCancelled()) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceBlocks(BlockPlaceEvent event) {
        if (menuManager.hasPlayerMenu(event.getPlayer().getUniqueId())) {

            PlayerMenuHolder holder = menuManager.getPlayerMenusMap().get(event.getPlayer().getUniqueId());

            event.setCancelled(holder.getMenu().getMenuPrevents().contains(MenuPrevents.PLAYER_PLACE_BLOCKS));

            MenuEvent menuEvent = new MenuEvent(event.getPlayer(), MenuEvent.ClickAction.KEYBOARD, holder, null);

            ((PlayerMenu) holder.getMenu()).onPlace(menuEvent);

            if (menuEvent.isCancelled()) event.setCancelled(true);
        }
    }
}
