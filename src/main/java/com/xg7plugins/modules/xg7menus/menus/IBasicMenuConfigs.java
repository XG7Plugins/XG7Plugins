package com.xg7plugins.modules.xg7menus.menus;

import com.xg7plugins.boot.Plugin;

public interface IBasicMenuConfigs {

    String getId();
    Plugin getPlugin();
    default MenuActions[] allowedActions() {
        return new MenuActions[0];
    }
    default boolean isEnabled() {
        return true;
    }
    // -1 means no repeating update
    default int repeatingUpdateMills(int slot) {
        return -1;
    }

    static IBasicMenuConfigs of(Plugin plugin, String id) {
        return new IBasicMenuConfigs() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public Plugin getPlugin() {
                return plugin;
            }
        };
    }

    static IBasicMenuConfigs of(Plugin plugin, String id, MenuActions[] allowedActions, boolean enabled) {
        return new IBasicMenuConfigs() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public Plugin getPlugin() {
                return plugin;
            }

            @Override
            public MenuActions[] allowedActions() {
                return allowedActions;
            }
            @Override
            public boolean isEnabled() {
                return enabled;
            }
        };
    }
}
