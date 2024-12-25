package com.xg7plugins;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.PluginConfigurations;
import com.xg7plugins.commands.defaultCommands.LangCommand;
import com.xg7plugins.commands.defaultCommands.ReloadCommand;
import com.xg7plugins.commands.defaultCommands.TaskCommand;
import com.xg7plugins.commands.defaultCommands.TestCommand;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.Entity;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.data.lang.LangItemTypeAdapter;
import com.xg7plugins.data.lang.LangManager;
import com.xg7plugins.data.lang.PlayerLanguage;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.PacketEvent;
import com.xg7plugins.events.defaultevents.CommandAntiTab;
import com.xg7plugins.events.defaultevents.CommandAntiTabOlder;
import com.xg7plugins.events.defaultevents.JoinAndQuit;
import com.xg7plugins.events.packetevents.PacketManagerBase;
import com.xg7plugins.libs.xg7geyserforms.forms.Form;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.libs.xg7menus.menuhandler.MenuHandler;
import com.xg7plugins.libs.xg7menus.menuhandler.PlayerMenuHandler;
import com.xg7plugins.libs.xg7geyserforms.FormManager;
import com.xg7plugins.libs.xg7holograms.HologramsManager;
import com.xg7plugins.libs.xg7holograms.event.ClickEventHandler;
import com.xg7plugins.libs.xg7menus.MenuManager;
import com.xg7plugins.libs.xg7npcs.NPCManager;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreListener;
import com.xg7plugins.libs.xg7scores.ScoreManager;
import com.xg7plugins.data.database.DBManager;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.events.packetevents.PacketEventManager1_7;
import com.xg7plugins.libs.xg7scores.builder.ScoreBoardBuilder;
import com.xg7plugins.menus.LangForm;
import com.xg7plugins.tasks.CooldownManager;
import com.xg7plugins.tasks.TaskManager;
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
        mainCommandAliases = {"7plugins", "7pl", "7pls"}
)
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
    private LangManager langManager;
    private EventManager eventManager;
    private TaskManager taskManager;
    private CooldownManager cooldownManager;
    private ScoreManager scoreManager;
    private PacketManagerBase packetEventManager;
    private MenuManager menuManager;
    private FormManager formManager;
    private JsonManager jsonManager;
    private HologramsManager hologramsManager;
    private NPCManager npcManager;

    private final ConcurrentHashMap<String, Plugin> plugins = new ConcurrentHashMap<>();

    public XG7Plugins() {
        getConfigsManager().registerAdapter(Item.class, new LangItemTypeAdapter());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        floodgate = Bukkit.getPluginManager().getPlugin("floodgate") != null;
        placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        getLog().loading("Enabling XG7Plugins...");

        Config config = getConfig("config");

        this.taskManager = new TaskManager(this);
        taskManager().registerExecutor("commands", Executors.newCachedThreadPool());
        taskManager().registerExecutor("database", Executors.newCachedThreadPool());
        taskManager().registerExecutor("files", Executors.newCachedThreadPool());
        taskManager().registerExecutor("menus", Executors.newCachedThreadPool());
        this.databaseManager = new DBManager(this);
        this.langManager = config.get("enable-langs", Boolean.class).orElse(false) ? new LangManager(this, new String[]{"en-us", "pt-br"}) : null;
        if (langManager == null) config.getConfigManager().putConfig("messages", new Config(this, "langs/" + config.get("main-lang", String.class).get()));
        else langManager.loadLangsFrom(this);
        this.jsonManager = new JsonManager();
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

            if (langManager == null) plugin.getConfigsManager().putConfig("messages", new Config(this, "langs/" + plugin.getConfig("config").get("main-lang", String.class).get()));
            else langManager.loadLangsFrom(plugin);
            Bukkit.getPluginManager().enablePlugin(plugin);
            plugin.getCommandManager().registerCommands(plugin.loadCommands());
            eventManager.registerPlugin(plugin, plugin.loadEvents());
            packetEventManager.registerPlugin(plugin, plugin.loadPacketEvents());
            databaseManager.connectPlugin(plugin, plugin.loadEntites());
            scoreManager.registerScores(plugin.loadScores());
            menuManager.registerMenus(plugin.loadMenus());
            if (formManager != null) {
                Form<?,?>[] forms = plugin.loadGeyserForms();
                if (forms != null) Arrays.stream(forms).filter(Form::isEnabled).forEach(form -> formManager.registerForm(form));
            }
            taskManager.registerTasks(plugin.loadRepeatingTasks());
        });

        getLog().loading("XG7Plugins enabled.");
    }


    @Override
    public void onDisable() {
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
    }

    @Override
    public void onReload() {

    }

    public Class<? extends Entity>[] loadEntites() {
        return new Class[]{PlayerLanguage.class};
    }
    @Override
    public ICommand[] loadCommands() {
        return new ICommand[]{new LangCommand(), new ReloadCommand(), new TaskCommand(), new TestCommand()};
    }

    @Override
    public Event[] loadEvents() {
        return new Event[]{new JoinAndQuit(), new ClickEventHandler(), new com.xg7plugins.libs.xg7npcs.event.ClickEventHandler(), minecraftVersion > 12 ? new CommandAntiTab() : null, new ScoreListener(), new MenuHandler(), new PlayerMenuHandler(menuManager)};
    }

    @Override
    public PacketEvent[] loadPacketEvents() {
        return minecraftVersion < 13 ? new PacketEvent[]{new CommandAntiTabOlder()} : super.loadPacketEvents();
    }

    @Override
    public Score[] loadScores() {
        scoreManager.initTask();
        return new Score[]{ScoreBoardBuilder.scoreBoard("teste")
                .title(Arrays.asList("§bTeste", "§7Teste", "§9Teste", "§1Teste", "§3Teste", "§5Teste", "§dTeste", "§fTeste"))
                .delay(500)
                .addLine("§bTeste")
                .addLine("§7Teste")
                .addLine("§9Teste")
                .addLine("§1Teste")
                .addLine("§aTeste looooooooooooooooooooooooooongo Nome %player_name% Direção: %player_direction%")
                .addLine("§5Teste")
                .addLine("§dTeste")
                .addLine("§fTeste fino senhores")
                .build(this)
        };
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


    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }

}
