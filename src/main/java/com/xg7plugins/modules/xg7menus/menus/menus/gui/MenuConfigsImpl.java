package com.xg7plugins.modules.xg7menus.menus.menus.gui;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import org.bukkit.event.inventory.InventoryType;

import java.util.EnumSet;

public class MenuConfigsImpl implements IMenuConfigurations {

    private final Plugin plugin;
    private final String id;
    private final String title;
    private InventoryType type;
    private int rows;
    private final EnumSet<MenuAction> allowedActions;
    private final boolean enabled;

    public MenuConfigsImpl(Plugin plugin, String id, String title, int rows, EnumSet<MenuAction> allowedActions, boolean enabled) {
        this.plugin = plugin;
        this.id = id;
        this.title = title;
       this.rows = rows;
        this.allowedActions = allowedActions;
        this.enabled = enabled;
    }
    public MenuConfigsImpl(Plugin plugin, String id, String title, InventoryType type, EnumSet<MenuAction> allowedActions, boolean enabled) {
        this.plugin = plugin;
        this.id = id;
        this.title = title;
        this.type = type;
        this.allowedActions = allowedActions;
        this.enabled = enabled;
    }
    public MenuConfigsImpl(Plugin plugin, String id, String title, int rows, boolean enabled) {
        this(plugin, id, title, rows, null, enabled);
    }

    public MenuConfigsImpl(Plugin plugin, String id, String title, InventoryType type, boolean enabled) {
        this(plugin, id, title, type, null, enabled);
    }

    public MenuConfigsImpl(Plugin plugin, String id, String title, int rows) {
        this(plugin, id, title, rows, null, true);
    }

    public MenuConfigsImpl(Plugin plugin, String id, String title, InventoryType type) {
        this(plugin, id, title, type, null, true);
    }


    @Override
    public InventoryType getInventoryType() {
        return type;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public EnumSet<MenuAction> allowedActions() {
        return allowedActions;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
