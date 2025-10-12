package com.xg7plugins.modules.xg7menus.builders;

import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
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
