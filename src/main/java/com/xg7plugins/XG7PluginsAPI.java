package com.xg7plugins;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.CacheManager;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.config.core.MainConfigSection;
import com.xg7plugins.data.database.dao.Repository;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.processor.DatabaseProcessor;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.dependencies.DependencyManager;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.modules.ModuleManager;
import com.xg7plugins.server.ServerInfo;
import com.xg7plugins.cooldowns.CooldownManager;
import com.xg7plugins.tasks.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * API utility class that provides centralized access to all components and functionalities
 * of the XG7Plugins framework.
 * <p>
 * This class contains static methods that allow accessing managers, server information,
 * player data, and other functionalities of the XG7Plugins ecosystem without the need to
 * instantiate objects or manually manage dependencies.
 *
 * @author DaviXG7
 */
public class XG7PluginsAPI {

    /**
     * Gets an instance of a specific XG7 plugin by its class type.
     *
     * @param <T>         The type of plugin to return
     * @param pluginClass The class of the desired plugin
     * @return The requested plugin instance, or null if not found
     */
    public static <T extends Plugin> T getXG7Plugin(Class<T> pluginClass) {
        return (T) XG7Plugins.getInstance().getPlugins().values().stream().filter(plugin -> pluginClass == plugin.getClass()).findFirst().orElse(null);
    }

    /**
     * Gets an instance of an XG7 plugin by its name.
     *
     * @param <T>  The type of plugin to return
     * @param name The name of the plugin
     * @return The requested plugin instance, or null if not found
     */
    public static <T extends Plugin> T getXG7Plugins(String name) {
        return (T) XG7Plugins.getInstance().getPlugins().get(name);
    }

    /**
     * Gets a set containing all registered XG7 plugin instances.
     *
     * @return A set of all XG7 plugin instances
     */
    public static Set<Plugin> getAllXG7Plugins() {
        return new HashSet<>(XG7Plugins.getInstance().getPlugins().values());
    }

    /**
     * Gets a set containing the names of all registered XG7 plugins.
     *
     * @return A set with all XG7 plugin names
     */
    public static Set<String> getAllXG7PluginsName() {
        return XG7Plugins.getInstance().getPlugins().values().stream().map(Plugin::getName).collect(Collectors.toSet());
    }

