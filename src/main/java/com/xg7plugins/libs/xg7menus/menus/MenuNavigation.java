package com.xg7plugins.libs.xg7menus.menus;

import com.xg7plugins.libs.xg7menus.menus.gui.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

@AllArgsConstructor
@Getter
public class MenuNavigation {

    private final HashMap<String, Menu> menus;

    public void openMenu(String id, Player player) {
        menus.get(id).open(player);
    }
}
