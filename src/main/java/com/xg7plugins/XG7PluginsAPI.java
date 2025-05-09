package com.xg7plugins;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.data.database.processor.DatabaseProcessor;
import com.xg7plugins.dependencies.DependencyManager;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.managers.ManagerRegistery;
import com.xg7plugins.modules.ModuleManager;
import com.xg7plugins.tasks.CooldownManager;
import com.xg7plugins.tasks.TaskManager;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class XG7PluginsAPI {

    public static <T extends Plugin> T getXG7Plugin(Class<T> pluginClass) {
        return (T) XG7Plugins.getInstance().getPlugins().values().stream().filter(plugin -> pluginClass == plugin.getClass()).findFirst().orElse(null);
    }
    public static <T extends Plugin> T getXG7Plugins(String name) {
        return (T) XG7Plugins.getInstance().getPlugins().get(name);
    }

    // Get the instance of the XG7Plugins plugin
    public static TaskManager taskManager() {
        return ManagerRegistery.get(XG7Plugins.getInstance(), TaskManager.class);
    }

    public static DatabaseManager databaseManager() {
        return ManagerRegistery.get(XG7Plugins.getInstance(), DatabaseManager.class);
    }

    public static EventManager eventManager() {
        return ManagerRegistery.get(XG7Plugins.getInstance(), EventManager.class);
    }

    public static CooldownManager cooldownManager() {
        return ManagerRegistery.get(XG7Plugins.getInstance(), CooldownManager.class);
    }
    public static LangManager langManager() {
        return ManagerRegistery.get(XG7Plugins.getInstance(), LangManager.class);
    }
    public static ModuleManager moduleManager() {
        return ManagerRegistery.get(XG7Plugins.getInstance(), ModuleManager.class);
    }
    public static ConfigManager configManager(Plugin plugin) {
        return ManagerRegistery.get(plugin, ConfigManager.class);
    }
    public static CommandManager commandManager(Plugin plugin) {
        return ManagerRegistery.get(plugin, CommandManager.class);
    }
    public static JsonManager jsonManager() {
        return ManagerRegistery.get(XG7Plugins.getInstance(), JsonManager.class);
    }
    public static DependencyManager dependencyManager() {
        return ManagerRegistery.get(XG7Plugins.getInstance(), DependencyManager.class);
    }

    public static DatabaseProcessor dbProcessor() {
        return databaseManager().getProcessor();
    }

    public static boolean isDependencyEnabled(String name) {
        return dependencyManager().isLoaded(name);
    }
    public static boolean isGeyserFormsEnabled() {
        return isDependencyEnabled("floodgate") && Config.mainConfigOf(XG7Plugins.getInstance()).get("enable-geyser-forms",Boolean.class).orElse(false);
    }

    public static boolean isWorldEnabled(Plugin plugin, String world) {
        return plugin.getEnabledWorlds().contains(world);
    }
    public static boolean isWorldEnabled(Plugin plugin, World world) {
        return isWorldEnabled(plugin, world.getName());
    }
    public static boolean isInWorldEnabled(Plugin plugin, Player player) {
        return isWorldEnabled(plugin, player.getWorld());
    }




}
