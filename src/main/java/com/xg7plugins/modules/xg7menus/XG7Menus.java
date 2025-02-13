package com.xg7plugins.modules.xg7menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.Listener;
import com.xg7plugins.modules.Module;
import com.xg7plugins.modules.xg7menus.menuhandler.MenuHandler;
import com.xg7plugins.modules.xg7menus.menuhandler.PlayerMenuHandler;
import com.xg7plugins.modules.xg7menus.menus.BaseMenu;
import com.xg7plugins.modules.xg7menus.menus.holders.PlayerMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.player.PlayerMenu;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class XG7Menus implements Module {

    @Getter
    private static XG7Menus instance;

    private final HashMap<UUID, PlayerMenuHolder> playerMenusMap = new HashMap<>();
    private final HashMap<String, BaseMenu> registeredMenus = new HashMap<>();


    @Override
    public void onInit() {
        instance = this;

        XG7Plugins.getInstance().getDebug().loading("XG7Menus initialized");
    }

    public List<Listener> loadListeners() {
        return Arrays.asList(new MenuHandler(), new PlayerMenuHandler());
    }

    @Override
    public void onDisable() {
        playerMenusMap.forEach((id, menu) -> {
            Player player = Bukkit.getPlayer(id);
            ((PlayerMenu) menu.getMenu()).close(player);
        });
    }

    @Override
    public String getName() {
        return "XG7Menus";
    }

    public void registerMenus(BaseMenu... menus) {
        if (menus == null) return;
        for (BaseMenu menu : menus) {
            XG7Plugins.getInstance().getDebug().loading("Registering menu " + menu.getId());
            registeredMenus.put(menu.getPlugin().getName() + ":" + menu.getId(), menu);
        }
    }
    public <T extends BaseMenu> T getMenu(Plugin plugin, String id) {
        return (T) registeredMenus.get(plugin.getName() + ":" + id);
    }

    public void registerPlayerMenuHolder(UUID playerId, PlayerMenuHolder holder) {
        XG7Plugins.getInstance().getDebug().info("menus", "Registering player menu holder for " + playerId);
        playerMenusMap.put(playerId, holder);
    }

    public void removePlayerMenuHolder(UUID playerId) {
        XG7Plugins.getInstance().getDebug().info("menus", "Removing player menu holder for " + playerId);
        playerMenusMap.remove(playerId);
    }

    public <T extends PlayerMenuHolder> T getPlayerMenuHolder(UUID playerId) {
        XG7Plugins.getInstance().getDebug().info("menus", "Getting player menu holder for " + playerId);
        return (T) playerMenusMap.get(playerId);
    }

    public boolean hasPlayerMenuHolder(UUID playerId) {
        return playerMenusMap.containsKey(playerId);
    }

}