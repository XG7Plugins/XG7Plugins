package com.xg7plugins;

import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.Entity;
import com.xg7plugins.data.lang.LangManager;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.PacketEvent;
import com.xg7plugins.libs.xg7geyserforms.builders.FormCreator;
import com.xg7plugins.libs.xg7menus.builders.BaseMenuBuilder;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.utils.Log;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.cumulus.form.Form;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter
public abstract class Plugin extends JavaPlugin {

    private String prefix;

    private final ConfigManager configsManager;
    private CommandManager commandManager;
    private LangManager langManager;
    private final Log log;

    private final String[] enableDraw;

    @Setter
    private String customPrefix;
    @Setter
    private List<String> enabledWorlds = Collections.emptyList();

    public Plugin(String prefix, String[] configs, String[] enableDraw) {
        this.configsManager = new ConfigManager(this, configs);
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        this.enableDraw = enableDraw;
        this.customPrefix = this.prefix;
        this.log = new Log(this);
        log.loading("Loading " + prefix + "...");
        this.commandManager = new CommandManager(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Arrays.stream(enableDraw).forEach(s -> Bukkit.getConsoleSender().sendMessage(s));

        log.loading("Loading langs...");
        Config config = XG7Plugins.getInstance().getConfigsManager().getConfig("config");
        this.langManager = config.get("enable-langs") ? new LangManager(this, new String[]{"en-us", "pt-br"}) : null;
        if (langManager == null) configsManager.putConfig("messages", new Config(this, "langs/" + config.get("main-lang")));
        if (configsManager.getConfig("config").get("prefix") != null) this.setCustomPrefix(ChatColor.translateAlternateColorCodes('&', configsManager.getConfig("config").get("prefix")));
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
    public BaseMenuBuilder<?,?>[] loadMenus() {
        return null;
    }
    public FormCreator<? extends Form,?>[] loadGeyserForms() {
        return null;
    }
    public ICommand[] loadCommands() {
        return null;
    }
    public Event[] loadEvents() {
        return null;
    }
    public PacketEvent[] loadPacketEvents() {
        return null;
    }
    public void loadTasks() {}
    public Score[] loadScores() {
        return null;
    }
}
