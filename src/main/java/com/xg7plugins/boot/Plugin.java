package com.xg7plugins.boot;

import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.lang.LangManager;
import com.xg7plugins.utils.Log;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public abstract class Plugin extends JavaPlugin {

    private String prefix;

    private final ConfigManager configsManager;
    private final CommandManager commandManager;
    private final LangManager langManager;
    private final Log log;

    private String customPrefix;
    private List<String> enabledWorlds = Collections.emptyList();

    public Plugin(String prefix, String[] defLangs, String[] configs) {

        if (Bukkit.getPluginManager().getPlugin("XG7Plugins") == null) {
            //Baixar
        }

        this.configsManager = new ConfigManager(this, configs);
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        this.customPrefix = this.prefix;
        this.log = new Log(this);

        log.loading("Loading " + prefix + "...");
        this.langManager = new LangManager(this, defLangs);
        this.commandManager = new CommandManager(this);
    }

    @Override
    public abstract void onEnable();
    @Override
    public abstract void onDisable();
    @Override
    public abstract void onLoad();
}
