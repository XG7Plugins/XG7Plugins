package com.xg7plugins.plugin_menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.holders.BasicMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.menus.Menu;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class AnimatedMenuTest extends Menu {
    public AnimatedMenuTest() {
        super(MenuConfigurations.of(XG7Plugins.getInstance(), "animated-menu", "Test", 3));
    }

    @Override
    public List<Item> getItems(Player player) {
        return Collections.emptyList();
    }

    @Override
    public void onClick(ActionEvent event) {
        super.onClick(event);
    }

    @Override
    public void onOpen(MenuEvent event) {
        super.onOpen(event);
    }

    @Override
    public void onRepeatingUpdate(BasicMenuHolder holder) {
        super.onRepeatingUpdate(holder);
    }
}
