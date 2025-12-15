package com.xg7plugins.api;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.CacheManager;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.ConfigManager;
import com.xg7plugins.data.database.dao.Repository;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.processor.DatabaseProcessor;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.dependencies.DependencyManager;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.extensions.ExtensionManager;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.loader.VersionChecker;
import com.xg7plugins.modules.ModuleManager;
import com.xg7plugins.modules.xg7dialogs.XG7Dialogs;
import com.xg7plugins.modules.xg7geyserforms.XG7GeyserForms;
import com.xg7plugins.modules.xg7holograms.XG7Holograms;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7npcs.XG7NPCs;
import com.xg7plugins.modules.xg7scores.XG7Scores;
import com.xg7plugins.server.ServerInfo;
import com.xg7plugins.cooldowns.CooldownManager;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.tasks.TimerTask;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
@AllArgsConstructor
public class XG7PluginsAPI implements API<XG7Plugins> {

    private XG7Plugins plugin;

    /**
     * Gets an instance of a specific XG7 plugin by its class type.
     *
     * @param <T>         The type of plugin to return
     * @param pluginClass The class of the desired plugin
     * @return The requested plugin instance, or null if not found
     */
    public <T extends Plugin> T getXG7Plugin(Class<T> pluginClass) {
        return (T) plugin.getPlugins().values().stream().filter(plugin -> pluginClass == plugin.getClass()).findFirst().orElse(null);
    }

    /**
     * Gets an instance of an XG7 plugin by its name.
     *
     * @param <T>  The type of plugin to return
     * @param name The name of the plugin
     * @return The requested plugin instance, or null if not found
     */
    public <T extends Plugin> T getXG7Plugin(String name) {
        return (T) plugin.getPlugins().values().stream().filter(plugin -> name.equalsIgnoreCase(plugin.getName())).findFirst().orElse(null);
    }

    public <T extends Plugin> T getXG7Plugin(JavaPlugin plugin) {
        return (T) this.plugin.getPlugins().get(plugin);
    }

    /**
     * Gets a set containing all registered XG7 plugin instances.
     *
     * @return A set of all XG7 plugin instances
     */
    public Set<Plugin> getAllXG7Plugins() {
        return new HashSet<>(this.plugin.getPlugins().values());
    }

    /**
     * Gets a set containing the names of all registered XG7 plugins.
     *
     * @return A set with all XG7 plugin names
     */
    public Set<String> getAllXG7PluginsNames() {
        return plugin.getPlugins().values().stream().map(Plugin::getName).collect(Collectors.toSet());
    }

    /**
     * Gets the task manager that controls scheduled tasks.
     *
     * @return The global TaskManager instance
     */
    public TaskManager taskManager() {
        return plugin.getTaskManager();
    }

    public TimerTask getTimerTask(String id) {
        return taskManager().getTimerTask(id);
    }
    public TimerTask getTimerTask(Plugin plugin, String id) {
        return taskManager().getTimerTask(plugin, id);
    }

    /**
     * Gets the database manager that handles connections and persistence operations.
     *
     * @return The global DatabaseManager instance
     */
    public DatabaseManager database() {
        return plugin.getDatabaseManager();
    }

    /**
     * Gets the cache manager that handles in-memory data.
     *
     * @return The global CacheManager instance
     */
    public CacheManager cacheManager() {
        return plugin.getCacheManager();
    }

    /**
     * Gets the system's Bukkit event manager.
     *
     * @return The global EventManager instance
     */
    public EventManager eventManager() {
        return plugin.getEventManager();
    }

    /**
     * Gets the network packet events manager.
     *
     * @return The global PacketEventManager instance
     */
    public PacketEventManager packetEventManager() {
        return plugin.getPacketEventManager();
    }

    /**
     * Gets the cooldown manager for controlling time between actions.
     *
     * @return The global CooldownManager instance
     */
    public CooldownManager cooldowns() {
        return plugin.getCooldownManager();
    }

    /**
     * Gets the system's language manager.
     *
     * @return The global LangManager instance
     */
    public LangManager langManager() {
        return plugin.getLangManager();
    }

