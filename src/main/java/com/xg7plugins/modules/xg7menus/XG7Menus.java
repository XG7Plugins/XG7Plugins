package com.xg7plugins.modules.xg7menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.events.Listener;
import com.xg7plugins.modules.Module;
import com.xg7plugins.modules.xg7menus.menuhandler.MenuHandler;
import com.xg7plugins.modules.xg7menus.menuhandler.PlayerMenuHandler;
import com.xg7plugins.modules.xg7menus.menus.IBasicMenu;
import com.xg7plugins.modules.xg7menus.menus.holders.MenuHolder;
import com.xg7plugins.modules.xg7menus.menus.holders.PlayerMenuHolder;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class XG7Menus implements Module {

    @Getter
    private static XG7Menus instance;

    @Getter
    private final HashMap<UUID, PlayerMenuHolder> playerMenusMap = new HashMap<>();
    @Getter
    private final HashMap<UUID, MenuHolder> menuHolders = new HashMap<>();

    private final HashMap<String, IBasicMenu> registeredMenus = new HashMap<>();

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

    @Override
    public String getName() {
        return "XG7Menus";
    }

    public void registerMenus(IBasicMenu... menus) {
        if (menus == null) return;
        for (IBasicMenu menu : menus) {
            if (!menu.getMenuConfigs().isEnabled()) continue;
            XG7Plugins.getInstance().getDebug().loading("Registering menu " + menu.getMenuConfigs().getId());
            registeredMenus.put(menu.getMenuConfigs().getPlugin().getName() + ":" + menu.getMenuConfigs().getId(), menu);
        }
    }
    public <T extends IBasicMenu> T getMenu(Plugin plugin, String id) {
        return (T) registeredMenus.get(plugin.getName() + ":" + id);
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
    public List<Task> loadTasks() {
        AtomicLong counter = new AtomicLong();
        return Collections.singletonList(new Task(
                XG7Plugins.getInstance(),
                "menu-task",
                true,
                true,
                1,
                TaskState.IDLE,
                () -> {
                    registeredMenus.values().forEach(menu -> {
                        if (menu.getMenuConfigs().repeatingUpdateMills() < 0) return;
                        if (counter.get() % menu.getMenuConfigs().repeatingUpdateMills() != 0) return;

                        menuHolders.values()
                                .stream()
                                .filter(holder -> holder.getMenu().getMenuConfigs().getId().equals(menu.getMenuConfigs().getId()))
                                .forEach(holder -> holder.getMenu().onRepeatingUpdate(holder));

                        playerMenusMap.values()
                                .stream()
                                .filter(holder -> holder.getMenu().getMenuConfigs().getId().equals(menu.getMenuConfigs().getId()))
                                .forEach(holder -> holder.getMenu().onRepeatingUpdate(holder));


                    });
                    counter.incrementAndGet();
                }
        ));
    }




}