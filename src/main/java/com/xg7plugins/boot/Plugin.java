package com.xg7plugins.boot;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.Entity;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.libs.xg7geyserforms.forms.Form;
import com.xg7plugins.libs.xg7menus.menus.BaseMenu;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.utils.Log;
import lombok.*;
import org.apache.commons.lang.IllegalClassException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
public abstract class Plugin extends JavaPlugin {

    private String prefix;

    private final ConfigManager configsManager;
    private CommandManager commandManager;
    private final Log log;


    @Setter
    private String customPrefix;
    @Setter
    private List<String> enabledWorlds = Collections.emptyList();

    public Plugin() {
        PluginConfigurations configurations = getClass().getAnnotation(PluginConfigurations.class);

        if (configurations == null) throw new IllegalClassException("PluginConfigurations annotation not found in " + getClass().getName());

        this.configsManager = new ConfigManager(this, configurations.configs());
        this.prefix = ChatColor.translateAlternateColorCodes('&', configurations.prefix());
        this.customPrefix = this.prefix;
        this.log = new Log(this);
        log.loading("Loading " + prefix + "...");
        this.commandManager = new CommandManager(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        PluginConfigurations configurations = getClass().getAnnotation(PluginConfigurations.class);

        if (configurations.onEnableDraw().length != 0) Arrays.stream(configurations.onEnableDraw()).forEach(s -> Bukkit.getConsoleSender().sendMessage(s));

        log.loading("Loading langs...");
        Config config = getConfig("config");

        Optional<String> newPerfix = config.get("prefix", String.class);
        if (!newPerfix.isPresent()) this.setCustomPrefix(ChatColor.translateAlternateColorCodes('&', newPerfix.get()));

        log.loading("Custom prefix: " + customPrefix);
    }
    @Override
    public void onDisable() {
        super.onDisable();
    };
    @Override
    public void onLoad() {
        XG7Plugins.register(this);
    };

    public Class<? extends Entity>[] loadEntites() {
        return null;
    }
    public BaseMenu[] loadMenus() {
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
    public Score[] loadScores() {
        return null;
    }

    public Config getConfig(String name) {
        return configsManager.getConfig(name);
    }
}
