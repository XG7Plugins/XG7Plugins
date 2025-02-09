package com.xg7plugins;

import com.github.retrooper.packetevents.PacketEvents;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.PluginConfigurations;
import com.xg7plugins.cache.CacheManager;
import com.xg7plugins.commands.defaultCommands.LangCommand;
import com.xg7plugins.commands.defaultCommands.reloadCommand.ReloadCommand;
import com.xg7plugins.commands.defaultCommands.taskCommand.TaskCommand;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.processor.DatabaseProcessor;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataDAO;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.events.defaultevents.JoinListener;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.tasks.CooldownManager;
import com.xg7plugins.tasks.TPSCalculator;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.utils.Metrics;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter(AccessLevel.PUBLIC)
@PluginConfigurations(
        prefix = "§bXG§37P§9lu§1gins§r",
        onEnableDraw = {
                "§b __   _______ §3______ §9_____  _             §1_           ",
                "§b \\ \\ / / ____|§3____ §9 |  __ \\| |           §1(_)          ",
                "§b  \\ V / |  __  §3  / §9/| |__) | |_   _  __ _ §1_ _ __  ___ ",
                "§b   > <| | |_ |  §3/ / §9|  ___/| | | | |/ _` | §1| '_ \\/ __|",
                "§b  / . \\ |__| | §3/ / §9 | |    | | |_| | (_| | §1| | | \\__ \\",
                "§b /_/ \\_\\_____|§3/_/  §9 |_|    |_|\\__,_|\\__,§1 |_|_| |_|___/",
                "§9                                     __/ |            ",
                "§9                                    |___/             "
        },
        mainCommandName = "xg7plugins",
        mainCommandAliases = {"7plugins", "7pl", "7pls", "xg7pl"}
)
public final class XG7Plugins extends Plugin {

    @Getter
    private static final int minecraftVersion;
    @Getter
    private static boolean floodgate;
    @Getter
    private static boolean placeholderAPI;
    @Getter
    private static boolean geyserFormEnabled = false;
    @Getter
    private static final boolean paper = Bukkit.getServer().getName().contains("Paper");
    @Getter
    private static final boolean bungeecord = Bukkit.spigot().getConfig().getBoolean("settings.bungeecord", false);

