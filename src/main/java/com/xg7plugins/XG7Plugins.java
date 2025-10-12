package com.xg7plugins;

import com.github.retrooper.packetevents.PacketEvents;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.setup.Collaborator;
import com.xg7plugins.boot.setup.PluginSetup;
import com.xg7plugins.boot.VersionChecker;
import com.xg7plugins.cache.CacheManager;
import com.xg7plugins.commands.impl.CommentCommand;
import com.xg7plugins.commands.impl.LangCommand;
import com.xg7plugins.commands.impl.reload.ReloadCause;
import com.xg7plugins.commands.impl.reload.ReloadCommand;
import com.xg7plugins.commands.impl.task_command.TaskCommand;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.cooldowns.CooldownManager;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.config.typeadapter.impl.SoundTypeAdapter;
import com.xg7plugins.data.database.dao.Repository;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;
import com.xg7plugins.dependencies.Dependency;
import com.xg7plugins.dependencies.DependencyManager;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.help.HelpMessenger;
import com.xg7plugins.help.chat.HelpChat;
import com.xg7plugins.help.form.HelpForm;
import com.xg7plugins.help.menu.HelpGUI;
import com.xg7plugins.help.xg7pluginshelp.XG7PluginsHelpForm;
import com.xg7plugins.help.xg7pluginshelp.XG7PluginsHelpGUI;
import com.xg7plugins.help.xg7pluginshelp.chathelp.XG7PluginsChatHelp;
import com.xg7plugins.menus.lang.LangForm;
import com.xg7plugins.menus.lang.LangMenu;
import com.xg7plugins.menus.tasks.TaskMenu;
import com.xg7plugins.modules.ModuleManager;
import com.xg7plugins.config.typeadapter.impl.LangItemTypeAdapter;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.listeners.JoinListener;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.modules.xg7geyserforms.XG7GeyserForms;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7scores.XG7Scores;
import com.xg7plugins.modules.xg7scores.organizer.impl.LuckpermsRule;
import com.xg7plugins.modules.xg7scores.organizer.impl.OPRule;
import com.xg7plugins.server.ServerInfo;
import com.xg7plugins.tasks.*;
import com.xg7plugins.tasks.plugin_tasks.DatabaseKeepAlive;
import com.xg7plugins.tasks.plugin_tasks.TPSCalculator;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.Metrics;
import com.xg7plugins.utils.XG7PluginsPlaceholderExpansion;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
        }
)
public final class XG7Plugins extends Plugin {

    private ServerInfo serverInfo;
    private VersionChecker versionChecker;

    private final ConcurrentHashMap<String, Plugin> plugins = new ConcurrentHashMap<>();

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .bStats(false)
                .checkForUpdates(true);
        PacketEvents.getAPI().load();
        versionChecker = new VersionChecker();
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        System.setProperty("adventure.text.warnWhenLegacyFormattingDetected", "false");
        debug.loading("Enabling XG7Plugins...");
        PacketEvents.getAPI().init();

        debug.loading("Loading metrics...");

        Metrics.getMetrics(this, 24626);

        debug.loading("Loading plugin configurations...");

        XG7PluginsAPI.configManager(this).registerAdapter(new LangItemTypeAdapter());
        XG7PluginsAPI.configManager(this).registerAdapter(new SoundTypeAdapter());

        debug.loading("Loading dependencies...");

        managerRegistry.registerManager(new DependencyManager());

        plugins.values().forEach(this::checkDependencies);

        if (XG7PluginsAPI.dependencyManager().isNeedRestart()) {
            debug.loading("======================================================================");
            debug.loading("§cShutdowning server for dependency updates... Please restart the server");
            debug.loading("======================================================================");
            Bukkit.shutdown();
            return;
        }

        debug.loading("Loading managers...");

        managerRegistry.registerManager(new CacheManager(this));
        managerRegistry.registerManager(new TaskManager());
        managerRegistry.registerManager(new DatabaseManager(this));
        managerRegistry.registerManager(new LangManager(this, new String[] { "en", "pt", "es" }));
        managerRegistry.registerManager(new JsonManager(this));
        managerRegistry.registerManager(new EventManager());
        managerRegistry.registerManager(new PacketEventManager());
        managerRegistry.registerManager(new CooldownManager(this));
        managerRegistry.registerManager(new ModuleManager(new XG7GeyserForms(), new XG7Menus(), new XG7Scores()));

        debug.loading("Loading server info...");

        this.serverInfo = new ServerInfo();

