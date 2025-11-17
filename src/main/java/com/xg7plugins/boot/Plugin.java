package com.xg7plugins.boot;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.setup.PluginSetup;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.impl.reload.ReloadCause;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.config.ConfigManager;
import com.xg7plugins.data.database.dao.Repository;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.dependencies.Dependency;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.help.HelpMessenger;
import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Debug;
import lombok.*;
import org.apache.commons.lang.IllegalClassException;
import org.bukkit.Bukkit;
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

        environmentConfig.setEnabledWorlds(ConfigFile.mainConfigOf(this).root().getList("enabled-worlds", String.class).orElse(Collections.emptyList()));

        debug = new Debug(this);

        debug.loading("Loading " + environmentConfig.getCustomPrefix() + "...");

        for (String cause : pluginSetup.reloadCauses()) ReloadCause.registerCause(this, new ReloadCause(cause));

        XG7Plugins.register(this);
    }

    @Override
    public void onEnable() {
        debug.loading("Enabling " + environmentConfig.getCustomPrefix() + "...");
        if (pluginSetup.onEnableDraw().length != 0) {
            Arrays.stream(pluginSetup.onEnableDraw()).forEach(Bukkit.getConsoleSender()::sendMessage);
            Bukkit.getConsoleSender().sendMessage("Plugin version: " + this.getDescription().getVersion());
            Bukkit.getConsoleSender().sendMessage("Found bug? Report us: https://discord.gg/yghhDAaCED");
            Bukkit.getConsoleSender().sendMessage("Consider donating <3: https://ko-fi.com/davixg7");
        }

        ConfigSection config = ConfigFile.mainConfigOf(this).root();

        environmentConfig.setCustomPrefix(ChatColor.translateAlternateColorCodes('&', config.get("prefix", environmentConfig.getPrefix())));

        environmentConfig.setEnabledWorlds(config.getList("enabled-worlds", String.class).orElse(Collections.emptyList()));

        debug.loading("Custom prefix: " + environmentConfig.getCustomPrefix());

        Bukkit.getScheduler().runTask(this, () -> {
            if (!ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("anti-tab", false)) return;

            debug.info("Loading anti-tab feature...");

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

        if (cause.equals(ReloadCause.CONFIG)) {
            XG7PluginsAPI.configManager(this).reloadConfigs();
            debug = new Debug(this);
        }

        debug = new Debug(this);

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
            XG7PluginsAPI.cooldowns().removeAll();
            XG7PluginsAPI.cooldowns().cancelTask();
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
     * Loads the plugin's database entities table.
     *
     * @return An array of classes extending Entity, used for database table creation
     */
    public Class<? extends Entity<?,?>>[] loadEntities() {
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
    public abstract void loadHelp();

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
    public List<Hologram<?>> loadHolograms() {
        return null;
    }

}