package com.xg7plugins.temp.xg7menus.menus.holders;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.temp.xg7menus.menus.BaseMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class PlayerMenuHolder extends MenuHolder {

    private final Player player;

    public PlayerMenuHolder(String id, Plugin plugin, BaseMenu menu, Player player) {
        super(id, plugin, null,9,null, menu, player);
        this.player = player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return player.getInventory();
    }
}
