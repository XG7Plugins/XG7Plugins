package com.xg7plugins.modules.xg7menus.menuhandler;

import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.events.ClickEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.holders.PlayerMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.player.PlayerMenu;
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

    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (XG7Menus.getInstance().hasPlayerMenuHolder(event.getPlayer().getUniqueId())) {

            PlayerMenuHolder holder = XG7Menus.getInstance().getPlayerMenuHolder(event.getPlayer().getUniqueId());

            event.setCancelled(!holder.getMenu().getMenuPermissions().contains(MenuPermissions.PLAYER_INTERACT));

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
        if (XG7Menus.getInstance().hasPlayerMenuHolder(event.getWhoClicked().getUniqueId()) && event.getInventory() instanceof PlayerInventory) {

            PlayerMenuHolder holder = XG7Menus.getInstance().getPlayerMenuHolder(event.getWhoClicked().getUniqueId());

            event.setCancelled(!holder.getMenu().getMenuPermissions().contains(MenuPermissions.CLICK));

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
        if (XG7Menus.getInstance().hasPlayerMenuHolder(event.getWhoClicked().getUniqueId()) && event.getInventory() instanceof PlayerInventory) {

            PlayerMenuHolder holder = XG7Menus.getInstance().getPlayerMenuHolder(event.getWhoClicked().getUniqueId());

            event.setCancelled(!holder.getMenu().getMenuPermissions().contains(MenuPermissions.DRAG));

            DragEvent dragEvent = new DragEvent(holder.getPlayer(), holder, event.getNewItems().entrySet().stream().map(entry -> new Item(entry.getValue()).slot(entry.getKey())).collect(Collectors.toList()), event.getInventorySlots(),event.getRawSlots());

            holder.getMenu().onDrag(dragEvent);

            if (dragEvent.isCancelled()) event.setCancelled(true);

        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (XG7Menus.getInstance().hasPlayerMenuHolder(event.getPlayer().getUniqueId())) {

            PlayerMenuHolder holder = XG7Menus.getInstance().getPlayerMenuHolder(event.getPlayer().getUniqueId());

            event.setCancelled(!holder.getMenu().getMenuPermissions().contains(MenuPermissions.PLAYER_DROP));

            MenuEvent menuEvent = new MenuEvent(event.getPlayer(), MenuEvent.ClickAction.KEYBOARD, holder, null);

            ((PlayerMenu) holder.getMenu()).onDrop(menuEvent);

            if (menuEvent.isCancelled()) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        if (XG7Menus.getInstance().hasPlayerMenuHolder(event.getPlayer().getUniqueId())) {

            PlayerMenuHolder holder = XG7Menus.getInstance().getPlayerMenuHolder(event.getPlayer().getUniqueId());

            event.setCancelled(!holder.getMenu().getMenuPermissions().contains(MenuPermissions.PLAYER_PICKUP));

            MenuEvent menuEvent = new MenuEvent(event.getPlayer(), MenuEvent.ClickAction.UNKNOWN, holder, null);

            ((PlayerMenu) holder.getMenu()).onPickup(menuEvent);

            if (menuEvent.isCancelled()) event.setCancelled(true);

        }
    }

    @EventHandler
    public void onBreakBlocks(BlockBreakEvent event) {
        if (XG7Menus.getInstance().hasPlayerMenuHolder(event.getPlayer().getUniqueId())) {

            PlayerMenuHolder holder = XG7Menus.getInstance().getPlayerMenuHolder(event.getPlayer().getUniqueId());

            event.setCancelled(!holder.getMenu().getMenuPermissions().contains(MenuPermissions.PLAYER_BREAK_BLOCKS));

            MenuEvent menuEvent = new MenuEvent(event.getPlayer(), MenuEvent.ClickAction.UNKNOWN, holder, null);

            ((PlayerMenu) holder.getMenu()).onBreak(menuEvent);

            if (menuEvent.isCancelled()) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceBlocks(BlockPlaceEvent event) {
        if (XG7Menus.getInstance().hasPlayerMenuHolder(event.getPlayer().getUniqueId())) {

            PlayerMenuHolder holder = XG7Menus.getInstance().getPlayerMenuHolder(event.getPlayer().getUniqueId());

            event.setCancelled(!holder.getMenu().getMenuPermissions().contains(MenuPermissions.PLAYER_PLACE_BLOCKS));

            MenuEvent menuEvent = new MenuEvent(event.getPlayer(), MenuEvent.ClickAction.UNKNOWN, holder, null);

            ((PlayerMenu) holder.getMenu()).onPlace(menuEvent);

            if (menuEvent.isCancelled()) event.setCancelled(true);
        }
    }
}
