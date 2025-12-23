package com.xg7plugins;

import com.github.retrooper.packetevents.PacketEvents;
import com.xg7plugins.commands.impl.*;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.loader.XG7PluginsLoader;
import com.xg7plugins.api.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.setup.Collaborator;
import com.xg7plugins.boot.setup.PluginSetup;
import com.xg7plugins.cache.CacheManager;
import com.xg7plugins.commands.impl.reload.ReloadCause;
import com.xg7plugins.commands.impl.reload.ReloadCommand;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.typeadapter.impl.LangItemTypeAdapter;
import com.xg7plugins.config.typeadapter.impl.SoundTypeAdapter;
import com.xg7plugins.cooldowns.CooldownManager;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.data.database.ConnectionType;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.data.database.connector.Connector;
import com.xg7plugins.data.database.dao.Repository;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.dependencies.Dependency;
import com.xg7plugins.dependencies.DependencyManager;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.events.listeners.JoinListener;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.help.HelpMessenger;
import com.xg7plugins.help.chat.HelpChat;
import com.xg7plugins.help.form.HelpForm;
import com.xg7plugins.help.menu.HelpGUI;
import com.xg7plugins.help.xg7pluginshelp.XG7PluginsHelpForm;
import com.xg7plugins.help.xg7pluginshelp.XG7PluginsHelpGUI;
import com.xg7plugins.help.xg7pluginshelp.chathelp.XG7PluginsChatHelp;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.menus.lang.LangForm;
import com.xg7plugins.menus.lang.LangMenu;
import com.xg7plugins.menus.tasks.TaskMenu;
import com.xg7plugins.modules.Module;
import com.xg7plugins.modules.ModuleManager;
import com.xg7plugins.modules.xg7dialogs.XG7Dialogs;
import com.xg7plugins.modules.xg7geyserforms.XG7GeyserForms;
import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import com.xg7plugins.modules.xg7holograms.XG7Holograms;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7npcs.XG7NPCs;
import com.xg7plugins.modules.xg7scores.XG7Scores;
import com.xg7plugins.modules.xg7scores.organizer.impl.LuckpermsRule;
import com.xg7plugins.modules.xg7scores.organizer.impl.OPRule;
import com.xg7plugins.server.ServerInfo;
import com.xg7plugins.tasks.TaskManager;
import com.xg7plugins.tasks.plugin_tasks.DatabaseKeepAlive;
import com.xg7plugins.tasks.plugin_tasks.TPSCalculator;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Metrics;
import com.xg7plugins.loader.VersionChecker;
import com.xg7plugins.utils.PluginKey;
import com.xg7plugins.utils.XG7PluginsPlaceholderExpansion;
import com.xg7plugins.modules.xg7dialogs.listener.DialogListener;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Main class for the XG7Plugins framework.
 * Manages loading, enabling, disabling, and reloading of plugins and their components.
 */
@Getter(AccessLevel.PUBLIC)
@PluginSetup(
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
        mainCommandAliases = { "7pl", "7pls", "xg7pl" },
        reloadCauses = { "json", "modules" },
        collaborators = {
                @Collaborator(uuid = "45766b7f-9789-40e1-bd0b-46fa0d032bde", name = "&aDaviXG7", role = "&bCreator of all plugin"),
                @Collaborator(uuid = "3b57c818-7cc7-4553-bb58-cf01a09b2dd1", name = "&aAceitou", role = "&bVideo editor"),
        },
        metricsId = 24626
)
public class XG7Plugins extends Plugin {

    private ServerInfo serverInfo;
    private VersionChecker versionChecker;

    private DependencyManager dependencyManager;
    private CacheManager cacheManager;
    private TaskManager taskManager;
    private EventManager eventManager;
    private DatabaseManager databaseManager;
    private LangManager langManager;
    private JsonManager jsonManager;
    private PacketEventManager packetEventManager;
    private CooldownManager cooldownManager;
    private ModuleManager moduleManager;

    private final ConcurrentHashMap<JavaPlugin, Plugin> plugins = new ConcurrentHashMap<>();


    public XG7Plugins(JavaPlugin plugin) {
        super(plugin);
        this.api = new XG7PluginsAPI(this);
    }

    @Override
    public void onLoad() {

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(javaPlugin));
        PacketEvents.getAPI().getSettings()
                .bStats(false)
                .checkForUpdates(true);
        PacketEvents.getAPI().load();

        debug.setupDebugMode();

        versionChecker = new VersionChecker();

        debug.info("load","Loading pre-load managers...");

