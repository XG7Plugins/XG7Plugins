package com.xg7plugins;

import com.github.retrooper.packetevents.PacketEvents;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.PluginSetup;
import com.xg7plugins.cache.CacheManager;
import com.xg7plugins.commands.core_commands.LangCommand;
import com.xg7plugins.commands.core_commands.ReloadCause;
import com.xg7plugins.commands.core_commands.ReloadCommand;
import com.xg7plugins.commands.core_commands.task_command.TaskCommand;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataDAO;
import com.xg7plugins.dependencies.Dependency;
import com.xg7plugins.dependencies.DependencyManager;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.help.formhelp.HelpCommandForm;
import com.xg7plugins.help.guihelp.HelpCommandGUI;
import com.xg7plugins.help.xg7pluginshelp.XG7PluginsHelpForm;
import com.xg7plugins.help.xg7pluginshelp.XG7PluginsHelpGUI;
import com.xg7plugins.help.xg7pluginshelp.chathelp.XG7PluginsChatHelp;
import com.xg7plugins.plugin_menus.LangForm;
import com.xg7plugins.plugin_menus.LangMenu;
import com.xg7plugins.plugin_menus.TaskMenu;
import com.xg7plugins.modules.ModuleManager;
import com.xg7plugins.lang.LangItemTypeAdapter;
import com.xg7plugins.lang.LangManager;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.events.defaultevents.JoinListener;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.modules.xg7geyserforms.XG7GeyserForms;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7scores.XG7Scores;
import com.xg7plugins.server.ServerInfo;
import com.xg7plugins.tasks.*;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.Metrics;
import com.xg7plugins.utils.XG7PluginsPlaceholderExpansion;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

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
        mainCommandAliases = {"7plugins", "7pl", "7pls", "xg7pl"},
        reloadCauses = {"json"}
)
public final class XG7Plugins extends Plugin {

    private ServerInfo serverInfo;

    private TPSCalculator tpsCalculator;

    private PlayerDataDAO playerDataDAO;

    private final ConcurrentHashMap<String, Plugin> plugins = new ConcurrentHashMap<>();

    /*
        Carrega o plugin
     */
    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .bStats(false)
                .checkForUpdates(true);
        PacketEvents.getAPI().load();
        super.onLoad();
    }

    /*
        Habilita o plugin

     */
    @Override
    public void onEnable() {
        super.onEnable();
        debug.loading("Enabling XG7Plugins...");
        PacketEvents.getAPI().init();

        debug.loading("Checking dependencies...");

        managerRegistry.registerManagers(new DependencyManager());

        debug.loading("Loading metrics...");

        Metrics.getMetrics(this, 24626);

        debug.loading("Starting tps calculator...");
        this.tpsCalculator = new TPSCalculator();
        tpsCalculator.start();

        debug.loading("Loading managers...");

        managerRegistry.registerManagers(
                new TaskManager(this),
                new CacheManager(this),
                new DatabaseManager(this),
                new LangManager(this, new String[]{"en", "pt", "es"}),
                new JsonManager(this),
                new EventManager(),
                new PacketEventManager(),
                new CooldownManager(this),
                new ModuleManager(new XG7GeyserForms(), new XG7Menus(), new XG7Scores())
        );

        this.playerDataDAO = new PlayerDataDAO();

        debug.loading("Loading server info...");
        try {
            this.serverInfo = new ServerInfo(this);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        debug.loading("Loading Menus...");

        XG7Menus menus = XG7Menus.getInstance();

        menus.registerMenus(new LangMenu(this), new TaskMenu(this));

        if (XG7PluginsAPI.isGeyserFormsEnabled()) {
            debug.loading("Loading GeyserForms...");
            XG7GeyserForms geyserForms = XG7GeyserForms.getInstance();
            geyserForms.registerForm(new LangForm());
        }

        debug.loading("Loading plugins...");
        register(this);
        plugins.forEach((name, plugin) -> loadPlugin(plugin));

        XG7PluginsAPI.configManager(this).registerAdapter(Item.class, new LangItemTypeAdapter());

        debug.loading("Registering PlaceholderAPI expansion...");
        if (XG7PluginsAPI.isDependencyEnabled("PlaceholderAPI")) new XG7PluginsPlaceholderExpansion().register();

        debug.loading("XG7Plugins enabled.");

    }

    /*
        Desabilita o plugin
     */
    @Override
    public void onDisable() {
        super.onDisable();
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§cServer is restarting..."));
        debug.loading("Stopping tpsCalculator...");
        tpsCalculator.cancel();
        this.plugins.forEach((name, plugin) -> unregister(plugin));

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
    }

    /**
        Recarrega o plugin

        @param cause causa do reload

     */

    @Override
    public void onReload(ReloadCause cause) {
        super.onReload(cause);
        if (cause.equals("json")) XG7PluginsAPI.jsonManager().invalidateCache();
    }
    public Class<? extends Entity<?,?>>[] loadEntities() {
        return new Class[]{PlayerData.class};
    }
    @Override
    public List<Command> loadCommands() {
        return Arrays.asList(new LangCommand(), new ReloadCommand(), new TaskCommand());
    }
    @Override
    public List<Listener> loadEvents() {
        return Collections.singletonList(new JoinListener());
    }
    @Override
    public List<Task> loadRepeatingTasks() {
        return Arrays.asList(XG7PluginsAPI.cooldowns().getTask(), new DatabaseKeepAlive());
    }
    @Override
    public List<Dependency> loadDependencies() {
        return Collections.singletonList(Dependency.of("PlaceholderAPI", "https://www.spigotmc.org/resources/placeholderapi.6245/download?version=541946"));
    }

    // Carregar ajuda
    @Override
    public void loadHelp() {
        this.helpCommandGUI = new HelpCommandGUI(this, new XG7PluginsHelpGUI(this));
        if (XG7Plugins.isDependencyEnabled("floodgate") && Config.mainConfigOf(this).get("enable-geyser-forms",Boolean.class).orElse(false)) this.helpCommandForm = new HelpCommandForm(new XG7PluginsHelpForm(this));
        this.helpInChat = new XG7PluginsChatHelp();
    }

    private void loadPlugin(Plugin plugin) {
        debug.loading("Enabling " + plugin.getName() + "...");

        plugin.getDebug().loading("Connecting plugin to database...");
        XG7PluginsAPI.database().connectPlugin(plugin, plugin.loadEntities());

        if (plugin != this) Bukkit.getPluginManager().enablePlugin(plugin);

        plugin.getDebug().loading("Registering listeners...");
        XG7PluginsAPI.eventManager().registerListeners(plugin, plugin.loadEvents());

        plugin.getDebug().loading("Registering commands...");
        XG7PluginsAPI.commandManager(plugin).registerCommands(plugin.loadCommands());

        plugin.getDebug().loading("Registering packet events...");
        XG7PluginsAPI.packetEventManager().registerListeners(plugin, plugin.loadPacketEvents());

        plugin.getDebug().loading("Registering tasks...");
        XG7PluginsAPI.taskManager().registerTasks(plugin.loadRepeatingTasks());

        plugin.getDebug().loading("Loading langs...");
        XG7PluginsAPI.langManager().loadLangsFrom(plugin);

        plugin.getDebug().loading("Loading help...");
        plugin.loadHelp();
    }

    public static void register(Plugin plugin) {
        Debug.of(XG7Plugins.getInstance()).loading("Registering " + plugin.getName() + "...");
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        xg7Plugins.getPlugins().put(plugin.getName().split(" ")[0], plugin);
        Debug.of(XG7Plugins.getInstance()).loading(plugin.getName() + " registered.");
    }

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

    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }

}
