package com.xg7plugins.modules.xg7menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.Listener;
import com.xg7plugins.modules.Module;
import com.xg7plugins.modules.xg7menus.handlers.MenuHandler;
import com.xg7plugins.modules.xg7menus.handlers.PlayerMenuHandler;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.MenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PlayerMenuHolder;
import com.xg7plugins.modules.xg7menus.task.MenuUpdaterTimerTask;
import com.xg7plugins.tasks.tasks.TimerTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class XG7Menus implements Module {

    @Getter
    private static XG7Menus instance;

    private final HashMap<UUID, PlayerMenuHolder> playerMenusMap = new HashMap<>();
    private final HashMap<UUID, MenuHolder> menuHolders = new HashMap<>();

    private final HashMap<String, BasicMenu> registeredMenus = new HashMap<>();

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

    public void registerMenus(BasicMenu... menus) {
        if (menus == null) return;
        for (BasicMenu menu : menus) {
            if (!menu.getMenuConfigs().isEnabled()) continue;
            XG7Plugins.getInstance().getDebug().info("Registering menu " + menu.getMenuConfigs().getId());
            registeredMenus.put(menu.getMenuConfigs().getPlugin().getName() + ":" + menu.getMenuConfigs().getId(), menu);
        }
    }
    public <T extends BasicMenu> T getMenu(Plugin plugin, String id) {
        return (T) registeredMenus.get(plugin.getName() + ":" + id);
    }

    public void unregisterMenu(Plugin plugin, String id) {
        registeredMenus.remove(plugin.getName() + ":" + id);
    }

    public static void registerPlayerMenuHolder(PlayerMenuHolder holder) {
        XG7Plugins.getInstance().getDebug().info("Registering player menu holder for " + holder.getPlayer().getUniqueId());
        XG7Menus.getInstance().getPlayerMenusMap().put(holder.getPlayer().getUniqueId(), holder);
    }

    public static void removePlayerMenuHolder(UUID playerId) {
        XG7Plugins.getInstance().getDebug().info("Removing player menu holder for " + playerId);
        XG7Menus.getInstance().getPlayerMenusMap().remove(playerId);
    }

    public static <T extends PlayerMenuHolder> T getPlayerMenuHolder(UUID playerId) {
        return (T) XG7Menus.getInstance().getPlayerMenusMap().get(playerId);
    }
    public static boolean hasPlayerMenuHolder(UUID playerId) {
        return XG7Menus.getInstance().getPlayerMenusMap().containsKey(playerId);
    }

    public static void registerHolder(MenuHolder holder) {
        XG7Plugins.getInstance().getDebug().info("Registering menu holder for " + holder.getPlayer().getUniqueId());
        XG7Menus.getInstance().getMenuHolders().put(holder.getPlayer().getUniqueId(), holder);
    }
    public static void removeHolder(UUID playerID) {
        XG7Plugins.getInstance().getDebug().info("Removing menu holder for " + playerID);
        XG7Menus.getInstance().getMenuHolders().remove(playerID);
    }

    public static <T extends MenuHolder> T getHolder(UUID playerId) {
        return (T) XG7Menus.getInstance().getMenuHolders().get(playerId);
    }

    public static boolean hasHolder(UUID playerId) {
        return XG7Menus.getInstance().getMenuHolders().containsKey(playerId);
    }

    @Override
    public Map<String, ExecutorService> getExecutors() {
        return Collections.singletonMap("menus", Executors.newSingleThreadExecutor());
    }

    @Override
    public List<TimerTask> loadTasks() {
        return Collections.singletonList(new MenuUpdaterTimerTask(this));
    }




}