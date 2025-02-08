package com.xg7plugins.temp.xg7menus.builders;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.temp.xg7menus.MenuException;
import com.xg7plugins.temp.xg7menus.MenuPrevents;
import com.xg7plugins.temp.xg7menus.events.MenuEvent;
import com.xg7plugins.temp.xg7menus.item.Item;
import com.xg7plugins.temp.xg7menus.menus.gui.Menu;
import com.xg7plugins.temp.xg7menus.menus.simple.SimpleMenu;
import com.xg7plugins.utils.Builder;
import org.bukkit.event.inventory.InventoryType;

import java.util.*;
import java.util.function.Consumer;

public class MenuBuilder<M extends Menu> implements Builder<M> {

    private final Plugin plugin;
    private final String id;
    private List<Item> items = new ArrayList<>();
    private String title;
    private int size;
    private InventoryType type;
    private Consumer<MenuEvent> onOpen;
    private Consumer<MenuEvent> onClose;
    private Set<MenuPrevents> prevents;

    public MenuBuilder(String id, Plugin plugin) {
        this.id = id;
        this.plugin = plugin;
    }

    public MenuBuilder title(String title) {
        this.title = title;
        return this;
    }

    public MenuBuilder items(List<Item> items) {
        this.items = items;
        return this;
    }
    public MenuBuilder items(Item... items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public MenuBuilder addItem(Item item) {
        this.items.add(item);
        return this;
    }

    public MenuBuilder size(int size) {
        this.size = size;
        return this;
    }

    public MenuBuilder type(InventoryType type) {
        this.type = type;
        return this;
    }

    public MenuBuilder onOpen(Consumer<MenuEvent> onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    public MenuBuilder onClose(Consumer<MenuEvent> onClose) {
        this.onClose = onClose;
        return this;
    }

    public MenuBuilder prevents(Set<MenuPrevents> prevents) {
        this.prevents = prevents;
        return this;
    }

    public MenuBuilder prevent(MenuPrevents prevent) {
        if (this.prevents == null) this.prevents = new HashSet<>();
        this.prevents.add(prevent);
        return this;
    }

    public MenuBuilder prevent(MenuPrevents... prevents) {
        if (this.prevents == null) this.prevents = new HashSet<>();

        this.prevents.addAll(Arrays.asList(prevents));
        return this;
    }

    public static MenuBuilder create(String id, Plugin plugin) {
        return new MenuBuilder(id, plugin);
    }

    @Override
    public M build(Object... args) {

        if (title == null) throw new MenuException(MenuException.ExceptionCause.BUILD_ERROR, "Title cannot be null");
        if (size == 0 && type == null) throw new MenuException(MenuException.ExceptionCause.BUILD_ERROR, "Size or inventory type must be set");
        if (size % 9 != 0 && type == null) throw new MenuException(MenuException.ExceptionCause.BUILD_ERROR, "Size must be a multiple of 9");
        if (size > 54 && type == null) throw new MenuException(MenuException.ExceptionCause.BUILD_ERROR, "Size cannot be greater than 54");

        if (size != 0) return (M) new SimpleMenu(plugin, id, title, size, items, onOpen, onClose, prevents);
        return (M) new SimpleMenu(plugin, id, title, type, items, onOpen, onClose, prevents);
    }
}
