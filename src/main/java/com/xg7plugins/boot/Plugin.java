package com.xg7plugins.boot;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.defaultCommands.reloadCommand.ReloadCause;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.dependencies.Dependency;
import com.xg7plugins.dependencies.DependencyManager;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.help.chathelp.HelpInChat;
import com.xg7plugins.help.formhelp.HelpCommandForm;
import com.xg7plugins.help.guihelp.HelpCommandGUI;
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

        PluginContext context = getContext();

        if (cause.equals(ReloadCause.CONFIG)) {
            context.getConfigsManager().reloadConfigs();
            context.setDebug(new Debug(this));
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
        PluginConfigurations configurations = getClass().getAnnotation(PluginConfigurations.class);

        PluginContext context = getContext();

        this.prefix = ChatColor.translateAlternateColorCodes('&', configurations.prefix());
        this.customPrefix = this.prefix;

        context.setConfigsManager(new ConfigManager(this, configurations.configs()));
        context.setDebug(new Debug(this));
        Debug.of(this).loading("Loading " + prefix + "...");
        context.setCommandManager(new CommandManager(this));

        XG7Plugins.register(this);
    }

    public Class<? extends Entity>[] loadEntities() {
        return null;
    }
    public ICommand[] loadCommands() {
        return null;
    }
    public Listener[] loadEvents() {
        return null;
    }
    public PacketListener[] loadPacketEvents() {
        return null;
    }
    public Task[] loadRepeatingTasks() {
        return null;
    }
    public abstract void loadHelp();
    public Dependency[] loadDependencies() {
        return null;
    }
    public Dependency[] loadRequiredDependencies() {
        return null;
    }

    public abstract <T extends PluginContext> T getContext();

    public boolean isWorldEnabled(String world) {
        return enabledWorlds.contains(world);
    }
    public boolean isWorldEnabled(World world) {
        return enabledWorlds.contains(world.getName());
    }
    public boolean isInWorldEnabled(Player player) {
        return enabledWorlds.contains(player.getWorld().getName());
    }

}
