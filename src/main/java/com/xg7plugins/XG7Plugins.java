package com.xg7plugins;

import com.xg7plugins.commands.defaultCommands.LangCommand;
import com.xg7plugins.commands.defaultCommands.ReloadCommand;
import com.xg7plugins.commands.defaultCommands.TaskCommands;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.database.Entity;
import com.xg7plugins.data.database.EntityProcessor;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.data.lang.LangManager;
import com.xg7plugins.data.lang.PlayerLanguage;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.PacketEvent;
import com.xg7plugins.events.defaultevents.JoinAndQuit;
import com.xg7plugins.events.packetevents.PacketManagerBase;
import com.xg7plugins.libs.xg7geyserforms.FormManager;
import com.xg7plugins.libs.xg7holograms.HologramsManager;
import com.xg7plugins.libs.xg7menus.MenuManager;
import com.xg7plugins.libs.xg7npcs.NPCManager;
import com.xg7plugins.libs.xg7scores.ScoreManager;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.DBManager;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.events.packetevents.PacketEventManager1_7;
import com.xg7plugins.tasks.TaskManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter(AccessLevel.PUBLIC)
public final class XG7Plugins extends Plugin {

    @Getter
    private static final int minecraftVersion;
    @Getter
    private static boolean floodgate;
    @Getter
    private static boolean placeholderAPI;

    static {
        Pattern pattern = Pattern.compile("1\\.([0-9]?[0-9])");
        Matcher matcher = pattern.matcher(Bukkit.getServer().getVersion());
        minecraftVersion = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private DBManager databaseManager;
    private EventManager eventManager;
    private TaskManager taskManager;
    private ScoreManager scoreManager;
    private PacketManagerBase packetEventManager;
    private MenuManager menuManager;
    private FormManager formManager;
    private JsonManager jsonManager;
    private HologramsManager hologramsManager;
    private NPCManager npcManager;

    private final HashMap<String, Plugin> plugins = new HashMap<>();

    public XG7Plugins() {
        super("[XG7Plugins]", /* null will be default configs */ null);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        floodgate = Bukkit.getPluginManager().getPlugin("floodgate") != null;
        placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        Config config = getConfigsManager().getConfig("config");

        if (config.get("prefix") != null) this.setCustomPrefix(ChatColor.translateAlternateColorCodes('&', config.get("prefix")));

        getLog().loading("Enabling XG7Plugins...");

        this.getCommandManager().registerCommands(LangCommand.class, ReloadCommand.class, TaskCommands.class);
        this.databaseManager = new DBManager(this);
        this.jsonManager = new JsonManager();
        this.databaseManager.connectPlugin(this, PlayerLanguage.class);
        this.hologramsManager = minecraftVersion < 8 ? null : new HologramsManager(this);
        this.npcManager = new NPCManager(this);
        this.menuManager = new MenuManager(this);
        this.eventManager = new EventManager();
        this.packetEventManager = minecraftVersion < 8 ? new PacketEventManager1_7() : new PacketEventManager();
        Bukkit.getOnlinePlayers().forEach(player -> {
            packetEventManager.create(player);
            if (minecraftVersion > 7) hologramsManager.addPlayer(player);
            npcManager.addPlayer(player);
        });
        this.taskManager = new TaskManager(this);
        this.scoreManager = new ScoreManager(this);
        this.eventManager.registerPlugin(this, JoinAndQuit.class);
        this.formManager = floodgate ? new FormManager() : null;
        EntityProcessor.createTableOf(this, PlayerLanguage.class);
    }


    @Override
    public void onDisable() {
        this.plugins.forEach((name, plugin) -> unregister(plugin));
        Bukkit.getOnlinePlayers().forEach(player -> {
            packetEventManager.stopEvent(player);
            if (minecraftVersion > 7) hologramsManager.removePlayer(player);
            npcManager.removePlayer(player);
        });
        scoreManager.removePlayers();
        if (minecraftVersion > 7) hologramsManager.cancelTask();
        npcManager.cancelTask();
        taskManager.getExecutor().shutdown();
    }

    public static void register(Plugin plugin) {
        XG7Plugins.getInstance().getLog().loading("Registering " + plugin.getName() + "...");
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        xg7Plugins.getPlugins().put(plugin.getName().split(" ")[0], plugin);
    }

    public static void unregister(Plugin plugin) {
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        xg7Plugins.getPacketEventManager().unregisterPlugin(plugin);
        xg7Plugins.getDatabaseManager().disconnectPlugin(plugin);
        xg7Plugins.getScoreManager().unregisterPlugin(plugin);

        xg7Plugins.getPlugins().remove(plugin.getName());

    }

    public void registerEvents(Plugin plugin, Class<? extends Event> eventClass) {
        eventManager.registerPlugin(plugin, eventClass);
    }
    @SafeVarargs
    public final void connectPlugin(Plugin plugin, Class<? extends Entity>... entityClasses) {
        databaseManager.connectPlugin(plugin, entityClasses);
    }
    @SafeVarargs
    public final void registerPacketEvents(Plugin plugin, Class<? extends PacketEvent>... eventClasses) {
        packetEventManager.registerPlugin(plugin, eventClasses);
    }
    @SafeVarargs
    public final void registerCommands(Plugin plugin, Class<? extends ICommand>... commandClasses) {
        plugin.getCommandManager().registerCommands(commandClasses);
    }


    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }

}
