package com.xg7plugins.modules.xg7menus.menus.interfaces.gui;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.BasicMenuConfigs;
import com.xg7plugins.utils.Pair;
import org.bukkit.event.inventory.InventoryType;

import java.util.EnumSet;
import java.util.List;

public interface MenuConfigurations extends BasicMenuConfigs {

    default InventoryType getInventoryType() {
        return null;
    }
    default int getRows() {
        return 0;
    }
    String getTitle();

    static MenuConfigsImpl of(Plugin plugin, String id, String title, InventoryType type, EnumSet<MenuAction> allowedActions, boolean enabled, List<Pair<String, String>> placeholders, long repeatingUpdateDelay) {
        return new MenuConfigsImpl(plugin, id, title, type, allowedActions, enabled, placeholders, repeatingUpdateDelay);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, int rows, EnumSet<MenuAction> allowedActions, boolean enabled, List<Pair<String, String>> placeholders, long repeatingUpdateDelay) {
        return new MenuConfigsImpl(plugin, id, title, rows, allowedActions, enabled, placeholders, repeatingUpdateDelay);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, int rows, EnumSet<MenuAction> allowedActions, boolean enabled) {
        return new MenuConfigsImpl(plugin, id, title, rows, allowedActions, enabled);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, int rows, EnumSet<MenuAction> allowedActions, boolean enabled, List<Pair<String, String>> placeholders) {
        return new MenuConfigsImpl(plugin, id, title, rows, allowedActions, enabled, placeholders);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, InventoryType type, EnumSet<MenuAction> allowedActions, boolean enabled) {
        return new MenuConfigsImpl(plugin, id, title, type, allowedActions, enabled);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, int rows, boolean enabled) {
        return new MenuConfigsImpl(plugin, id, title, rows, EnumSet.noneOf(MenuAction.class), enabled);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, InventoryType type, boolean enabled) {
        return new MenuConfigsImpl(plugin, id, title, type, EnumSet.noneOf(MenuAction.class), enabled);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, int rows) {
        return new MenuConfigsImpl(plugin, id, title, rows, EnumSet.noneOf(MenuAction.class), true);
    }
    static MenuConfigsImpl of(Plugin plugin, String id, String title, InventoryType type) {
        return new MenuConfigsImpl(plugin, id, title, type, EnumSet.noneOf(MenuAction.class), true);
    }


}
