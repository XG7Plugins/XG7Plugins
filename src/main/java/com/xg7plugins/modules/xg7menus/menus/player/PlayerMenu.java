package com.xg7plugins.modules.xg7menus.menus.player;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.menus.BaseMenu;
import com.xg7plugins.modules.xg7menus.menus.holders.PlayerMenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public abstract class PlayerMenu extends BaseMenu {

    private final HashMap<UUID, HashMap<Integer, ItemStack>> playerOldItems;
    private final PlayerMenuMessages messages;

    protected PlayerMenu(Plugin plugin, String id, PlayerMenuMessages messages, boolean storeOldItems) {
        super(plugin, id);
        this.messages = messages;
        playerOldItems = storeOldItems ? new HashMap<>() : null;
    }

    public void onDrop(MenuEvent event) {
        event.getWhoClicked().sendMessage(messages.getOnDropMessage((Player) event.getWhoClicked()));
    }
    public void onPickup(MenuEvent event) {
        event.getWhoClicked().sendMessage(messages.getOnPickupMessage((Player) event.getWhoClicked()));
    }
    public void onBreak(MenuEvent event) {
        event.getWhoClicked().sendMessage(messages.getOnBreakMessage((Player) event.getWhoClicked()));
    }
    public void onPlace(MenuEvent event) {
        event.getWhoClicked().sendMessage(messages.getOnPlaceMessage((Player) event.getWhoClicked()));
    }

    public void close(Player player) {

        if (playerOldItems != null) {
            if (!playerOldItems.containsKey(player.getUniqueId())) return;
            playerOldItems.get(player.getUniqueId()).forEach(player.getInventory()::setItem);
            playerOldItems.remove(player.getUniqueId());
        }

        PlayerMenuHolder holder = XG7Menus.getInstance().getPlayerMenuHolder(player.getUniqueId());

        if (holder == null) return;

        MenuEvent event = new MenuEvent(player, MenuEvent.ClickAction.UNKNOWN, holder, player.getLocation());

        XG7Menus.getInstance().removePlayerMenuHolder(player.getUniqueId());

        onClose(event);
    }

    @Override
    public void open(Player player) {

        if (playerOldItems != null) {
            playerOldItems.put(player.getUniqueId(), new HashMap<>());


            for (int i = 0; i < player.getInventory().getSize(); i++) {
                if (player.getInventory().getItem(i) == null) continue;
                playerOldItems.get(player.getUniqueId()).put(i, player.getInventory().getItem(i));
            }
        }

        player.getInventory().clear();

        PlayerMenuHolder holder = new PlayerMenuHolder(id, plugin, this, player);

        XG7Menus.getInstance().registerPlayerMenuHolder(player.getUniqueId(), holder);

        putItems(player, holder);

        MenuEvent event = new MenuEvent(player, MenuEvent.ClickAction.UNKNOWN, holder, player.getLocation());

        onOpen(event);

    }

}
