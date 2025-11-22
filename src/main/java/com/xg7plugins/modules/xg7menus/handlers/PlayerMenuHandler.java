package com.xg7plugins.modules.xg7menus.handlers;

import com.xg7plugins.cooldowns.CooldownManager;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PlayerMenuHolder;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PlayerMenuHandler implements Listener {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!XG7Menus.hasPlayerMenuHolder(event.getPlayer().getUniqueId())) return;

        PlayerMenuHolder holder = XG7Menus.getPlayerMenuHolder(event.getPlayer().getUniqueId());

        MenuAction menuAction = MenuAction.from(event.getAction());
        Slot slotClicked = Slot.fromSlot(event.getPlayer().getInventory().getHeldItemSlot(), true);

        event.setCancelled(!holder.getMenu().getMenuConfigs().allowedActions().contains(menuAction));

        ActionEvent actionEvent = new ActionEvent(holder, menuAction, slotClicked.get(), slotClicked, InventoryItem.from(event.getItem()).toInventoryItem(slotClicked), event.isCancelled());

        holder.getMenu().onClick(actionEvent);

        String message = holder.getMenu().getMenuConfigs().getOnInteractMessage(holder.getPlayer());
        if (!message.isEmpty()) holder.getPlayer().sendMessage(message);

        event.setCancelled(actionEvent.isCancelled());


    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMenuClick(InventoryClickEvent event) {
        if (!(XG7Menus.hasPlayerMenuHolder(event.getWhoClicked().getUniqueId()) && event.getInventory().getHolder().equals(event.getWhoClicked())))
            return;

        CooldownManager cooldownManager = XG7Plugins.getAPI().cooldowns();

        if (cooldownManager.containsPlayer("xg7menus_click_cooldown", (Player) event.getWhoClicked())) {
            event.setCancelled(true);
            return;
        }

        MenuAction menuAction = MenuAction.from(event.getClick());
        PlayerMenuHolder holder = XG7Menus.getPlayerMenuHolder(event.getWhoClicked().getUniqueId());

        Slot slotClicked = Slot.fromSlot(event.getSlot(), true);

        event.setCancelled(!holder.getMenu().getMenuConfigs().allowedActions().contains(menuAction));

        ActionEvent actionEvent = new ActionEvent(holder, menuAction, slotClicked.get(), slotClicked, InventoryItem.from(event.getCurrentItem()).toInventoryItem(slotClicked), event.isCancelled());

        holder.getMenu().onClick(actionEvent);

        String message = holder.getMenu().getMenuConfigs().getOnClickMessage(holder.getPlayer());
        if (!message.isEmpty()) holder.getPlayer().sendMessage(message);

        event.setCancelled(actionEvent.isCancelled());

        cooldownManager.addCooldown((Player) event.getWhoClicked(), "xg7menus_click_cooldown", 300L);

    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(XG7Menus.hasPlayerMenuHolder(event.getWhoClicked().getUniqueId()) && event.getInventory() instanceof PlayerInventory))
            return;
        PlayerMenuHolder holder = XG7Menus.getPlayerMenuHolder(event.getWhoClicked().getUniqueId());

        List<InventoryItem> draggedItems = event.getNewItems().entrySet().stream().map((e) -> InventoryItem.from(e.getValue()).toInventoryItem(e.getKey())).collect(Collectors.toList());

        Set<Slot> slotsClicked = event.getInventorySlots().stream().map(Slot::fromSlot).collect(Collectors.toSet());
        Set<Integer> rawSlots = event.getRawSlots();

        event.setCancelled(!holder.getMenu().getMenuConfigs().allowedActions().contains(MenuAction.DRAG));

        DragEvent dragEvent = new DragEvent(holder, draggedItems, slotsClicked, rawSlots, event.isCancelled());

        holder.getMenu().onDrag(dragEvent);

        String message = holder.getMenu().getMenuConfigs().getOnDragMessage(holder.getPlayer());
        if (!message.isEmpty()) holder.getPlayer().sendMessage(message);

        event.setCancelled(dragEvent.isCancelled());


    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        handleItemOrBlock(event, MenuAction.PLAYER_DROP);

    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        handleItemOrBlock(event, MenuAction.PLAYER_PICKUP);

    }

    @EventHandler
    public void onBreakBlocks(BlockBreakEvent event) {
        handleItemOrBlock(event, MenuAction.PLAYER_BREAK_BLOCK);

    }

    @EventHandler
    public void onPlaceBlocks(BlockPlaceEvent event) {
        handleItemOrBlock(event, MenuAction.PLAYER_PLACE_BLOCK);
    }

    private <T extends Event & Cancellable> void handleItemOrBlock(T event, MenuAction action) {

        Player player = ReflectionObject.of(event).getMethod("getPlayer").invoke();

        if (!XG7Menus.hasPlayerMenuHolder(player.getUniqueId())) return;

        PlayerMenuHolder holder = XG7Menus.getPlayerMenuHolder(player.getUniqueId());

        Slot slotHeld = Slot.fromSlot(player.getInventory().getHeldItemSlot(), true);

        if (holder.getMenu().getMenuConfigs().allowedActions() != null) event.setCancelled(!holder.getMenu().getMenuConfigs().allowedActions().contains(action));

        ActionEvent actionEvent = new ActionEvent(holder, action, slotHeld.get(), slotHeld,null, event.isCancelled());

        String message;

        switch (action) {
            case PLAYER_DROP:
                holder.getMenu().onDrop(actionEvent);
                message = holder.getMenu().getMenuConfigs().getOnDropMessage(holder.getPlayer());
                break;
            case PLAYER_PICKUP:
                holder.getMenu().onPickup(actionEvent);
                message = holder.getMenu().getMenuConfigs().getOnPickupMessage(holder.getPlayer());
                break;
            case PLAYER_BREAK_BLOCK:
                holder.getMenu().onBreakBlocks(actionEvent);
                message = holder.getMenu().getMenuConfigs().getOnBreakMessage(holder.getPlayer());
                break;
            case PLAYER_PLACE_BLOCK:
                holder.getMenu().onPlaceBlocks(actionEvent);
                message = holder.getMenu().getMenuConfigs().getOnPlaceMessage(holder.getPlayer());
                break;
            default:
                message = holder.getMenu().getMenuConfigs().getOnInteractMessage(holder.getPlayer());
        }

        if (!message.isEmpty()) holder.getPlayer().sendMessage(message);

        event.setCancelled(actionEvent.isCancelled());

    }
}