        debug.loading("Loading Menus...");

        XG7Menus menus = XG7Menus.getInstance();

        menus.registerMenus(new LangMenu(), new TaskMenu(this));

        if (XG7PluginsAPI.isGeyserFormsEnabled()) {
            debug.loading("Loading GeyserForms...");
            XG7GeyserForms geyserForms = XG7GeyserForms.getInstance();
            geyserForms.registerForm(new LangForm());
        }

        if (ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("organize-tablist", true)) {

            debug.loading("Loading tab organizer...");

            XG7Scores scores = XG7Scores.getInstance();

            scores.registerTablistOrgaizerRule(new OPRule());

            if (XG7PluginsAPI.isDependencyEnabled("LuckPerms")) {

                LuckPerms luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();

                luckPerms.getGroupManager().loadAllGroups().thenRun(() -> luckPerms.getGroupManager().getLoadedGroups().forEach(group -> scores.registerTablistOrgaizerRule(new LuckpermsRule(group))));

            }
        }

        debug.loading("Loading plugins...");

        plugins.forEach((name, plugin) -> loadPlugin(plugin));

        debug.loading("XG7Plugins enabled.");

        Bukkit.getScheduler().runTask(this, () -> {

            List<CommandSender> players = Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).collect(Collectors.toList());

            versionChecker.notify(players);
            versionChecker.notify(Collections.singletonList(Bukkit.getConsoleSender()));

        });

    }

    @Override
    public void onDisable() {
        super.onDisable();
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§cServer is restarting..."));

        this.plugins.forEach((name, plugin) -> unregister(plugin));

        debug.loading("Stopping cooldowns...");
        XG7PluginsAPI.cooldowns().removeAll();
        XG7PluginsAPI.cooldowns().cancelTask();

        debug.loading("Stopping tasks...");
        XG7PluginsAPI.taskManager().shutdown();

        debug.loading("Stopping scores...");
        XG7Scores.getInstance().onDisable();

        debug.loading("Stopping cache...");
        XG7PluginsAPI.cacheManager().shutdown();

        debug.loading("Disabling modules...");
        XG7PluginsAPI.moduleManager().disableModules();

        debug.loading("Stopping PacketEvents...");
        PacketEvents.getAPI().terminate();

        debug.loading("Stopping database...");
        try {
            XG7PluginsAPI.database().shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onReload(ReloadCause cause) {
        super.onReload(cause);
        if (cause.equals("json")) XG7PluginsAPI.jsonManager().invalidateCache();

        if (cause.equals(ReloadCause.EVENTS)) {
            XG7PluginsAPI.moduleManager().loadListeners();
        }
        if (cause.equals("modules")) {
            XG7PluginsAPI.moduleManager().reloadModules();
        }

        this.loadHelp();

        List<CommandSender> players = Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).collect(Collectors.toList());

        versionChecker.notify(players);
        versionChecker.notify(Collections.singletonList(Bukkit.getConsoleSender()));

    }

    public Class<? extends Entity<?, ?>>[] loadEntities() {
        return new Class[] { PlayerData.class };
    }

    @Override
    public List<Repository<?, ?>> loadRepositories() {
        return Collections.singletonList(new PlayerDataRepository());
    }

    @Override
    public List<Command> loadCommands() {
        return Arrays.asList(new LangCommand(), new ReloadCommand(), new TaskCommand(), new CommentCommand());
    }

    @Override
    public List<Listener> loadEvents() {
        return Collections.singletonList(new JoinListener());
    }

    @Override
    public List<TimerTask> loadRepeatingTasks() {
        return Arrays.asList(XG7PluginsAPI.cooldowns().getTask(), new DatabaseKeepAlive(), new TPSCalculator());
    }

    @Override
    public List<Dependency> loadDependencies() {
        return Collections.singletonList(Dependency.of("PlaceholderAPI",
                "https://ci.extendedclip.com/job/PlaceholderAPI/197/artifact/build/libs/PlaceholderAPI-2.11.6.jar"));
    }

    @Override
    public Object loadPlaceholderExpansion() {
        return new XG7PluginsPlaceholderExpansion();
    }

    @Override
    public void loadHelp() {

        HelpGUI helpCommandGUI = new HelpGUI(this, new XG7PluginsHelpGUI(this));

        HelpForm helpCommandForm = null;

        if (XG7PluginsAPI.isGeyserFormsEnabled()) {
            helpCommandForm = new HelpForm(new XG7PluginsHelpForm(this));
        }

        HelpChat helpInChat = new XG7PluginsChatHelp();

        this.helpMessenger = new HelpMessenger(this, helpCommandGUI, helpCommandForm, helpInChat);
    }

    /**
     * Handles loading and initialization of a plugin.
     * This method sets up all required components and configurations for a plugin
     * to function.
     *
     * @param plugin The plugin instance to load
     */
    private void loadPlugin(Plugin plugin) {
        debug.loading("Enabling " + plugin.getName() + "...");

        long msLoading = System.currentTimeMillis();

        debug.loading("Checking dependencies...");
        XG7PluginsAPI.dependencyManager().loadDependencies(plugin);

        if (!XG7PluginsAPI.dependencyManager().loadRequiredDependencies(plugin)) {
            debug.severe("Error on loading dependencies for " + plugin.getName() + ", disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if (plugin != this) {
            plugin.getDebug().loading("Loading plugin configurations...");

            XG7PluginsAPI.configManager(plugin).registerAdapter(new SoundTypeAdapter());
        }

        plugin.getDebug().loading("Connecting plugin to database...");
        XG7PluginsAPI.database().connectPlugin(plugin, plugin.loadEntities());

        if (plugin != this)
            Bukkit.getPluginManager().enablePlugin(plugin);

        plugin.getDebug().loading("Loading entities manager...");
        XG7PluginsAPI.database().registerRepositories(plugin.loadRepositories());

        plugin.getDebug().loading("Registering listeners...");
        XG7PluginsAPI.eventManager().registerListeners(plugin, plugin.loadEvents());

        plugin.getDebug().loading("Registering commands...");
        XG7PluginsAPI.commandManager(plugin).registerCommands(plugin.loadCommands());

        plugin.getDebug().loading("Registering packet events...");
        XG7PluginsAPI.packetEventManager().registerListeners(plugin, plugin.loadPacketEvents());

        plugin.getDebug().loading("Registering tasks...");
        XG7PluginsAPI.taskManager().registerTimerTasks(plugin.loadRepeatingTasks());

        plugin.getDebug().loading("Loading langs...");
        XG7PluginsAPI.langManager().loadLangsFrom(plugin);

        plugin.getDebug().loading("Loading help...");
        plugin.loadHelp();

        if (XG7PluginsAPI.dependencyManager().exists("PlaceholderAPI")) {

            PlaceholderExpansion placeholderExpansion = (PlaceholderExpansion) plugin.loadPlaceholderExpansion();

            if (placeholderExpansion == null)
                return;

            plugin.getDebug().loading("Registering PlaceholderAPI expansion...");

            placeholderExpansion.register();
        }

        debug.loading(plugin.getName() + " loaded in " + (System.currentTimeMillis() - msLoading) + "ms.");
    }

    /**
     * Registers a plugin with XG7Plugins.
     * Adds the plugin to the plugin registry to be managed by the core.
     *
     * @param plugin The plugin instance to register
     */
    public static void register(Plugin plugin) {
        Debug.of(XG7Plugins.getInstance()).loading("Registering " + plugin.getName() + "...");
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        xg7Plugins.getPlugins().put(plugin.getName().split(" ")[0], plugin);
        Debug.of(XG7Plugins.getInstance()).loading(plugin.getName() + " registered.");
    }

    /**
     * Unregisters a plugin from XG7Plugins.
     * Cleans up resources and removes the plugin from management.
     *
     * @param plugin The plugin instance to unregister
     */
    public static void unregister(Plugin plugin) {

        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        Debug debug = Debug.of(xg7Plugins);

        debug.loading("Unregistering " + plugin.getName() + "...");

        debug.loading("Disabling plugin events...");
        XG7PluginsAPI.eventManager().unregisterListeners(plugin);
        XG7PluginsAPI.packetEventManager().unregisterListeners(plugin);

        debug.loading("Disconnecting plugin from database...");
        XG7PluginsAPI.database().disconnectPlugin(plugin);

        xg7Plugins.getPlugins().remove(plugin.getName());
        debug.loading(plugin.getName() + " unregistered.");

    }

    private void checkDependencies(Plugin plugin) {
        debug.loading("Checking dependencies...");
        XG7PluginsAPI.dependencyManager().loadDependencies(plugin);

        if (!XG7PluginsAPI.dependencyManager().loadRequiredDependencies(plugin)) {
            debug.severe("Error on loading dependencies for " + plugin.getName() + ", disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
    }

    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }

}
