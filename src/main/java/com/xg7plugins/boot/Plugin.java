package com.xg7plugins.boot;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.core_commands.reload.ReloadCause;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.dao.DAO;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.dependencies.Dependency;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.help.HelpMessenger;
import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Debug;
import lombok.*;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.IllegalClassException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
/**
 * Abstract base class for XG7 plugins that extends Bukkit's JavaPlugin.
 * Provides a structured framework for plugin development with support for
 * configurations, commands, events, database, and dependency management.
 *
 * This class manages the plugin lifecycle including loading, enabling,
 * reloading, and disabling.
 *
 * @author DaviXG7
 */
@Getter
public abstract class Plugin extends JavaPlugin {

    private final PluginSetup pluginSetup;

    protected EnvironmentConfig environmentConfig;

    protected ManagerRegistry managerRegistry;
    protected Debug debug;

    protected HelpMessenger helpMessenger;


    public Plugin() {
        pluginSetup = getClass().getAnnotation(PluginSetup.class);
        if (pluginSetup == null) throw new IllegalClassException("PluginSetup annotation not found in " + getClass().getName());

        managerRegistry = new ManagerRegistry(this);
        this.environmentConfig = new EnvironmentConfig();
    }

    @Override
    public void onLoad() {
        environmentConfig.setPrefix(ChatColor.translateAlternateColorCodes('&', pluginSetup.prefix()));
        environmentConfig.setCustomPrefix(environmentConfig.getPrefix());

        managerRegistry.registerManagers(new ConfigManager(this, pluginSetup.configs()));
        managerRegistry.registerManagers(new CommandManager(this));

        environmentConfig.setEnabledWorlds(Config.mainConfigOf(this).getList("enabled-worlds",String.class, true).orElse(Collections.emptyList()));

        debug = new Debug(this);

        debug.loading("Loading " + environmentConfig.getCustomPrefix() + "...");

        for (String cause : pluginSetup.reloadCauses()) ReloadCause.registerCause(this, new ReloadCause(cause));

        XG7Plugins.register(this);
    }

    @Override
    public void onEnable() {
        if (pluginSetup.onEnableDraw().length != 0) Arrays.stream(pluginSetup.onEnableDraw()).forEach(Bukkit.getConsoleSender()::sendMessage);

        Config config = Config.mainConfigOf(this);

        environmentConfig.setCustomPrefix(ChatColor.translateAlternateColorCodes('&', config.get("prefix", String.class).orElse(environmentConfig.getPrefix())));

        environmentConfig.setEnabledWorlds(config.getList("enabled-worlds", String.class).orElse(Collections.emptyList()));

        debug.loading("Custom prefix: " + environmentConfig.getCustomPrefix());

        Bukkit.getScheduler().runTask(this, () -> {
            if (!Config.mainConfigOf(XG7Plugins.getInstance()).get("anti-tab", Boolean.class).orElse(false)) return;

            debug.loading("Loading anti-tab feature...");

            XG7PluginsAPI.packetEventManager().registerListeners(this, XG7PluginsAPI.commandManager(this).getAntiTab());
        });
    }

    /**
     * Handles plugin reloading based on the specific cause provided.
     * This method reloads only the necessary components based on the cause type,
     * allowing for more efficient partial reloads.
     * <p>

     * Behavior:
     * <p>
     * - ReloadCause.CONFIG: Reloads all configurations and reinitializes the debugger <p>
     * - ReloadCause.EVENTS: Reloads all event handlers and packet listeners <p>
     * - ReloadCause.DATABASE: Reestablishes database connections <p>
     * - ReloadCause.LANGS: Clears language cache and reloads language files <p>
     * - ReloadCause.TASKS: Cancels all running tasks and restarts them <p>
     * - ReloadCause.Custom: You can create a cause to handle another type of reload <p>
     * <p>

     * @param cause The reload cause that determines which components will be reloaded
     *              Default causes include: CONFIG, EVENTS, DATABASE, LANGS, TASKS
     * <p>
     */
    public void onReload(ReloadCause cause) {

        XG7Plugins xg7Plugin = XG7Plugins.getInstance();

        if (cause.equals(ReloadCause.CONFIG)) {
            XG7PluginsAPI.configManager(xg7Plugin).reloadConfigs();
            debug = new Debug(this);
        }
        if (cause.equals(ReloadCause.EVENTS)) {
            XG7PluginsAPI.eventManager().reloadEvents(this);
            XG7PluginsAPI.packetEventManager().reloadListeners(this);
        }
        if (cause.equals(ReloadCause.DATABASE)) {
            XG7PluginsAPI.database().reloadConnection(this);
        }
        if (cause.equals(ReloadCause.LANGS)) {
            XG7PluginsAPI.langManager().clearCache();
            XG7PluginsAPI.langManager().loadLangsFrom(this);
        }
        if (cause.equals(ReloadCause.TASKS)) {
            XG7PluginsAPI.taskManager().cancelAllRegisteredTasks(this);
            XG7PluginsAPI.taskManager().reloadTasks(this);
        }

    }

    @Override
    public void onDisable() {
        debug.loading("Disabling " + environmentConfig.getCustomPrefix() + "...");

    }

    public <T extends EnvironmentConfig> T getEnvironmentConfig() {
        return (T) environmentConfig;
    }

    /**
     * Loads the plugin's database entities.
     *
     * @return An array of classes extending Entity, used for database object mapping
     */
    public Class<? extends Entity<?,?>>[] loadEntities() {
        return null;
    }

    public List<DAO<?,?>> loadDAOs() {
        return null;
    }

    /**
     * Loads the plugin's commands that will be automatically registered.
     *
     * @return A list of commands to be registered by the command system
     */
    public List<Command> loadCommands() {
        return null;
    }

    /**
     * Loads the Bukkit event listeners for this plugin.
     *
     * @return A list of listeners to be registered by the event manager
     */
    public List<Listener> loadEvents() {
        return null;
    }

    /**
     * Loads the network packet listeners for this plugin.
     *
     * @return A list of packet listeners to be registered
     */
    public List<PacketListener> loadPacketEvents() {
        return null;
    }

    /**
     * Loads the repeating tasks (schedulers) for the plugin.
     *
     * @return A list of tasks to be executed periodically
     */
    public List<TimerTask> loadRepeatingTasks() {
        return null;
    }

    /**
     * Sets up the plugin's help system.
     * This method must be implemented to register help messages.
     */
    public abstract void loadHelp();

    /**
     * Loads the plugin's optional dependencies.
     *
     * @return A list of dependencies that the plugin can utilize
     */
    public List<Dependency> loadDependencies() {
        return null;
    }

    /**
     * Loads the plugin's required dependencies.
     *
     * @return A list of dependencies that are necessary for the plugin to function
     */
    public List<Dependency> loadRequiredDependencies() {
        return null;
    }

    public PlaceholderExpansion loadPlaceholderExpansion() {
        return null;
    }

}