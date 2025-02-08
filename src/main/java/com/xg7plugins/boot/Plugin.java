package com.xg7plugins.boot;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.extensions.ExtensionManager;
import com.xg7plugins.help.formhelp.HelpCommandForm;
import com.xg7plugins.help.chathelp.HelpInChat;
import com.xg7plugins.help.guihelp.HelpCommandGUI;
import com.xg7plugins.temp.xg7geyserforms.forms.Form;
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

    private String prefix;

    private ConfigManager configsManager;
    private CommandManager commandManager;
    protected Debug debug;

    private ExtensionManager extensionManager;

    protected HelpCommandGUI helpCommandGUI;
    protected HelpInChat helpInChat;
    protected HelpCommandForm helpCommandForm;

    @Setter
    private String customPrefix;
    @Setter
    private List<String> enabledWorlds;

    public Plugin() {

        PluginConfigurations configurations = getClass().getAnnotation(PluginConfigurations.class);

        if (configurations == null) throw new IllegalClassException("PluginConfigurations annotation not found in " + getClass().getName());
    }

    @Override
    public void onEnable() {
        super.onEnable();

        PluginConfigurations configurations = getClass().getAnnotation(PluginConfigurations.class);

        if (configurations.onEnableDraw().length != 0) Arrays.stream(configurations.onEnableDraw()).forEach(s -> Bukkit.getConsoleSender().sendMessage(s));

        debug.loading("Loading langs...");
        Config config = getConfig("config");

        this.setCustomPrefix(ChatColor.translateAlternateColorCodes('&', config.get("prefix", String.class).orElse(prefix)));

        this.enabledWorlds = config.getList("enabled-worlds", String.class).orElse(Collections.emptyList());


        debug.loading("Custom prefix: " + customPrefix);

        Bukkit.getScheduler().runTask(this, () -> {
            this.extensionManager = new ExtensionManager(this);

            extensionManager.initExtensions();
            extensionManager.loadTasks();
            extensionManager.loadExecutors();
            extensionManager.loadCommands();
            extensionManager.loadListeners();

            debug.loading("Loaded " + extensionManager.getExtensions().size() + " extensions");

        });
    }
    @Override
    public void onDisable() {
        extensionManager.disableExtensions();
    };
    @Override
    public void onLoad() {
        PluginConfigurations configurations = getClass().getAnnotation(PluginConfigurations.class);
        this.configsManager = new ConfigManager(this, configurations.configs());
        this.prefix = ChatColor.translateAlternateColorCodes('&', configurations.prefix());
        this.customPrefix = this.prefix;
        this.debug = new Debug(this);
        debug.loading("Loading " + prefix + "...");
        this.commandManager = new CommandManager(this);
        XG7Plugins.register(this);
    };
    public Class<? extends Entity>[] loadEntites() {
        return null;
    }
    public Form<?,?>[] loadGeyserForms() {
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

    public Config getConfig(String name) {
        return configsManager.getConfig(name);
    }

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
