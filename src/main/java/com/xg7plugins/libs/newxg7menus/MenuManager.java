package com.xg7plugins.libs.newxg7menus;

import com.xg7plugins.libs.newxg7menus.menus.BaseMenu;
import com.xg7plugins.libs.newxg7menus.menus.player.PlayerMenu;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class MenuManager {

    private final HashMap<UUID, PlayerMenu> playerMenusMap = new HashMap<>();
    private final HashMap<String, BaseMenu> registredMenus = new HashMap<>();


    public void addPlayerMenu(UUID playerId, PlayerMenu menu) {
        playerMenusMap.put(playerId, menu);
    }
    public void removePlayerMenu(UUID playerId) {
        playerMenusMap.remove(playerId);
    }

    public void registerMenus(BaseMenu... menus) {
        Arrays.stream(menus).forEach(menu -> registredMenus.put(menu.getPlugin().getName() + ":" + menu.getId(), menu));
    }

}
