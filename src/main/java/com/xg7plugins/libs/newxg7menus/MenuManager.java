package com.xg7plugins.libs.newxg7menus;

import com.xg7plugins.libs.newxg7menus.menus.BaseMenu;
import com.xg7plugins.libs.newxg7menus.menus.holders.PlayerMenuHolder;
import com.xg7plugins.libs.newxg7menus.menus.player.PlayerMenu;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class MenuManager {

    private final HashMap<UUID, PlayerMenuHolder> playerMenusMap = new HashMap<>();
    private final HashMap<String, BaseMenu> registredMenus = new HashMap<>();


    public void addPlayerMenu(UUID playerId, PlayerMenuHolder holder) {
        playerMenusMap.put(playerId, holder);
    }
    public void removePlayerMenu(UUID playerId) {
        playerMenusMap.remove(playerId);
    }
    public boolean hasPlayerMenu(UUID playerId) {
        return playerMenusMap.containsKey(playerId);
    }

    public void registerMenus(BaseMenu... menus) {
        Arrays.stream(menus).forEach(menu -> registredMenus.put(menu.getPlugin().getName() + ":" + menu.getId(), menu));
    }

}
