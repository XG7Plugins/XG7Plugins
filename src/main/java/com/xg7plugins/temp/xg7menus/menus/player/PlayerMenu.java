package com.xg7plugins.temp.xg7menus.menus.player;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.temp.xg7menus.events.MenuEvent;
import com.xg7plugins.temp.xg7menus.menus.BaseMenu;
import com.xg7plugins.temp.xg7menus.menus.holders.PlayerMenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public abstract class PlayerMenu extends BaseMenu {

    private final HashMap<UUID, HashMap<Integer, ItemStack>> playerOldItems;

    protected PlayerMenu(Plugin plugin, String id, boolean storeOldItems) {
        super(plugin, id);
        playerOldItems = storeOldItems ? new HashMap<>() : null;
    }

    public void onDrop(MenuEvent event) {}
    public void onPickup(MenuEvent event) {}
    public void onBreak(MenuEvent event) {}
    public void onPlace(MenuEvent event) {}

    public void close(Player player) {

        if (playerOldItems != null) {
            if (!playerOldItems.containsKey(player.getUniqueId())) return;
            playerOldItems.get(player.getUniqueId()).forEach(player.getInventory()::setItem);
            playerOldItems.remove(player.getUniqueId());
        }

        PlayerMenuHolder holder = XG7Plugins.getInstance().getMenuManager().getPlayerMenusMap().get(player.getUniqueId());

        if (holder == null) return;

        MenuEvent event = new MenuEvent(player, MenuEvent.ClickAction.UNKNOWN, holder, player.getLocation());

        XG7Plugins.getInstance().getMenuManager().removePlayerMenu(player.getUniqueId());

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

        XG7Plugins.getInstance().getMenuManager().addPlayerMenu(player.getUniqueId(), holder);

        putItems(player, holder);

        MenuEvent event = new MenuEvent(player, MenuEvent.ClickAction.UNKNOWN, holder, player.getLocation());

        onOpen(event);

    }

}
