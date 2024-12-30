package com.xg7plugins.libs.xg7menus.menus.gui;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7menus.menus.BaseMenu;
import com.xg7plugins.libs.xg7menus.menus.holders.MenuHolder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public abstract class Menu extends BaseMenu {

    protected final InventoryType type;
    protected final String title;
    protected final int size;

    public Menu(Plugin plugin, String id, String title, InventoryType type) {
        super(plugin, id);
        this.type = type;
        this.size = 0;
        this.title = title;
    }
    public Menu(Plugin plugin, String id, String title, int size) {
        super(plugin, id);
        this.type = null;
        this.size = size;
        this.title = title;
    }

    @Override
    public void open(Player player) {
        MenuHolder holder = new MenuHolder(id, plugin, Text.format(title, XG7Plugins.getInstance()).getWithPlaceholders(player),size,type, this,player);
        player.openInventory(holder.getInventory());
        putItems(player, holder);
    }
}
