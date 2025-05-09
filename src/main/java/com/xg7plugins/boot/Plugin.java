package com.xg7plugins.boot;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.core_commands.ReloadCause;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.dependencies.Dependency;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.help.chathelp.HelpInChat;
import com.xg7plugins.help.formhelp.HelpCommandForm;
import com.xg7plugins.help.guihelp.HelpCommandGUI;
import com.xg7plugins.managers.ManagerRegistery;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.utils.Debug;
import lombok.*;
import org.apache.commons.lang.IllegalClassException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
public abstract class Plugin extends JavaPlugin {

    private final PluginConfigurations configurations;

    @Setter
    private String customPrefix;
    private String prefix;
    @Setter
    private List<String> enabledWorlds;

    private ManagerRegistery managers;
    private Debug debug;

    private HelpCommandGUI helpCommandGUI;
    private HelpInChat helpInChat;
    private HelpCommandForm helpCommandForm;


    public Plugin() {
        configurations = getClass().getAnnotation(PluginConfigurations.class);
        if (configurations == null) throw new IllegalClassException("PluginConfigurations annotation not found in " + getClass().getName());
    }

    @Override
    public void onEnable() {
        if (configurations.onEnableDraw().length != 0) Arrays.stream(configurations.onEnableDraw()).forEach(Bukkit.getConsoleSender()::sendMessage);

        Config config = Config.mainConfigOf(this);

        this.setCustomPrefix(ChatColor.translateAlternateColorCodes('&', config.get("prefix", String.class).orElse(prefix)));

        this.enabledWorlds = config.getList("enabled-worlds", String.class).orElse(Collections.emptyList());

        Debug.of(this).loading("Custom prefix: " + customPrefix);
    }

    public void onReload(ReloadCause cause) {

        XG7Plugins xg7Plugin = XG7Plugins.getInstance();

        if (cause.equals(ReloadCause.CONFIG)) {
            configsManager.reloadConfigs();
            debug = new Debug(this);
            return;
        }
        if (cause.equals(ReloadCause.EVENTS)) {
            xg7Plugin.getEventManager().unregisterListeners(this);
            xg7Plugin.getEventManager().registerListeners(this, this.loadEvents());
            xg7Plugin.getPacketEventManager().unregisterListeners(this);
            xg7Plugin.getPacketEventManager().registerListeners(this, this.loadPacketEvents());
            return;
        }
        if (cause.equals(ReloadCause.DATABASE)) {
            xg7Plugin.getDatabaseManager().disconnectPlugin(this);
            xg7Plugin.getDatabaseManager().connectPlugin(this, this.loadEntities());
            return;
        }
        if (cause.equals(ReloadCause.LANGS)) {
            xg7Plugin.getLangManager().getLangs().clear().join();
            xg7Plugin.getLangManager().loadLangsFrom(this);
            return;
        }
        if (cause.equals(ReloadCause.TASKS)) {
            xg7Plugin.getTaskManager().cancelTasks(this);
            xg7Plugin.getTaskManager().getTasks().values().stream().filter(task -> task.getPlugin().getName().equals(this.getName())).forEach(task -> xg7Plugin.getTaskManager().runTask(task));
        }

    };


    @Override
    public void onDisable() {
        Debug.of(this).loading("Disabling " + prefix + "...");
        Debug.of(this).loading("Disabling extensions...");
    }
    @Override
    public void onLoad() {
        this.prefix = ChatColor.translateAlternateColorCodes('&', configurations.prefix());
        this.customPrefix = this.prefix;

        configsManager = new ConfigManager(this, configurations.configs());
        debug = new Debug(this);
        Debug.of(this).loading("Loading " + prefix + "...");
        commandManager = new CommandManager(this);

        for (String cause : configurations.reloadCauses()) ReloadCause.registerCause(this, ReloadCause.of(this, cause));

        XG7Plugins.register(this);
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
