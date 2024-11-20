package com.xg7plugins;

import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.data.config.Config;
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
public abstract class Plugin extends JavaPlugin {

    private String prefix;

    private final ConfigManager configsManager;
    private CommandManager commandManager;
    private LangManager langManager;
    private final Log log;

    @Setter
    private String customPrefix;
    @Setter
    private List<String> enabledWorlds = Collections.emptyList();

    public Plugin(String prefix, String[] configs) {
        this.configsManager = new ConfigManager(this, configs);
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        this.customPrefix = this.prefix;
        this.log = new Log(this);
        log.loading("Loading " + prefix + "...");

    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.commandManager = new CommandManager(this);
        Config config = XG7Plugins.getInstance().getConfigsManager().getConfig("config");
        this.langManager = config.get("enable-langs") ? new LangManager(this, new String[]{"en-us", "pt-br"}) : null;
        if (langManager == null) configsManager.putConfig("messages", new Config(this, "langs/" + config.get("main-lang")));
    }
    @Override
    public void onDisable() {
        super.onDisable();
    };
    @Override
    public void onLoad() {
        super.onLoad();
    };
}
