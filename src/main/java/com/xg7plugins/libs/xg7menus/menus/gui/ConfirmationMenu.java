package com.xg7plugins.libs.xg7menus.menus.gui;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.libs.xg7menus.menus.holders.ConfirmationMenuHolder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public abstract class ConfirmationMenu extends Menu {

    public ConfirmationMenu(Plugin plugin, String id, String title, InventoryType type) {
        super(plugin, id, title, type);

    }

    public ConfirmationMenu(Plugin plugin, String id, String title, int size) {
        super(plugin, id, title, size);

    }

    public abstract <T extends MenuEvent> void confirm(T event);
    public abstract <T extends MenuEvent> void cancel(T event);

    @Override
    public void open(Player player) {
        ConfirmationMenuHolder holder = new ConfirmationMenuHolder(id, plugin, Text.format(title, XG7Plugins.getInstance()).getWithPlaceholders(player),size,type, this, player);
        player.openInventory(holder.getInventory());
        putItems(player, holder);

    }



}
