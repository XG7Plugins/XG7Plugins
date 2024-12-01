package com.xg7plugins.libs.newxg7menus.menus.player;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.menus.BaseMenu;
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

    @Override
    public void open(Player player) {

        playerOldItems.putIfAbsent(player.getUniqueId(), new HashMap<>());


        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (player.getInventory().getItem(i) == null) continue;
            playerOldItems.get(player.getUniqueId()).put(i, player.getInventory().getItem(i));
        }

        player.getInventory().clear();

        putItems(player, player.getInventory());

    }

}
