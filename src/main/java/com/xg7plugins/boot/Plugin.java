package com.xg7plugins.boot;

import com.xg7plugins.api.API;
import com.xg7plugins.boot.setup.PluginSetup;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.impl.reload.ReloadCause;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.config.ConfigManager;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.database.dao.Repository;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.dependencies.Dependency;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.extensions.ExtensionManager;
import com.xg7plugins.help.HelpMessenger;
import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Debug;
import lombok.*;
import org.apache.commons.lang.IllegalClassException;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
/**
 * Abstract base class for XG7 plugins that extends Bukkit's JavaPlugin.
 * Provides a structured framework for plugin development with support for
 * configurations, commands, tasks, events, database, and more.
 * <p>
 * This class manages the plugin lifecycle including loading, enabling,
 * reloading, and disabling.
 *
 * @author DaviXG7
 */
@Getter
public abstract class Plugin {

    protected final JavaPlugin javaPlugin;

    private final PluginSetup pluginSetup;

    protected final CommandManager commandManager;
    protected final ConfigManager configManager;
    private final ExtensionManager extensionManager;

    @Setter
    protected HelpMessenger helpMessenger;

    protected Debug debug;


    protected API<?> api;

    @Setter
    private boolean enabled = false;


    public Plugin(JavaPlugin plugin) {
        this.javaPlugin = plugin;
        pluginSetup = getClass().getAnnotation(PluginSetup.class);
        if (pluginSetup == null) throw new IllegalClassException("PluginSetup annotation not found in " + getClass().getName());

        this.commandManager = new CommandManager(this);
        this.configManager = new ConfigManager(this, pluginSetup.configs());
        this.extensionManager = new ExtensionManager(this);

        this.debug = new Debug(this);
    }

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

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

        if (cause.equals(ReloadCause.CONFIG)) {
            XG7Plugins.getAPI().configManager(this).reloadConfigs();
            debug = new Debug(this);
        }

        debug = new Debug(this);

        if (cause.equals(ReloadCause.EVENTS)) {
            XG7Plugins.getAPI().eventManager().reloadEvents(this);
            XG7Plugins.getAPI().packetEventManager().reloadListeners(this);
        }
        if (cause.equals(ReloadCause.DATABASE)) {
            XG7Plugins.getAPI().database().reloadConnection(this);
        }
        if (cause.equals(ReloadCause.LANGS)) {
            XG7Plugins.getAPI().langManager().clearCache();
            XG7Plugins.getAPI().langManager().loadLangsFrom(this);
        }
        if (cause.equals(ReloadCause.TASKS)) {
            XG7Plugins.getAPI().taskManager().cancelAllRegisteredTasks(this);
            XG7Plugins.getAPI().cooldowns().removeAll();
            XG7Plugins.getAPI().cooldowns().cancelTask();
            XG7Plugins.getAPI().taskManager().reloadTasks(this);
        }
        if (cause.equals(ReloadCause.EXTENSIONS)) {
            XG7Plugins.getAPI().extensionManager(this).reloadExtensions();
        }

    }

    /**
     * Loads the plugin's database entities table.
     *
     * @return An array of classes extending Entity, used for database table creation
     */
    public Class<? extends Entity<?,?>>[] loadDBEntities() {
        return null;
    }

    /**
     * Loads the plugin's database entity repositories.
     *
     * @return An array of Repositories
     */
    public List<Repository<?,?>> loadRepositories() {
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
    public abstract HelpMessenger loadHelp();

    /**
     * Loads the plugin's optional dependencies.
     *
     * @return A list of dependencies that the plugin can use
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

    /**
     * Loads the plugin's placeholder expansions, handled by PlaceholderAPI plugin.
     *
     * @return The plugin's PlaceholderAPI expansion
     */
    public Object loadPlaceholderExpansion() {
        return null;
    }

    public List<BasicMenu> loadMenus() {
        return null;
    }
    public List<Form<?,?>> loadForms() {
        return null;
    }
    public List<Score> loadScores() {
        return null;
    }
    public List<Hologram> loadHolograms() {
        return null;
    }



    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', pluginSetup.prefix());
    }
    public String getCustomPrefix() {
        return ConfigFile.mainConfigOf(this).root().get("prefix", getPrefix());
    }
    public List<String> getEnabledWorlds() {
        return ConfigFile.mainConfigOf(this).root().getList("enabled-worlds", String.class).orElse(Collections.emptyList());
    }
    public List<String> getEnabledRegions() {
        return ConfigFile.mainConfigOf(this).root().getList("enabled-regions", String.class).orElse(Collections.emptyList());
    }
    public String getName() {
        return javaPlugin.getDescription().getName();
    }
    public String getVersion() {
        return javaPlugin.getDescription().getVersion();
    }
}