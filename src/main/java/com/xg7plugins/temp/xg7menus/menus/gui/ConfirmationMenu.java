package com.xg7plugins.temp.xg7menus.menus.gui;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.temp.xg7menus.events.MenuEvent;
import com.xg7plugins.temp.xg7menus.menus.holders.ConfirmationMenuHolder;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.TextCentralizer;
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
        ConfirmationMenuHolder holder = new ConfirmationMenuHolder(id, plugin, Text.detectLangOrText(plugin,player,title).join().getTextCentralized(TextCentralizer.PixelsSize.INV),size,type, this, player);
        player.openInventory(holder.getInventory());
        putItems(player, holder);

    }



}
