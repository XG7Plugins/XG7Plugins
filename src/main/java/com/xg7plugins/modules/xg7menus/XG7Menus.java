package com.xg7plugins.modules.xg7menus;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Listener;
import com.xg7plugins.modules.Module;
import com.xg7plugins.modules.xg7menus.handlers.MenuHandler;
import com.xg7plugins.modules.xg7menus.handlers.PlayerMenuHandler;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.MenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PlayerMenuHolder;
import com.xg7plugins.modules.xg7menus.task.MenuUpdaterTimerTask;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.PluginKey;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class XG7Menus implements Module {

    private boolean enabled;

    private final HashMap<UUID, PlayerMenuHolder> playerMenusMap = new HashMap<>();
    private final HashMap<UUID, MenuHolder> menuHolders = new HashMap<>();

    private final HashMap<PluginKey, BasicMenu> registeredMenus = new HashMap<>();

    @Override
    public void onInit() {
        XG7Plugins.getInstance().getDebug().log("XG7Menus initialized");
    }

    public List<Listener> loadListeners() {
        return Arrays.asList(new MenuHandler(), new PlayerMenuHandler());
    }

    @Override
    public void onDisable() {
        playerMenusMap.forEach((id, menu) -> {
            Player player = Bukkit.getPlayer(id);
            if (player == null) return;
            menu.getMenu().close(menu);
        });
    }

    public void closeAllMenus(Player player) {
        player.closeInventory();
        if (XG7Menus.hasPlayerMenuHolder(player.getUniqueId())) {
            PlayerMenuHolder holder = XG7Menus.getPlayerMenuHolder(player.getUniqueId());

            holder.getMenu().close(holder);
        }
    }

    @Override
    public void onReload() {
        Bukkit.getOnlinePlayers().forEach(this::closeAllMenus);
    }

    @Override
    public String getName() {
        return "XG7Menus";
    }

    public void registerMenus(List<BasicMenu> menus) {
        if (menus == null) return;
        for (BasicMenu menu : menus) {
            if (!menu.getMenuConfigs().isEnabled()) continue;
            XG7Plugins.getInstance().getDebug().info("menus", "Registering menu " + menu.getMenuConfigs().getId());
            registeredMenus.put(PluginKey.of(menu.getMenuConfigs().getPlugin(), menu.getMenuConfigs().getId()), menu);
        }
    }
    public <T extends BasicMenu> T getMenu(Plugin plugin, String id) {
        return getMenu(PluginKey.of(plugin, id));
    }

    public <T extends BasicMenu> T getMenu(PluginKey key) {
        return (T) registeredMenus.get(key);
    }

    public void unregisterMenu(PluginKey key) {
        registeredMenus.remove(key);
    }
    public void unregisterMenu(Plugin plugin, String id) {
        registeredMenus.remove(PluginKey.of(plugin, id));
    }

    public static void registerPlayerMenuHolder(PlayerMenuHolder holder) {
        XG7Plugins.getInstance().getDebug().info("menus", "Registering player menu holder for " + holder.getPlayer().getUniqueId());

        XG7Plugins.getAPI().menus().getPlayerMenusMap().put(holder.getPlayer().getUniqueId(), holder);
    }

    public static void removePlayerMenuHolder(UUID playerId) {
        XG7Plugins.getInstance().getDebug().info("menus", "Removing player menu holder for " + playerId);

        XG7Plugins.getAPI().menus().getPlayerMenusMap().remove(playerId);
    }

    public static <T extends PlayerMenuHolder> T getPlayerMenuHolder(UUID playerId) {
        return (T) XG7Plugins.getAPI().menus().getPlayerMenusMap().get(playerId);
    }
    public static boolean hasPlayerMenuHolder(UUID playerId) {
        return XG7Plugins.getAPI().menus().getPlayerMenusMap().containsKey(playerId);
    }

    public static void registerHolder(MenuHolder holder) {
        XG7Plugins.getInstance().getDebug().info("menus", "Registering menu holder for " + holder.getPlayer().getUniqueId());
        XG7Plugins.getAPI().menus().getMenuHolders().put(holder.getPlayer().getUniqueId(), holder);
    }
    public static void removeHolder(UUID playerID) {
        XG7Plugins.getInstance().getDebug().info("menus", "Removing menu holder for " + playerID);
        XG7Plugins.getAPI().menus().getMenuHolders().remove(playerID);
    }

    public static <T extends MenuHolder> T getHolder(UUID playerId) {
        return (T) XG7Plugins.getAPI().menus().getMenuHolders().get(playerId);
    }

    public static boolean hasHolder(UUID playerId) {
        return XG7Plugins.getAPI().menus().getMenuHolders().containsKey(playerId);
    }

    @Override
    public List<TimerTask> loadTasks() {
        return Collections.singletonList(new MenuUpdaterTimerTask(this));
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


}