        this.dependencyManager = new DependencyManager();
        this.cacheManager = new CacheManager(this);
        this.taskManager = new TaskManager();
        this.databaseManager = new DatabaseManager(this);

    }

    @Override
    public void onEnable() {
        debug.info("load", "Enabling XG7Plugins...");
        PacketEvents.getAPI().init();

        debug.info("load","Loading plugin configurations...");

        XG7Plugins.getAPI().configManager(this).registerAdapter(new LangItemTypeAdapter());
        XG7Plugins.getAPI().configManager(this).registerAdapter(new SoundTypeAdapter());

        debug.info("load","Loading managers...");

        this.langManager = new LangManager(this, new String[] { "en", "pt", "es" });
        this.jsonManager = new JsonManager(this);
        this.eventManager = new EventManager();
        this.packetEventManager = new PacketEventManager();
        this.cooldownManager = new CooldownManager(this);
        this.moduleManager = new ModuleManager();
        moduleManager.registerModules(new XG7GeyserForms(), new XG7Menus(), new XG7Scores(), new XG7Holograms(), new XG7NPCs(), new XG7Dialogs());


        debug.info("load","Loading server info...");

        this.serverInfo = new ServerInfo();

        if (ConfigFile.mainConfigOf(this).root().get("organize-tablist", true)) {

            debug.info("load","Loading tab organizer...");

            XG7Scores scores = XG7Plugins.getAPI().scores();

            scores.registerTablistOrgaizerRule(new OPRule());

            if (XG7Plugins.getAPI().isDependencyEnabled("LuckPerms")) {

                LuckPerms luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();

                luckPerms.getGroupManager().loadAllGroups().thenRun(() -> luckPerms.getGroupManager().getLoadedGroups().forEach(group -> scores.registerTablistOrgaizerRule(new LuckpermsRule(group))));

            }
        }

        taskManager.runSync(BukkitTask.of(() -> {

            List<CommandSender> commandSenders = Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).collect(Collectors.toList());

            commandSenders.add(Bukkit.getConsoleSender());

            versionChecker.notify(commandSenders, XG7Plugins.getAPI().getAllXG7Plugins());

        }));
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§cServer is restarting..."));

        debug.info("load","Stopping cooldowns...");
        XG7Plugins.getAPI().cooldowns().removeAll();
        XG7Plugins.getAPI().cooldowns().cancelTask();

        debug.info("load","Stopping tasks...");
        XG7Plugins.getAPI().taskManager().shutdown();

        debug.info("load","Stopping scores...");
        XG7Plugins.getAPI().scores().onDisable();

        debug.info("load","Stopping cache...");
        XG7Plugins.getAPI().cacheManager().shutdown();

        debug.info("load","Disabling modules...");
        XG7Plugins.getAPI().moduleManager().disableAllModules();

        debug.info("load","Stopping PacketEvents...");
        PacketEvents.getAPI().terminate();

        debug.info("load","Stopping database...");
        try {
            XG7Plugins.getAPI().database().shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onReload(ReloadCause cause) {
        super.onReload(cause);
        if (cause.equals("json")) XG7Plugins.getAPI().jsonManager().invalidateCache();

        if (cause.equals(ReloadCause.EVENTS)) {
            XG7Plugins.getAPI().moduleManager().getModules().values().stream().filter(Module::isEnabled).forEach(XG7Plugins.getAPI().moduleManager()::loadListeners);
        }
        if (cause.equals("modules")) {
            XG7Plugins.getAPI().moduleManager().getModules().values().stream().filter(Module::isEnabled).forEach(XG7Plugins.getAPI().moduleManager()::reloadModule);
        }

        this.loadHelp();

        List<CommandSender> commandSenders = Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).collect(Collectors.toList());
        commandSenders.add(Bukkit.getConsoleSender());

        versionChecker.notify(commandSenders, XG7Plugins.getAPI().getAllXG7Plugins());
    }

    public Class<? extends Entity<?, ?>>[] loadDBEntities() {
        return new Class[] { PlayerData.class };
    }

    @Override
    public List<Repository<?, ?>> loadRepositories() {
        return Collections.singletonList(new PlayerDataRepository());
    }

    @Override
    public List<Command> loadCommands() {
        return Arrays.asList(new LangCommand(), new ReloadCommand(), new TaskCommand(), new CommentCommand(), new UpdateCommand(), new ConfigCommand());
    }

    @Override
    public List<Listener> loadEvents() {
        return Collections.singletonList(new JoinListener());
    }

    public List<PacketListener> loadPacketEvents() {
        return Collections.singletonList(new DialogListener());
    }

    @Override
    public List<TimerTask> loadRepeatingTasks() {
        return Arrays.asList(XG7Plugins.getAPI().cooldowns().getTask(), new DatabaseKeepAlive(), new TPSCalculator());
    }

    @Override
    public List<Dependency> loadDependencies() {
        return Collections.singletonList(
                Dependency.of(
                        "PlaceholderAPI",
                        "https://github.com/PlaceholderAPI/PlaceholderAPI/releases/download/2.11.7/PlaceholderAPI-2.11.7.jar",
                        false
                )
        );
    }

    @Override
    public Object loadPlaceholderExpansion() {
        return new XG7PluginsPlaceholderExpansion();
    }

    @Override
    public List<BasicMenu> loadMenus() {
        return Arrays.asList(new LangMenu(), new TaskMenu(this));
    }

    @Override
    public List<Form<?,?>> loadForms() {
        return Collections.singletonList(new LangForm());
    }

    @Override
    public HelpMessenger loadHelp() {

        HelpGUI helpCommandGUI = new HelpGUI(this, new XG7PluginsHelpGUI(this));

        HelpForm helpCommandForm = null;

        if (XG7Plugins.getAPI().isGeyserFormsEnabled()) {
            helpCommandForm = new HelpForm(new XG7PluginsHelpForm(this));
        }

        HelpChat helpInChat = new XG7PluginsChatHelp();

        return new HelpMessenger(this, helpCommandGUI, helpCommandForm, helpInChat);
    }

    /**
     * Loads the specified plugin, initializing its components and dependencies.
     * @param plugin The plugin to load
     */
    public void loadPlugin(Plugin plugin) {
        long msLoading = System.currentTimeMillis();

        registerPlugin(plugin);

        plugin.getDebug().setupDebugMode();

        plugin.getDebug().log("Loading " + plugin.getCustomPrefix() + "...");
        plugin.onLoad();

        debug.info("load","Loading metrics...");
        Metrics.loadMetrics(plugin.getJavaPlugin(), plugin.getPluginSetup().metricsId());

        debug.log("Checking plugin dependencies...");
        XG7Plugins.getAPI().dependencyManager().loadDependencies(plugin);

        if (!XG7Plugins.getAPI().dependencyManager().loadRequiredDependencies(plugin)) {
            debug.severe("Error on loading dependencies for " + plugin.getName() + ", skipping load...");
            unregisterPlugin(plugin);
            return;
        }

        for (String cause : plugin.getPluginSetup().reloadCauses()) ReloadCause.registerCause(plugin, new ReloadCause(cause));

        if (plugin != this) {
            plugin.getDebug().log("Loading plugin configurations...");

            XG7Plugins.getAPI().configManager(plugin).registerAdapter(new SoundTypeAdapter());
        }

        plugin.getDebug().log("Loading plugin extensions...");
        plugin.getExtensionManager().loadExtensions();

        plugin.getDebug().log(plugin.getName() + " loaded in §b" + (System.currentTimeMillis() - msLoading) + "ms§r.");

    }

    /**
     * Enables the specified plugin, activating its features and registering its components.
     * @param plugin The plugin to enable
     */
    public void enablePlugin(Plugin plugin) {
        long msEnabling = System.currentTimeMillis();

        plugin.getDebug().log("Enabling " + plugin.getCustomPrefix() + "...");
        if (plugin.getPluginSetup().onEnableDraw().length != 0) {
            Arrays.stream(plugin.getPluginSetup().onEnableDraw()).forEach(Bukkit.getConsoleSender()::sendMessage);
            Bukkit.getConsoleSender().sendMessage("Plugin version: " + plugin.getVersion());
            Bukkit.getConsoleSender().sendMessage("Found bug? Report us: https://discord.gg/yghhDAaCED");
            Bukkit.getConsoleSender().sendMessage("Consider donating <3: https://ko-fi.com/davixg7");
        }

        debug.log("Custom prefix: " + plugin.getCustomPrefix());

        plugin.getDebug().log("Connecting plugin to database...");
        XG7Plugins.getAPI().database().connectPlugin(plugin, plugin.loadDBEntities());

        plugin.onEnable();

        Connector connector = XG7Plugins.getAPI().database().getConnectorRegistry().getConnector(plugin);

        try {
            Connection connection = connector.getConnection(plugin);
            if (connection != null) {
                plugin.getDebug().info("load","Loading entities manager...");
                XG7Plugins.getAPI().database().registerRepositories(plugin.loadRepositories());
                if (connector.getType() != ConnectionType.SQLITE) connection.close();
            }
        } catch (Exception ignored) {
        }

        plugin.getDebug().info("load","Registering listeners...");
        XG7Plugins.getAPI().eventManager().registerListeners(plugin, plugin.loadEvents());

        plugin.getDebug().info("load","Registering commands...");
        XG7Plugins.getAPI().commandManager(plugin).registerCommands(plugin.loadCommands());

        plugin.getDebug().info("load","Registering packet events...");
        XG7Plugins.getAPI().packetEventManager().registerListeners(plugin, plugin.loadPacketEvents());

        plugin.getDebug().info("load","Registering tasks...");
        XG7Plugins.getAPI().taskManager().registerTimerTasks(plugin.loadRepeatingTasks());

        plugin.getDebug().info("load","Loading langs...");
        XG7Plugins.getAPI().langManager().loadLangsFrom(plugin);

        if (XG7Plugins.getAPI().menus().isEnabled()) {
            plugin.getDebug().info("load","Loading default menus...");
            XG7Plugins.getAPI().menus().registerMenus(plugin.loadMenus());
        }

        if (XG7Plugins.getAPI().geyserForms().isEnabled()) {
            plugin.getDebug().info("load","Loading default geyser forms...");
            List<Form<?,?>> forms = plugin.loadForms();
            if (forms != null && !forms.isEmpty()) forms.forEach(XG7Plugins.getAPI().geyserForms()::registerForm);
        }
        if (XG7Plugins.getAPI().scores().isEnabled()) {
            plugin.getDebug().info("load","Loading default scores...");
            XG7Plugins.getAPI().scores().registerScores(plugin.loadScores());
        }
        if (XG7Plugins.getAPI().holograms().isEnabled()) {
            plugin.getDebug().info("load","Loading default holograms...");
            List<Hologram> holograms = plugin.loadHolograms();
            if (holograms != null && !holograms.isEmpty()) holograms.forEach(XG7Plugins.getAPI().holograms()::registerHologram);
        }

        plugin.getDebug().info("load","Loading help...");
        plugin.setHelpMessenger(loadHelp());

        if (XG7Plugins.getAPI().dependencyManager().exists("PlaceholderAPI")) {

            PlaceholderExpansion placeholderExpansion = (PlaceholderExpansion) plugin.loadPlaceholderExpansion();

            if (placeholderExpansion == null)
                return;

            plugin.getDebug().info("load","Registering PlaceholderAPI expansion...");

            placeholderExpansion.register();
        }

        plugin.setEnabled(true);

        plugin.getDebug().log("Enabling extensions...");

        plugin.getExtensionManager().enableExtensions();

        taskManager.runSync(BukkitTask.of(() -> {
            if (!ConfigFile.mainConfigOf(this).root().get("anti-tab", false)) return;

            debug.info("load","Loading anti-tab feature...");

            XG7Plugins.getAPI().packetEventManager().registerListeners(this, XG7Plugins.getAPI().commandManager(plugin).getAntiTab());
        }));

        plugin.getDebug().log(plugin.getName() + " enabled in §b" + (System.currentTimeMillis() - msEnabling) + "ms§r.");
    }

    /**
     * Disables the specified plugin, deactivating its features and unregistering its components.
     * @param plugin The plugin to disable
     */
    public void disablePlugin(Plugin plugin) {
        debug.log("Disabling " + plugin.getName() + "...");

        plugin.onDisable();

        debug.info("info", "Disabling plugin events...");
        XG7Plugins.getAPI().eventManager().unregisterListeners(plugin);
        XG7Plugins.getAPI().packetEventManager().unregisterListeners(plugin);

        debug.info("info", "Disconnecting plugin from database...");
        XG7Plugins.getAPI().database().disconnectPlugin(plugin);

        plugin.setEnabled(false);

        plugin.getDebug().log("Disabling extensions...");

        plugin.getExtensionManager().disableExtensions();
    }

    /**
     * Registers the specified plugin in the framework's plugin registry.
     * @param plugin The plugin to register
     */
    public void registerPlugin(Plugin plugin) {
        this.plugins.put(plugin.getJavaPlugin(), plugin);
    }

    /**
     * Unregisters the specified plugin from the framework's plugin registry.
     * @param plugin The plugin to unregister
     */
    public void unregisterPlugin(Plugin plugin) {
        this.plugins.remove(plugin.getJavaPlugin());
    }

    /**
     * Retrieves the singleton instance of the XG7Plugins framework.
     * @return The XG7Plugins instance
     */
    public static XG7Plugins getInstance() {
        return (XG7Plugins) XG7PluginsLoader.getCore();
    }

    /**
     * Retrieves the API instance for interacting with the XG7Plugins framework.
     * @return The XG7PluginsAPI instance
     */
    public static XG7PluginsAPI getAPI() {
        return (XG7PluginsAPI) XG7Plugins.getInstance().getApi();
    }

    /**
     * Creates a PluginKey for the specified plugin ID.
     * @param id The plugin ID
     * @return The PluginKey instance
     */
    public static PluginKey getPluginID(String id) {
        return PluginKey.of(XG7Plugins.getInstance(), id);
    }


}
