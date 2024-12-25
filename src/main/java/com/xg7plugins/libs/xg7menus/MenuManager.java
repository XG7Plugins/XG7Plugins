package com.xg7plugins.libs.xg7menus;

import com.xg7plugins.libs.xg7menus.menus.BaseMenu;
import com.xg7plugins.libs.xg7menus.menus.holders.PlayerMenuHolder;
import com.xg7plugins.libs.xg7menus.menus.player.PlayerMenu;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class MenuManager {

    private final HashMap<UUID, PlayerMenuHolder> playerMenusMap = new HashMap<>();
    private final HashMap<String, BaseMenu> registeredMenus = new HashMap<>();


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
        Arrays.stream(menus).forEach(menu -> registeredMenus.put(menu.getPlugin().getName() + ":" + menu.getId(), menu));
    }

    public BaseMenu getMenu(String id) {
        return registeredMenus.get(id);
    }

    public void disable() {
        playerMenusMap.forEach((id, menu) -> {
            Player player = Bukkit.getPlayer(id);
            ((PlayerMenu) menu.getMenu()).close(player);
        });
    }

}
