package com.xg7plugins.libs.xg7menus.menus.gui;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7menus.MenuPermissions;
import com.xg7plugins.libs.xg7menus.menus.BaseMenu;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class Menu extends BaseMenu implements InventoryHolder {

    protected Inventory inventory;

    public Menu(String id, String title, int size, Map<Integer, ItemStack> items, Map<Integer,Consumer<ClickEvent>> clicks, Consumer<ClickEvent> defaultClick, Consumer<MenuEvent> openEvent, Consumer<MenuEvent> closeEvent, EnumSet<MenuPermissions> permissions, HumanEntity player) {
        super(id,defaultClick,openEvent,closeEvent,items,clicks,permissions,player);
        this.inventory = Bukkit.createInventory(this, size, title);
        update();
    }
    public Menu(String id,String title, InventoryType type, Map<Integer, ItemStack> items, Map<Integer,Consumer<ClickEvent>> clicks, Consumer<ClickEvent> defaultClick, Consumer<MenuEvent> openEvent, Consumer<MenuEvent> closeEvent, EnumSet<MenuPermissions> permissions, HumanEntity player) {
        super(id,defaultClick, openEvent, closeEvent, items, clicks, permissions, player);
        this.inventory = Bukkit.createInventory(this, type, title);
        update();
    }

    public void setItem(int slot, ItemStack item) {
        items.put(slot, item);
    }
    public void setItem(int slot, ItemStack item, Consumer<ClickEvent> clickEvent) {
        items.put(slot, item);
        clickEvents.put(slot, clickEvent);
    }
    public void setClickEvent(int slot, Consumer<ClickEvent> clickEvent) {
        clickEvents.put(slot, clickEvent);
    }

    public void update() {
        items.forEach((key, value) -> inventory.setItem(key, value));
    }

    public void open() {
        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> player.openInventory(inventory));
    }
    public void close() {
        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> player.closeInventory());
    }

    public void updateItem(int slot, ItemBuilder builder) {
        inventory.setItem(slot, builder.toItemStack());
        items.put(slot, builder.toItemStack());
        if (builder.getEvent() != null) clickEvents.put(slot, builder.getEvent());
        else clickEvents.remove(slot);
    }
}
