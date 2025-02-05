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
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataDAO;
import com.xg7plugins.help.formhelp.HelpCommandForm;
import com.xg7plugins.help.guihelp.HelpCommandGUI;
import com.xg7plugins.help.xg7pluginshelp.XG7PluginsHelpForm;
import com.xg7plugins.help.xg7pluginshelp.XG7PluginsHelpGUI;
import com.xg7plugins.help.xg7pluginshelp.chathelp.XG7PluginsChatHelp;
import com.xg7plugins.lang.LangItemTypeAdapter;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.events.defaultevents.CommandAntiTab;
import com.xg7plugins.events.defaultevents.CommandAntiTabOlder;
import com.xg7plugins.events.defaultevents.JoinAndQuit;
import com.xg7plugins.events.packetevents.PacketEventManagerBase;
import com.xg7plugins.libs.xg7geyserforms.forms.Form;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.libs.xg7menus.menuhandler.MenuHandler;
import com.xg7plugins.libs.xg7menus.menuhandler.PlayerMenuHandler;
import com.xg7plugins.libs.xg7geyserforms.FormManager;
import com.xg7plugins.libs.xg7holograms.HologramsManager;
import com.xg7plugins.libs.xg7holograms.event.ClickEventHandler;
import com.xg7plugins.libs.xg7menus.MenuManager;
import com.xg7plugins.libs.xg7menus.menus.BaseMenu;
import com.xg7plugins.libs.xg7npcs.NPCManager;
import com.xg7plugins.libs.xg7scores.ScoreListener;
import com.xg7plugins.libs.xg7scores.ScoreManager;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.events.packetevents.PacketEventManager1_7;
import com.xg7plugins.menus.LangForm;
import com.xg7plugins.menus.LangMenu;
import com.xg7plugins.menus.TaskMenu;
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
    private ScoreManager scoreManager;
    private PacketEventManagerBase packetEventManager;
    private MenuManager menuManager;
    private FormManager formManager;
    private JsonManager jsonManager;
    private HologramsManager hologramsManager;
    private NPCManager npcManager;

    private PlayerDataDAO playerDataDAO;

    private final ConcurrentHashMap<String, Plugin> plugins = new ConcurrentHashMap<>();

    public XG7Plugins() {
        getConfigsManager().registerAdapter(Item.class, new LangItemTypeAdapter());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .bStats(false)
                .checkForUpdates(true);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        PacketEvents.getAPI().init();
        Metrics.getMetrics(this, 24626);
        this.tpsCalculator = new TPSCalculator();
        tpsCalculator.start();
        floodgate = Bukkit.getPluginManager().getPlugin("floodgate") != null;
        placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        getLog().loading("Enabling XG7Plugins...");

        Config config = getConfig("config");

        geyserFormEnabled = floodgate && config.get("enable-geyser-forms", Boolean.class).orElse(false);

        this.taskManager = new TaskManager(this);
        taskManager().registerExecutor("commands", Executors.newCachedThreadPool());
        taskManager().registerExecutor("database", Executors.newCachedThreadPool());
        taskManager().registerExecutor("files", Executors.newCachedThreadPool());
        taskManager().registerExecutor("menus", Executors.newCachedThreadPool());
        taskManager().registerExecutor("cache", Executors.newSingleThreadExecutor());
        this.cacheManager = new CacheManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.playerDataDAO = new PlayerDataDAO();
        this.langManager = config.get("enable-langs", Boolean.class).orElse(false) ? new LangManager(this, new String[]{"en-us", "pt-br", "sp-sp"}) : null;
        if (langManager == null) config.getConfigManager().putConfig("messages", new Config(this, "langs/" + config.get("main-lang", String.class).get()));
        else langManager.loadLangsFrom(this);
        this.jsonManager = new JsonManager(this);
        this.hologramsManager = minecraftVersion < 8 ? null : new HologramsManager(this);
        this.npcManager = new NPCManager(this);
        this.menuManager = new MenuManager();
        this.eventManager = new EventManager();
        this.packetEventManager = minecraftVersion < 8 ? new PacketEventManager1_7() : new PacketEventManager();
        this.cooldownManager = new CooldownManager(this);
        this.scoreManager = new ScoreManager(this);
        this.formManager = floodgate ? new FormManager() : null;

        Bukkit.getOnlinePlayers().forEach(player -> packetEventManager.create(player));

        getLog().loading("Loading plugins...");
        register(this);
        plugins.forEach((name, plugin) -> {
            getLog().info("Enabling " + plugin.getName() + "...");
            databaseManager.connectPlugin(plugin, plugin.loadEntites());
            if (plugin != this) Bukkit.getPluginManager().enablePlugin(plugin);
            eventManager.registerPlugin(plugin, plugin.loadEvents());
            plugin.getCommandManager().registerCommands(plugin.loadCommands());
            packetEventManager.registerPlugin(plugin, plugin.loadPacketEvents());
            taskManager.registerTasks(plugin.loadRepeatingTasks());
            scoreManager.registerScores(plugin.loadScores());
            menuManager.registerMenus(plugin.loadMenus());
            loadHelp();
            if (formManager != null) {
                Form<?,?>[] forms = plugin.loadGeyserForms();
                if (forms != null) Arrays.stream(forms).filter(Form::isEnabled).forEach(form -> formManager.registerForm(form));
            }
            if (langManager == null) plugin.getConfigsManager().putConfig("messages", new Config(this, "langs/" + plugin.getConfig("config").get("main-lang", String.class).get()));
            else langManager.loadLangsFrom(plugin);
        });

        if (placeholderAPI) new XG7PluginsPlaceholderExpansion().register();

        getLog().loading("XG7Plugins enabled.");

    }



    @Override
    public void onDisable() {
        tpsCalculator.cancel();
        scoreManager.cancelTask();
        scoreManager.removePlayers();
        this.plugins.forEach((name, plugin) -> unregister(plugin));
        Bukkit.getOnlinePlayers().forEach(player -> {
            packetEventManager.stopEvent(player);
            if (minecraftVersion > 7) hologramsManager.removePlayer(player);
            npcManager.removePlayer(player);
        });
        if (minecraftVersion > 7) hologramsManager.cancelTask();
        npcManager.cancelTask();
        taskManager.shutdown();
        cacheManager.shutdown();
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
    public BaseMenu[] loadMenus() {
        return new BaseMenu[]{new LangMenu(this), new TaskMenu(this)};
    }

    @Override
    public Listener[] loadEvents() {
        return new Listener[]{new JoinAndQuit(), new ClickEventHandler(), new com.xg7plugins.libs.xg7npcs.event.ClickEventHandler(), minecraftVersion > 12 ? new CommandAntiTab() : null, new ScoreListener(), new MenuHandler(), new PlayerMenuHandler(menuManager)};
    }

    @Override
    public PacketListener[] loadPacketEvents() {
        return minecraftVersion < 13 ? new PacketListener[]{new CommandAntiTabOlder()} : super.loadPacketEvents();
    }

    public Task[] loadRepeatingTasks() {
        return new Task[]{hologramsManager == null ? null : hologramsManager.getTask(), npcManager.getTask(), scoreManager.getTask(), cooldownManager.getTask()};
    }

    @Override
    public void loadHelp() {
        this.helpCommandGUI = new HelpCommandGUI(this, new XG7PluginsHelpGUI(this));
        if (floodgate) this.helpCommandForm = new HelpCommandForm(new XG7PluginsHelpForm(this));
        this.helpInChat = new XG7PluginsChatHelp();
    }

    @Override
    public Form<?, ?>[] loadGeyserForms() {
        return new Form[]{new LangForm()};
    }

    public static void register(Plugin plugin) {
        XG7Plugins.getInstance().getLog().loading("Registering " + plugin.getName() + "...");
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        xg7Plugins.getPlugins().put(plugin.getName().split(" ")[0], plugin);
    }

    public static void unregister(Plugin plugin) {
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();
        XG7Plugins.getInstance().getLog().loading("Unregistering " + plugin.getName() + "...");
        xg7Plugins.getPacketEventManager().unregisterPlugin(plugin);
        xg7Plugins.getDatabaseManager().disconnectPlugin(plugin);
        xg7Plugins.getScoreManager().unregisterPlugin(plugin);

        xg7Plugins.getPlugins().remove(plugin.getName());

    }

    public static TaskManager taskManager() {
        return XG7Plugins.getInstance().getTaskManager();
    }

    public static void reload(Plugin plugin) {
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        if (plugin == xg7Plugins) return;


        unregister(plugin);
        Bukkit.getPluginManager().disablePlugin(plugin);
        Bukkit.getPluginManager().enablePlugin(plugin);
        register(plugin);
    }

    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }

}
