package com.xg7plugins.libs.xg7menus;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7menus.builders.BaseMenuBuilder;
import com.xg7plugins.libs.xg7menus.menus.BaseMenu;
import com.xg7plugins.libs.xg7menus.menus.player.PlayerMenu;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class MenuManager {

    private final XG7Plugins plugin;
    private final Map<String, BaseMenuBuilder<?,?>> builders = new HashMap<>();

    private final Map<UUID, PlayerMenu> playerMenuMap = new HashMap<>();

    private final Cache<String, BaseMenu> cachedMenus;

    public MenuManager(XG7Plugins plugin) {
        this.plugin = plugin;
        this.cachedMenus = Caffeine.newBuilder().expireAfterAccess(Text.convertToMilliseconds(plugin, plugin.getConfigsManager().getConfig("config").get("menu-cache-expires")), TimeUnit.MILLISECONDS).build();
    }
    public void addPlayerMenu(UUID id, PlayerMenu menu) {
        playerMenuMap.put(id, menu);
    }
    public void removePlayerMenu(UUID id) {
        if (!playerMenuMap.containsKey(id)) return;
        playerMenuMap.get(id).clear();
        playerMenuMap.remove(id);
    }
    public boolean cacheExistsPlayer(String id, Player player) {
       return cachedMenus.asMap().containsKey(id + ":" + player.getUniqueId());
    }
    public BaseMenu getMenuByPlayer(String id, Player player) {
        return cachedMenus.asMap().get(id + ":" + player.getUniqueId());
    }
    public BaseMenu removePlayer(String id, Player player) {
        return cachedMenus.asMap().remove(id + ":" + player.getUniqueId());
    }
    public void removePlayerFromAll(Player player) {
        cachedMenus.asMap().entrySet().removeIf(entry -> entry.getKey().endsWith(player.getUniqueId().toString()));
    }
    public void registerBuilder(String id, BaseMenuBuilder<?,?> builder) {
        builders.put(id, builder);
    }
    public BaseMenuBuilder<?,?> getBuilder(String id) {
        return builders.get(id);
    }
}
