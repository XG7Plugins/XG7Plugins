package com.xg7plugins.modules.xg7menus.menus;

import com.xg7plugins.boot.Plugin;

import java.util.EnumSet;

public interface IBasicMenuConfigs {

    String getId();
    Plugin getPlugin();
    default EnumSet<MenuAction> allowedActions() {
        return EnumSet.noneOf(MenuAction.class);
    }
    default boolean isEnabled() {
        return true;
    }
    // -1 means no repeating update
    default int repeatingUpdateMills() {
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

    static IBasicMenuConfigs of(Plugin plugin, String id, EnumSet<MenuAction> allowedActions, boolean enabled) {
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
            public EnumSet<MenuAction> allowedActions() {
                return allowedActions;
            }
            @Override
            public boolean isEnabled() {
                return enabled;
            }
        };
    }
}
