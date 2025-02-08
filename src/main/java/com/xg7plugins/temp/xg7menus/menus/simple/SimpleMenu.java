package com.xg7plugins.temp.xg7menus.menus.simple;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.temp.xg7menus.MenuPrevents;
import com.xg7plugins.temp.xg7menus.events.MenuEvent;
import com.xg7plugins.temp.xg7menus.item.Item;
import com.xg7plugins.temp.xg7menus.menus.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SimpleMenu extends Menu {

    private List<Item> items;
    private Consumer<MenuEvent> onOpen;
    private Consumer<MenuEvent> onClose;

    public SimpleMenu(Plugin plugin, String id, String title, InventoryType type, List<Item> items, Consumer<MenuEvent> onOpen, Consumer<MenuEvent> onClose, Set<MenuPrevents> prevents) {
        super(plugin, id, title, type);
        this.items = items;
        this.onOpen = onOpen;
        this.onClose = onClose;
        if (prevents != null) setMenuPrevents(prevents);
    }

    public SimpleMenu(Plugin plugin, String id, String title, int size, List<Item> items, Consumer<MenuEvent> onOpen, Consumer<MenuEvent> onClose, Set<MenuPrevents> prevents) {
        super(plugin, id, title, size);
        this.items = items;
        this.onOpen = onOpen;
        this.onClose = onClose;
        if (prevents != null) setMenuPrevents(prevents);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected List<Item> items(Player player) {
        return this.items;
    }

    @Override
    public void onOpen(MenuEvent event) {
        if (onOpen != null) onOpen.accept(event);
    }
    @Override
    public void onClose(MenuEvent event) {
        if (onClose != null) onClose.accept(event);
    }

}