    /**
     * Gets the system's module manager.
     *
     * @return The global ModuleManager instance
     */
    public ModuleManager moduleManager() {
        return plugin.getModuleManager();
    }

    public XG7Menus menus() {
        return moduleManager().getModule(XG7Menus.class);
    }

    public XG7Scores scores() {
        return moduleManager().getModule(XG7Scores.class);
    }

    public XG7GeyserForms geyserForms() {
        return moduleManager().getModule(XG7GeyserForms.class);
    }

    public XG7Holograms holograms() {
        return moduleManager().getModule(XG7Holograms.class);
    }

    public XG7NPCs npcs() {
        return moduleManager().getModule(XG7NPCs.class);
    }

    public XG7Dialogs dialogs() {
        return moduleManager().getModule(XG7Dialogs.class);
    }

    /**
     * Gets the configuration manager for a specific plugin.
     *
     * @param plugin The plugin to get the configuration manager for
     * @return The ConfigManager associated with the specified plugin
     */
    public ConfigManager configManager(Plugin plugin) {
        return plugin.getConfigManager();
    }

    /**
     * Gets the command manager for a specific plugin.
     *
     * @param plugin The plugin to get the command manager for
     * @return The CommandManager associated with the specified plugin
     */
    public CommandManager commandManager(Plugin plugin) {
        return plugin.getCommandManager();
    }

    public ExtensionManager extensionManager(Plugin plugin) {
        return plugin.getExtensionManager();
    }

    /**
     * Gets a list of all registered commands for a specific plugin.
     *
     * @param plugin The plugin to get commands for
     * @return A list containing all registered Command instances
     */
    public List<Command> commandListOf(Plugin plugin) {
        return commandManager(plugin).getCommandList();
    }

    /**
     * Gets a map of all registered commands and their aliases for a specific plugin.
     * The map keys are command names/aliases, and values are the Command instances.
     *
     * @param plugin The plugin to get commands for
     * @return A map containing command names/aliases mapped to Command instances
     */
    public Map<String, CommandNode> commandNodesOf(Plugin plugin) {
        return commandManager(plugin).getCommandNodeMap();
    }

    public Set<CommandNode> rootCommandNodesOf(Plugin plugin) {
        return new HashSet<>(XG7Plugins.getAPI().commandNodesOf(plugin).values());
    }

    /**
     * Gets the system's JSON manager.
     *
     * @return The global JsonManager instance
     */
    public JsonManager jsonManager() {
        return plugin.getJsonManager();
    }

    /**
     * Gets the dependency manager that controls external plugins.
     *
     * @return The global DependencyManager instance
     */
    public DependencyManager dependencyManager() {
        return plugin.getDependencyManager();
    }

    /**
     * Gets the database processor for low-level database operations.
     *
     * @return The DatabaseProcessor instance
     */
    public DatabaseProcessor dbProcessor() {
        return database().getProcessor();
    }

    /**
     * Checks if a specific dependency is loaded and enabled.
     *
     * @param name The name of the dependency to check
     * @return true if the dependency is enabled, false otherwise
     */
    public boolean isDependencyEnabled(String name) {
        return dependencyManager().isLoaded(name);
    }

    /**
     * Checks if Geyser forms support is enabled.
     * Requires the Floodgate plugin to be loaded and the option enabled in config.
     *
     * @return true if Geyser forms support is enabled, false otherwise
     */
    public boolean isGeyserFormsEnabled() {
        System.out.println("Floodgate loaded: " + dependencyManager().exists("floodgate"));
        System.out.println("XG7GeyserForms enabled: " + moduleManager().isModuleEnabled("XG7GeyserForms"));
        return dependencyManager().exists("floodgate") && moduleManager().isModuleEnabled("XG7GeyserForms");
    }

    /**
     * Checks if a specific world is enabled for a plugin.
     *
     * @param plugin The plugin to check
     * @param world  The name of the world to check
     * @return true if the world is enabled for the plugin, false otherwise
     */
    public boolean isEnabledWorld(Plugin plugin, String world) {
        return plugin.getEnabledWorlds().contains(world);
    }

