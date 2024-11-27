package com.xg7plugins.libs.newxg7menus;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.events.ClickEvent;
import com.xg7plugins.libs.newxg7menus.events.MenuEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class Menu<M extends Menu<M>> implements InventoryHolder {

    private HashMap<Integer, Consumer<MenuEvent>> clickActions = new HashMap<>();
    private final Plugin plugin;

}