    /**
     * Gets the task manager that controls scheduled tasks.
     *
     * @return The global TaskManager instance
     */
    public static TaskManager taskManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), TaskManager.class);
    }

    /**
     * Gets the database manager that handles connections and persistence operations.
     *
     * @return The global DatabaseManager instance
     */
    public static DatabaseManager database() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), DatabaseManager.class);
    }

    /**
     * Gets the cache manager that handles in-memory data.
     *
     * @return The global CacheManager instance
     */
    public static CacheManager cacheManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), CacheManager.class);
    }

    /**
     * Gets the system's Bukkit event manager.
     *
     * @return The global EventManager instance
     */
    public static EventManager eventManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), EventManager.class);
    }

    /**
     * Gets the network packet events manager.
     *
     * @return The global PacketEventManager instance
     */
    public static PacketEventManager packetEventManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), PacketEventManager.class);
    }

    /**
     * Gets the cooldown manager for controlling time between actions.
     *
     * @return The global CooldownManager instance
     */
    public static CooldownManager cooldowns() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), CooldownManager.class);
    }

    /**
     * Gets the system's language manager.
     *
     * @return The global LangManager instance
     */
    public static LangManager langManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), LangManager.class);
    }

    /**
     * Gets the system's module manager.
     *
     * @return The global ModuleManager instance
     */
    public static ModuleManager moduleManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), ModuleManager.class);
    }

    /**
     * Gets the configuration manager for a specific plugin.
     *
     * @param plugin The plugin to get the configuration manager for
     * @return The ConfigManager associated with the specified plugin
     */
    public static ConfigManager configManager(Plugin plugin) {
        return ManagerRegistry.get(plugin, ConfigManager.class);
    }

    /**
     * Gets the command manager for a specific plugin.
     *
     * @param plugin The plugin to get the command manager for
     * @return The CommandManager associated with the specified plugin
     */
    public static CommandManager commandManager(Plugin plugin) {
        return ManagerRegistry.get(plugin, CommandManager.class);
    }

    /**
     * Gets a list of all registered commands for a specific plugin.
     *
     * @param plugin The plugin to get commands for
     * @return A list containing all registered Command instances
     */
    public static List<Command> commandListOf(Plugin plugin) {
        return commandManager(plugin).getCommandList();
    }

    /**
     * Gets a map of all registered commands and their aliases for a specific plugin.
     * The map keys are command names/aliases, and values are the Command instances.
     *
     * @param plugin The plugin to get commands for
     * @return A map containing command names/aliases mapped to Command instances
     */
    public static Map<String, Command> commandMapOf(Plugin plugin) {
        return commandManager(plugin).getMappedCommands();
    }

    /**
     * Gets the system's JSON manager.
     *
     * @return The global JsonManager instance
     */
    public static JsonManager jsonManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), JsonManager.class);
    }

    /**
     * Gets the dependency manager that controls external plugins.
     *
     * @return The global DependencyManager instance
     */
    public static DependencyManager dependencyManager() {
        return ManagerRegistry.get(XG7Plugins.getInstance(), DependencyManager.class);
    }

    /**
     * Gets the database processor for low-level database operations.
     *
     * @return The DatabaseProcessor instance
     */
    public static DatabaseProcessor dbProcessor() {
        return database().getProcessor();
    }

    /**
     * Checks if a specific dependency is loaded and enabled.
     *
     * @param name The name of the dependency to check
     * @return true if the dependency is enabled, false otherwise
     */
    public static boolean isDependencyEnabled(String name) {
        return dependencyManager().isLoaded(name);
    }

    /**
     * Checks if Geyser forms support is enabled.
     * Requires the Floodgate plugin to be loaded and the option enabled in config.
     *
     * @return true if Geyser forms support is enabled, false otherwise
     */
    public static boolean isGeyserFormsEnabled() {
        return dependencyManager().exists("floodgate") && Config.of(XG7Plugins.getInstance(), MainConfigSection.class).isGeyserFormsEnabled();
    }

    /**
     * Checks if a specific world is enabled for a plugin.
     *
     * @param plugin The plugin to check
     * @param world  The name of the world to check
     * @return true if the world is enabled for the plugin, false otherwise
     */
    public static boolean isEnabledWorld(Plugin plugin, String world) {
        return plugin.getEnvironmentConfig().getEnabledWorlds().contains(world);
    }

    /**
     * Checks if a specific world is enabled for a plugin.
     *
     * @param plugin The plugin to check
     * @param world  The World object to check
     * @return true if the world is enabled for the plugin, false otherwise
     */
    public static boolean isEnabledWorld(Plugin plugin, World world) {
        return isEnabledWorld(plugin, world.getName());
    }

    /**
     * Checks if the world the player is currently in is enabled for a plugin.
     *
     * @param plugin The plugin to check
     * @param player The player whose world will be checked
     * @return true if the player's world is enabled for the plugin, false otherwise
     */
    public static boolean isInAnEnabledWorld(Plugin plugin, Player player) {
        return isEnabledWorld(plugin, player.getWorld());
    }

    /**
     * Gets a list of enabled world names for a specific plugin.
     *
     * @param plugin The plugin to get enabled worlds for
     * @return A list of enabled world names
     */
    public static List<String> getEnabledWorldsOf(Plugin plugin) {
        return plugin.getEnvironmentConfig().getEnabledWorlds();
    }

    /**
     * Gets a repository instance by its class type.
     *
     * @param <ID>  The type of the entity ID
     * @param <T>   The type of the entity
     * @param <U>   The type of the repository
     * @param clazz The repository class to get
     * @return The requested repository instance
     */
    public static <ID,T extends Entity<?, ?>, U extends Repository<ID,T>> U getRepository(Class<U> clazz) {
        return database().getDaoManager().getRepository(clazz);
    }

    /**
     * Gets a list of all registered repositories.
     *
     * @return A list containing all repository instances
     */
    public static List<Repository> getRepositories() {
        return database().getDaoManager().getAllRepositories();
    }

    /**
     * Gets a list of all repositories registered for a specific plugin.
     *
     * @param plugin The plugin to get repositories for
     * @return A list of repositories associated with the plugin
     */
    public static List<Repository> getRepositoriesByPlugin(Plugin plugin) {
        return database().getDaoManager().getAllRepositoriesByPlugin(plugin);
    }

    /**
     * Requests player data by UUID asynchronously.
     *
     * @param uuid The player's UUID
     * @return A CompletableFuture that will contain the player data when available
     */
    public static CompletableFuture<PlayerData> requestPlayerData(UUID uuid) {
        return getRepository(PlayerDataRepository.class).getAsync(uuid);
    }

    /**
     * Requests player data from the player instance asynchronously.
     *
     * @param player The player to get data for
     * @return A CompletableFuture that will contain the player data when available
     */
    public static CompletableFuture<PlayerData> requestPlayerData(Player player) {
        return requestPlayerData(player.getUniqueId());
    }

    /**
     * Gets player data by UUID synchronously.
     *
     * @param uuid The player's UUID
     * @return The player data associated with the UUID
     */
    public static PlayerData getPlayerData(UUID uuid) {
        return getRepository(PlayerDataRepository.class).get(uuid);
    }

    /**
     * Gets player data from the player instance synchronously.
     *
     * @param player The player to get data for
     * @return The player data associated with the player
     */
    public static PlayerData getPlayerData(Player player) {
        return getRepository(PlayerDataRepository.class).get(player.getUniqueId());
    }

    /**
     * Gets a set with the names of all online players.
     *
     * @return A set containing the names of all online players
     */
    public static Set<String> getAllPlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet());
    }

    /**
     * Gets a set with the UUIDs of all online players.
     *
     * @return A set containing the UUIDs of all online players
     */
    public static Set<UUID> getAllPlayerUUIDs() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
    }

    /**
     * Gets a set with instances of all online players.
     *
     * @return A set containing instances of all online players
     */
    public static Set<Player> getAllPlayers() {
        return new HashSet<>(Bukkit.getOnlinePlayers());
    }

    /**
     * Gets the server software type (Bukkit, Spigot, Paper, etc).
     *
     * @return The ServerInfo.Software enum representing the server type
     */
    public static ServerInfo.Software getServerSoftware() {
        return XG7Plugins.getInstance().getServerInfo().getSoftware();
    }

    /**
     * Gets complete server information.
     *
     * @return The ServerInfo instance containing information about the server
     */
    public static ServerInfo getServerInfo() {
        return XG7Plugins.getInstance().getServerInfo();
    }
}