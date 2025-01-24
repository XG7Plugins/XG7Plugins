package com.xg7plugins.boot;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.help.formhelp.HelpCommandForm;
import com.xg7plugins.help.chathelp.HelpInChat;
import com.xg7plugins.help.guihelp.HelpCommandGUI;
import com.xg7plugins.libs.xg7geyserforms.forms.Form;
import com.xg7plugins.libs.xg7menus.menus.BaseMenu;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.utils.Log;
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

    private final String prefix;

    private final ConfigManager configsManager;
    private final CommandManager commandManager;
    private final Log log;

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

        this.setCustomPrefix(ChatColor.translateAlternateColorCodes('&', config.get("prefix", String.class).orElse(prefix)));

        this.enabledWorlds = config.getList("enabled-worlds", String.class).orElse(Collections.emptyList());


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
