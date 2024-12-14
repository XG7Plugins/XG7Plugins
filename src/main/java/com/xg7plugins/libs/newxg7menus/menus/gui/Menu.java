package com.xg7plugins.libs.newxg7menus.menus.gui;

import com.google.common.collect.Sets;
import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.MenuPrevents;
import com.xg7plugins.libs.newxg7menus.menus.BaseMenu;
import com.xg7plugins.libs.newxg7menus.menus.holders.MenuHolder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashSet;
import java.util.Set;

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
        MenuHolder holder = new MenuHolder(id, plugin, title,size,type, this,player);
        player.openInventory(holder.getInventory());
        putItems(player, holder);
    }
}
