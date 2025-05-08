package com.xg7plugins.modules.xg7menus.builders;

import com.xg7plugins.modules.xg7menus.menus.menus.gui.IMenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.menus.Menu;
import com.xg7plugins.modules.xg7menus.simple.SimpleMenu;

public class MenuBuilder extends BasicMenuBuilder<MenuBuilder, Menu> {
    public MenuBuilder(IMenuConfigurations configs) {
        super(configs);
    }

    @Override
    public Menu build(Object... args) {
        return new SimpleMenu(
                (IMenuConfigurations) menuConfigs,
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

    public static MenuBuilder inicialize(IMenuConfigurations configurations) {
        return new MenuBuilder(configurations);
    }
}
