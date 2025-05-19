package com.xg7plugins.modules.xg7menus.builders;

import com.xg7plugins.modules.xg7menus.menus.menus.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.menus.Menu;
import com.xg7plugins.modules.xg7menus.simple.SimpleMenu;

public class MenuBuilder extends BasicMenuBuilder<MenuBuilder, Menu> {
    public MenuBuilder(MenuConfigurations configs) {
        super(configs);
    }

    @Override
    public Menu build() {
        return new SimpleMenu(
                (MenuConfigurations) menuConfigs,
                items,
                clickableItems,
                clickConsumer,
                dragConsumer,
                openEventConsumer,
                closeEventConsumer,
                updateAction,
                repeatingUpdateAction
        );
    }

    public static MenuBuilder inicialize(MenuConfigurations configurations) {
        return new MenuBuilder(configurations);
    }
}