    /**
     * Checks if a specific world is enabled for a plugin.
     *
     * @param plugin The plugin to check
     * @param world  The World object to check
     * @return true if the world is enabled for the plugin, false otherwise
     */
    public boolean isEnabledWorld(Plugin plugin, World world) {
        return isEnabledWorld(plugin, world.getName());
    }

    /**
     * Checks if the world the player is currently in is enabled for a plugin.
     *
     * @param plugin The plugin to check
     * @param player The player whose world will be checked
     * @return true if the player's world is enabled for the plugin, false otherwise
     */
    public boolean isInAnEnabledWorld(Plugin plugin, Player player) {
        return isEnabledWorld(plugin, player.getWorld());
    }

    /**
     * Gets a list of enabled world names for a specific plugin.
     *
     * @param plugin The plugin to get enabled worlds for
     * @return A list of enabled world names
     */
    public List<String> getEnabledWorldsOf(Plugin plugin) {
        return plugin.getEnabledWorlds();
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
    public <ID,T extends Entity<?, ?>, U extends Repository<ID,T>> U getRepository(Class<U> clazz) {
        return database().getDaoManager().getRepository(clazz);
    }

    /**
     * Gets a list of all registered repositories.
     *
     * @return A list containing all repository instances
     */
    public List<Repository> getRepositories() {
        return database().getDaoManager().getAllRepositories();
    }

    /**
     * Gets a list of all repositories registered for a specific plugin.
     *
     * @param plugin The plugin to get repositories for
     * @return A list of repositories associated with the plugin
     */
    public List<Repository> getRepositoriesByPlugin(Plugin plugin) {
        return database().getDaoManager().getAllRepositoriesByPlugin(plugin);
    }

    /**
     * Requests player data by UUID asynchronously.
     *
     * @param uuid The player's UUID
     * @return A CompletableFuture that will contain the player data when available
     */
    public CompletableFuture<PlayerData> requestPlayerData(UUID uuid) {
        return getRepository(PlayerDataRepository.class).getAsync(uuid);
    }

    /**
     * Requests player data from the player instance asynchronously.
     *
     * @param player The player to get data for
     * @return A CompletableFuture that will contain the player data when available
     */
    public CompletableFuture<PlayerData> requestPlayerData(Player player) {
        return requestPlayerData(player.getUniqueId());
    }

    /**
     * Gets player data by UUID synchronously.
     *
     * @param uuid The player's UUID
     * @return The player data associated with the UUID
     */
    public PlayerData getPlayerData(UUID uuid) {
        return getRepository(PlayerDataRepository.class).get(uuid);
    }

    /**
     * Gets player data from the player instance synchronously.
     *
     * @param player The player to get data for
     * @return The player data associated with the player
     */
    public PlayerData getPlayerData(Player player) {
        return getRepository(PlayerDataRepository.class).get(player.getUniqueId());
    }

    /**
     * Gets a set with the names of all online players.
     *
     * @return A set containing the names of all online players
     */
    public Set<String> getAllPlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet());
    }

    /**
     * Gets a set with the UUIDs of all online players.
     *
     * @return A set containing the UUIDs of all online players
     */
    public Set<UUID> getAllPlayerUUIDs() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
    }

    /**
     * Gets a set with instances of all online players.
     *
     * @return A set containing instances of all online players
     */
    public Set<Player> getAllPlayers() {
        return new HashSet<>(Bukkit.getOnlinePlayers());
    }

    /**
     * Gets the server software type (Bukkit, Spigot, Paper, etc).
     *
     * @return The ServerInfo.Software enum representing the server type
     */
    public ServerInfo.Software getServerSoftware() {
        return XG7Plugins.getInstance().getServerInfo().getSoftware();
    }

    /**
     * Gets complete server information.
     *
     * @return The ServerInfo instance containing information about the server
     */
    public ServerInfo getServerInfo() {
        return XG7Plugins.getInstance().getServerInfo();
    }

    public VersionChecker getVersionChecker() {
        return plugin.getVersionChecker();
    }

    @Override
    public XG7Plugins getPlugin() {
        return plugin;
    }
}