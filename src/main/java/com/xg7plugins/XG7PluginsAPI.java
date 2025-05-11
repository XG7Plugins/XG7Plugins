package com.xg7plugins;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.CacheManager;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.data.database.processor.DatabaseProcessor;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.dependencies.DependencyManager;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.modules.ModuleManager;
import com.xg7plugins.server.ServerInfo;
import com.xg7plugins.tasks.CooldownManager;
import com.xg7plugins.tasks.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class XG7PluginsAPI {

    public static <T extends Plugin> T getXG7Plugin(Class<T> pluginClass) {
        return (T) XG7Plugins.getInstance().getPlugins().values().stream().filter(plugin -> pluginClass == plugin.getClass()).findFirst().orElse(null);
    }
    public static <T extends Plugin> T getXG7Plugins(String name) {
        return (T) XG7Plugins.getInstance().getPlugins().get(name);
    }
    public static Set<Plugin> getAllXG7Plugins() {
        return new HashSet<>(XG7Plugins.getInstance().getPlugins().values());
    }
    public static Set<String> getAllXG7PluginsName() {
        return XG7Plugins.getInstance().getPlugins().values().stream().map(Plugin::getName).collect(Collectors.toSet());
    }

    // Get the instance of the XG7Plugins plugin
    public static TaskManager taskManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), TaskManager.class);
    }
    public static DatabaseManager database() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), DatabaseManager.class);
    }
    public static CacheManager cacheManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), CacheManager.class);
    }
    public static EventManager eventManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), EventManager.class);
    }
    public static PacketEventManager packetEventManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), PacketEventManager.class);
    }
    public static CooldownManager cooldowns() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), CooldownManager.class);
    }
    public static LangManager langManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), LangManager.class);
    }
    public static ModuleManager moduleManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), ModuleManager.class);
    }
    public static ConfigManager configManager(Plugin plugin) {
        return ManagerRegistry.get(plugin, ConfigManager.class);
    }
    public static CommandManager commandManager(Plugin plugin) {
        return ManagerRegistry.get(plugin, CommandManager.class);
    }
    public static JsonManager jsonManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), JsonManager.class);
    }
    public static DependencyManager dependencyManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), DependencyManager.class);
    }

    public static DatabaseProcessor dbProcessor() {
        return database().getProcessor();
    }

    public static boolean isDependencyEnabled(String name) {
        return dependencyManager().isLoaded(name);
    }
    public static boolean isGeyserFormsEnabled() {
        return isDependencyEnabled("floodgate") && Config.mainConfigOf(XG7Plugins.getInstance()).get("enable-geyser-forms",Boolean.class).orElse(false);
    }
    public static boolean isWorldEnabled(Plugin plugin, String world) {
        return plugin.getEnvironmentConfig().getEnabledWorlds().contains(world);
    }
    public static boolean isWorldEnabled(Plugin plugin, World world) {
        return isWorldEnabled(plugin, world.getName());
    }
    public static boolean isInWorldEnabled(Plugin plugin, Player player) {
        return isWorldEnabled(plugin, player.getWorld());
    }

    public static CompletableFuture<PlayerData> requestPlayerData(UUID uuid) {
        return XG7Plugins.getInstance().getPlayerDataDAO().get(uuid);
    }
    public static CompletableFuture<PlayerData> requestPlayerData(Player player) {
        return requestPlayerData(player.getUniqueId());
    }

    public static Set<String> getAllPlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet());
    }
    public static Set<UUID> getAllPlayerUUIDs() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
    }
    public static Set<Player> getAllPlayers() {
        return new HashSet<>(Bukkit.getOnlinePlayers());
    }

    public static ServerInfo.Software getServerSoftware() {
        return XG7Plugins.getInstance().getServerInfo().getSoftware();
    }

    public static ServerInfo getServerInfo() {
        return XG7Plugins.getInstance().getServerInfo();
    }



}