    static {
        Pattern pattern = Pattern.compile("1\\.([0-9]?[0-9])");
        Matcher matcher = pattern.matcher(Bukkit.getServer().getVersion());
        minecraftVersion = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private DatabaseManager databaseManager;
    private CacheManager cacheManager;
    private TPSCalculator tpsCalculator;
    private LangManager langManager;
    private EventManager eventManager;
    private TaskManager taskManager;
    private CooldownManager cooldownManager;
    private PacketEventManager packetEventManager;
    private JsonManager jsonManager;

    private PlayerDataDAO playerDataDAO;

    private final ConcurrentHashMap<String, Plugin> plugins = new ConcurrentHashMap<>();

    public XG7Plugins() {
//        getConfigsManager().registerAdapter(Item.class, new LangItemTypeAdapter());
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .bStats(false)
                .checkForUpdates(true);
        PacketEvents.getAPI().load();
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        debug.loading("Enabling XG7Plugins...");
        PacketEvents.getAPI().init();
        debug.loading("Loading metrics...");
        Metrics.getMetrics(this, 24626);
        debug.loading("Starting tps calculator...");
        this.tpsCalculator = new TPSCalculator();
        tpsCalculator.start();
        floodgate = Bukkit.getPluginManager().getPlugin("floodgate") != null;
        placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        Config config = getConfig("config");

        geyserFormEnabled = floodgate && config.get("enable-geyser-forms", Boolean.class).orElse(false);

        debug.loading("Loading managers...");

        debug.loading("Loading task manager...");
        this.taskManager = new TaskManager(this);
        taskManager().registerExecutor("commands", Executors.newCachedThreadPool());
        taskManager().registerExecutor("database", Executors.newCachedThreadPool());
        taskManager().registerExecutor("files", Executors.newCachedThreadPool());
        taskManager().registerExecutor("menus", Executors.newCachedThreadPool());
        taskManager().registerExecutor("cache", Executors.newSingleThreadExecutor());

        debug.loading("Loading cache manager...");
        this.cacheManager = new CacheManager(this);

        debug.loading("Loading database...");
        this.databaseManager = new DatabaseManager(this);
        this.playerDataDAO = new PlayerDataDAO();

        debug.loading("Loading lang manager...");
        this.langManager = new LangManager(this, new String[]{"en", "pt", "es"});

        debug.loading("Loading JSON manager...");
        this.jsonManager = new JsonManager(this);

        debug.loading("Loading events manager...");
        this.eventManager = new EventManager();

        debug.loading("Loading packet events manager...");
        this.packetEventManager = new PacketEventManager();

        debug.loading("Loading cooldown manager...");
        this.cooldownManager = new CooldownManager(this);

        debug.loading("Loading plugins...");
        register(this);
        plugins.forEach((name, plugin) -> {
            debug.loading("Enabling " + plugin.getName() + "...");

            debug.loading("Connecting plugin to database...");
            databaseManager.connectPlugin(plugin, plugin.loadEntites());

            if (plugin != this) Bukkit.getPluginManager().enablePlugin(plugin);

            debug.loading("Registering listeners...");
            eventManager.registerListeners(plugin, plugin.loadEvents());

            debug.loading("Registering commands...");
            plugin.getCommandManager().registerCommands(plugin.loadCommands());

            debug.loading("Registering packet events...");
            packetEventManager.registerListeners(plugin, plugin.loadPacketEvents());

            debug.loading("Registering tasks...");
            taskManager.registerTasks(plugin.loadRepeatingTasks());

            debug.loading("Loading langs...");
            langManager.loadLangsFrom(plugin);

            debug.loading("Loading help...");
            loadHelp();

        });

        if (placeholderAPI) {
            debug.loading("Registering PlaceholderAPI expansion...");
            new XG7PluginsPlaceholderExpansion().register();
        }

        debug.loading("XG7Plugins enabled.");

    }

    @Override
    public void onDisable() {
        super.onDisable();
        debug.loading("Stopping tpsCalculator...");
        tpsCalculator.cancel();
        this.plugins.forEach((name, plugin) -> unregister(plugin));

        debug.loading("Stopping tasks...");
        taskManager.shutdown();

        debug.loading("Stopping cache...");
        cacheManager.shutdown();

        debug.loading("Stopping PacketEvents...");
        PacketEvents.getAPI().terminate();
    }

    public Class<? extends Entity>[] loadEntites() {
        return new Class[]{PlayerData.class};
    }
    @Override
    public ICommand[] loadCommands() {
        return new ICommand[]{new LangCommand(), new ReloadCommand(), new TaskCommand()};
    }

    @Override
    public Listener[] loadEvents() {
        return new Listener[]{new JoinListener()};
    }

    @Override
    public PacketListener[] loadPacketEvents() {
        return null;
    }

    public Task[] loadRepeatingTasks() {
        return new Task[]{cooldownManager.getTask()};
    }

    @Override
    public void loadHelp() {
//        this.helpCommandGUI = new HelpCommandGUI(this, new XG7PluginsHelpGUI(this));
//        if (floodgate) this.helpCommandForm = new HelpCommandForm(new XG7PluginsHelpForm(this));
//        this.helpInChat = new XG7PluginsChatHelp();
    }

    public static void register(Plugin plugin) {
        XG7Plugins.getInstance().getDebug().loading("Registering " + plugin.getName() + "...");
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        xg7Plugins.getPlugins().put(plugin.getName().split(" ")[0], plugin);
        XG7Plugins.getInstance().getDebug().loading(plugin.getName() + " registered.");
    }

    public static void unregister(Plugin plugin) {
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();
        xg7Plugins.getDebug().loading("Unregistering " + plugin.getName() + "...");

        xg7Plugins.getDebug().loading("Disabling plugin events...");
        xg7Plugins.getEventManager().unregisterListeners(plugin);
        xg7Plugins.getPacketEventManager().unregisterListeners(plugin);

        xg7Plugins.getDebug().loading("Disconnecting plugin from database...");
        xg7Plugins.getDatabaseManager().disconnectPlugin(plugin);

        xg7Plugins.getPlugins().remove(plugin.getName());
        xg7Plugins.getDebug().loading(plugin.getName() + " unregistered.");

    }

    public static TaskManager taskManager() {
        return XG7Plugins.getInstance().getTaskManager();
    }
    public static DatabaseManager database(){
        return XG7Plugins.getInstance().getDatabaseManager();
    }
    public static DatabaseProcessor dbProcessor() {
        return XG7Plugins.getInstance().getDatabaseManager().getProcessor();
    }
    public static Plugin getXG7Plugin(String name) {
        return XG7Plugins.getInstance().getPlugins().get(name);
    }
    public static JsonManager json() {
        return XG7Plugins.getInstance().getJsonManager();
    }

    public static void reload(Plugin plugin) {
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        XG7Plugins.getInstance().getDebug().loading("Reloading " + plugin.getName() + "...");

        if (plugin == xg7Plugins) return;

        unregister(plugin);
        Bukkit.getPluginManager().disablePlugin(plugin);
        Bukkit.getPluginManager().enablePlugin(plugin);
        register(plugin);

        xg7Plugins.getDebug().loading(plugin.getName() + " reloaded.");
    }

    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }

}
