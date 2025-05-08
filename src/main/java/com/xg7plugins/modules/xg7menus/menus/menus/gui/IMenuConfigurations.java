package com.xg7plugins.modules.xg7menus.menus.menus.gui;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.IBasicMenuConfigs;
import org.bukkit.event.inventory.InventoryType;

import java.util.EnumSet;

public interface IMenuConfigurations extends IBasicMenuConfigs {

    default InventoryType getInventoryType() {
        return null;
    }
    default int getRows() {
        return 0;
    }
    String getTitle();

    static MenuConfigsImpl of(Plugin plugin, String id, String title, int rows, EnumSet<MenuAction> allowedActions, boolean enabled) {
        return new MenuConfigsImpl(plugin, id, title, rows, allowedActions, enabled);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, InventoryType type, EnumSet<MenuAction> allowedActions, boolean enabled) {
        return new MenuConfigsImpl(plugin, id, title, type, allowedActions, enabled);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, int rows, boolean enabled) {
        return new MenuConfigsImpl(plugin, id, title, rows, null, enabled);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, InventoryType type, boolean enabled) {
        return new MenuConfigsImpl(plugin, id, title, type, null, enabled);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, int rows) {
        return new MenuConfigsImpl(plugin, id, title, rows, null, true);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, InventoryType type) {
        return new MenuConfigsImpl(plugin, id, title, type, null, true);
    }


}
