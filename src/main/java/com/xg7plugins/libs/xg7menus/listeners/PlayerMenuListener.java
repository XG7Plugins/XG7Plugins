package com.xg7plugins.libs.xg7menus.listeners;


import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7menus.MenuManager;
import com.xg7plugins.libs.xg7menus.MenuPermissions;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.DragEvent;
import com.xg7plugins.libs.xg7menus.menus.player.PlayerMenu;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.bukkitevents.EventHandler;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;

public class PlayerMenuListener implements Event {

    private final MenuManager manager = XG7Plugins.getInstance().getMenuManager();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!manager.getPlayerMenuMap().containsKey(event.getPlayer().getUniqueId())) return;

        PlayerMenu menu = manager.getPlayerMenuMap().get(event.getPlayer().getUniqueId());

        ClickEvent clickEvent = new ClickEvent(
                event.getPlayer(),
                ClickEvent.ClickAction.valueOf(event.getAction().name()),
                event.getPlayer().getInventory().getHeldItemSlot(),
                event.getItem(),
                menu,
                event.getClickedBlock() == null ? null : event.getClickedBlock().getLocation()
        );
        if (menu.getClickEvents().containsKey(event.getPlayer().getInventory().getHeldItemSlot())) {
            menu.getClickEvents().get(event.getPlayer().getInventory().getHeldItemSlot()).accept(clickEvent);
            event.setCancelled(clickEvent.isCancelled());
            return;
        }
        if (menu.getDefaultClickEvent() != null) {
            menu.getDefaultClickEvent().accept(clickEvent);
            event.setCancelled(clickEvent.isCancelled());
            return;
        }
        event.setCancelled(!menu.getPermissions().contains(MenuPermissions.INTERACT));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!manager.getPlayerMenuMap().containsKey(event.getPlayer().getUniqueId())) return;
        event.setCancelled(!manager.getPlayerMenuMap().get(event.getPlayer().getUniqueId()).getPermissions().contains(MenuPermissions.BREAK_BLOCKS));
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!manager.getPlayerMenuMap().containsKey(event.getPlayer().getUniqueId())) return;
        event.setCancelled(!manager.getPlayerMenuMap().get(event.getPlayer().getUniqueId()).getPermissions().contains(MenuPermissions.PLACE_BLOCKS));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!manager.getPlayerMenuMap().containsKey(event.getPlayer().getUniqueId())) return;
        event.setCancelled(!manager.getPlayerMenuMap().get(event.getPlayer().getUniqueId()).getPermissions().contains(MenuPermissions.DROP));
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (!manager.getPlayerMenuMap().containsKey(event.getPlayer().getUniqueId())) return;
        event.setCancelled(!manager.getPlayerMenuMap().get(event.getPlayer().getUniqueId()).getPermissions().contains(MenuPermissions.PICKUP));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!manager.getPlayerMenuMap().containsKey(event.getWhoClicked().getUniqueId())) return;
        if (!(event.getClickedInventory() instanceof PlayerInventory)) return;

        PlayerMenu menu = manager.getPlayerMenuMap().get(event.getWhoClicked().getUniqueId());

        ClickEvent clickEvent = new ClickEvent(
                (Player) event.getWhoClicked(),
                ClickEvent.ClickAction.valueOf(event.getClick().name()),
                event.getSlot(),
                event.getCurrentItem(),
                menu,
                null
        );

        if (menu.getClickEvents().containsKey(event.getSlot())) {
            menu.getClickEvents().get(event.getSlot()).accept(clickEvent);
            event.setCancelled(clickEvent.isCancelled());
            return;
        }
        if (menu.getDefaultClickEvent() != null) {
            menu.getDefaultClickEvent().accept(clickEvent);
            event.setCancelled(clickEvent.isCancelled());
            return;
        }
        event.setCancelled(!menu.getPermissions().contains(MenuPermissions.CLICK));

    }

    @EventHandler
    public void onDrag(final InventoryDragEvent event) {
        if (!manager.getPlayerMenuMap().containsKey(event.getWhoClicked().getUniqueId())) return;
        if (!(event.getInventory() instanceof PlayerInventory)) return;

        PlayerMenu playerMenu = manager.getPlayerMenuMap().get(event.getWhoClicked().getUniqueId());

        DragEvent dragEvent = new DragEvent(
                (Player) event.getWhoClicked(),
                event.getInventorySlots(),
                event.getNewItems(),
                playerMenu
        );
        if (playerMenu.getDefaultClickEvent() != null) {
            playerMenu.getDefaultClickEvent().accept(dragEvent);
            event.setCancelled(dragEvent.isCancelled());
            return;
        }
        event.setCancelled(!playerMenu.getPermissions().contains(MenuPermissions.DRAG));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        manager.removePlayerMenu(event.getPlayer().getUniqueId());
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}
