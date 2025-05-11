package com.xg7plugins.boot;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.core_commands.ReloadCause;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.dependencies.Dependency;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.help.chathelp.HelpInChat;
import com.xg7plugins.help.formhelp.HelpCommandForm;
import com.xg7plugins.help.guihelp.HelpCommandGUI;
import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.utils.Debug;
import lombok.*;
import org.apache.commons.lang.IllegalClassException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
public abstract class Plugin extends JavaPlugin {

    private final PluginSetup configurations;

    private final EnvironmentConfig environmentConfig;

    protected ManagerRegistry managerRegistry;
    protected Debug debug;

    private HelpCommandGUI helpCommandGUI;
    private HelpInChat helpInChat;
    private HelpCommandForm helpCommandForm;


    public Plugin() {
        configurations = getClass().getAnnotation(PluginSetup.class);
        if (configurations == null) throw new IllegalClassException("PluginConfigurations annotation not found in " + getClass().getName());

        managerRegistry = new ManagerRegistry(this);
        this.environmentConfig = new EnvironmentConfig();
    }

    @Override
    public void onLoad() {
        environmentConfig.setPrefix(ChatColor.translateAlternateColorCodes('&', configurations.prefix()));
        environmentConfig.setCustomPrefix(environmentConfig.getPrefix());

        managerRegistry.registerManagers(new ConfigManager(this, configurations.configs()));

        debug = new Debug(this);

        debug.loading("Loading " + environmentConfig.getCustomPrefix() + "...");

        managerRegistry.registerManagers(new CommandManager(this));

        for (String cause : configurations.reloadCauses()) ReloadCause.registerCause(this, ReloadCause.of(this, cause));

        XG7Plugins.register(this);
    }

    @Override
    public void onEnable() {
        if (configurations.onEnableDraw().length != 0) Arrays.stream(configurations.onEnableDraw()).forEach(Bukkit.getConsoleSender()::sendMessage);

        Config config = Config.mainConfigOf(this);

        environmentConfig.setCustomPrefix(ChatColor.translateAlternateColorCodes('&', config.get("prefix", String.class).orElse(environmentConfig.getPrefix())));

        environmentConfig.setEnabledWorlds(config.getList("enabled-worlds", String.class).orElse(Collections.emptyList()));

        debug.loading("Custom prefix: " + environmentConfig.getCustomPrefix());
    }

    public void onReload(ReloadCause cause) {

        XG7Plugins xg7Plugin = XG7Plugins.getInstance();

        if (cause.equals(ReloadCause.CONFIG)) {
            XG7PluginsAPI.configManager(xg7Plugin).reloadConfigs();
            debug = new Debug(this);
            return;
        }
        if (cause.equals(ReloadCause.EVENTS)) {
            XG7PluginsAPI.eventManager().reloadEvents(this);
            XG7PluginsAPI.packetEventManager().reloadListeners(this);
            return;
        }
        if (cause.equals(ReloadCause.DATABASE)) {
            XG7PluginsAPI.database().reloadConnection(this);
            return;
        }
        if (cause.equals(ReloadCause.LANGS)) {
            XG7PluginsAPI.langManager().clearCache();
            XG7PluginsAPI.langManager().loadLangsFrom(this);
            return;
        }
        if (cause.equals(ReloadCause.TASKS)) {
            XG7PluginsAPI.taskManager().cancelTasks(this);
            XG7PluginsAPI.taskManager().reloadTasks(this);
        }

    };


    @Override
    public void onDisable() {
        debug.loading("Disabling " + environmentConfig.getCustomPrefix() + "...");

    }

    public <T extends EnvironmentConfig> T getEnvironmentConfig() {
        return (T) environmentConfig;
    }

    public Class<? extends Entity<?,?>>[] loadEntities() {
        return null;
    }
    public List<Command> loadCommands() {
        return null;
    }
    public List<Listener> loadEvents() {
        return null;
    }
    public List<PacketListener> loadPacketEvents() {
        return null;
    }
    public List<Task> loadRepeatingTasks() {
        return null;
    }
    public abstract void loadHelp();
    public List<Dependency> loadDependencies() {
        return null;
    }
    public List<Dependency> loadRequiredDependencies() {
        return null;
    }
    public List<ReloadCause> loadReloadCauses() {
        return null;
    }
}
