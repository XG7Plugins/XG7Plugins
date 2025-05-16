package com.xg7plugins.modules.xg7menus.menus.menus.gui.menus;

import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.holders.BasicMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.holders.MenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.MenuConfigurations;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public abstract class Menu implements BasicMenu {

    private MenuConfigurations menuConfigs;

    @Override
    public void open(Player player) {
        MenuHolder holder = new MenuHolder(this, player);

        XG7Menus.registerHolder(holder);
    }
    @Override
    public void close(BasicMenuHolder menuHolder) {
        menuHolder.getPlayer().closeInventory();
    }

    @Override
    public MenuConfigurations getMenuConfigs() {
        return menuConfigs;
    }


}
