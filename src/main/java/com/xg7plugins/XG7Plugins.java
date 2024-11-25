package com.xg7plugins;

import com.xg7plugins.commands.defaultCommands.LangCommand;
import com.xg7plugins.commands.defaultCommands.ReloadCommand;
import com.xg7plugins.commands.defaultCommands.TaskCommands;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.database.Entity;
import com.xg7plugins.data.JsonManager;
import com.xg7plugins.data.lang.PlayerLanguage;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.PacketEvent;
import com.xg7plugins.events.defaultevents.CommandAntiTab;
import com.xg7plugins.events.defaultevents.CommandAntiTabOlder;
import com.xg7plugins.events.defaultevents.JoinAndQuit;
import com.xg7plugins.events.packetevents.PacketManagerBase;
import com.xg7plugins.libs.xg7geyserforms.FormManager;
import com.xg7plugins.libs.xg7geyserforms.builders.FormCreator;
import com.xg7plugins.libs.xg7holograms.HologramsManager;
import com.xg7plugins.libs.xg7holograms.event.ClickEventHandler;
import com.xg7plugins.libs.xg7menus.MenuManager;
import com.xg7plugins.libs.xg7menus.builders.BaseMenuBuilder;
import com.xg7plugins.libs.xg7menus.listeners.MenuListener;
import com.xg7plugins.libs.xg7menus.listeners.PlayerMenuListener;
import com.xg7plugins.libs.xg7npcs.NPCManager;
import com.xg7plugins.libs.xg7scores.ScoreListener;
import com.xg7plugins.libs.xg7scores.ScoreManager;
import com.xg7plugins.data.database.DBManager;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.events.packetevents.PacketEventManager1_7;
import com.xg7plugins.tasks.CooldownManager;
import com.xg7plugins.tasks.TaskManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.geysermc.cumulus.form.Form;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
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
        super("§bXG§37P§9lu§1gins§r", /* null will be default configs */ null,
                new String[]{
                        "§b __   _______ §3______ §9_____  _             §1_           ",
                        "§b \\ \\ / / ____|§3____ §9 |  __ \\| |           §1(_)          ",
                        "§b  \\ V / |  __  §3  / §9/| |__) | |_   _  __ _ §1_ _ __  ___ ",
                        "§b   > <| | |_ |  §3/ / §9|  ___/| | | | |/ _` | §1| '_ \\/ __|",
                        "§b  / . \\ |__| | §3/ / §9 | |    | | |_| | (_| | §1| | | \\__ \\",
                        "§b /_/ \\_\\_____|§3/_/  §9 |_|    |_|\\__,_|\\__,§1 |_|_| |_|___/",
                        "§9                                     __/ |            ",
                        "§9                                    |___/             "
                });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        floodgate = Bukkit.getPluginManager().getPlugin("floodgate") != null;
        placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        getLog().loading("Enabling XG7Plugins...");

        this.databaseManager = new DBManager(this);
        this.jsonManager = new JsonManager();
        this.hologramsManager = minecraftVersion < 8 ? null : new HologramsManager(this);
        this.npcManager = new NPCManager(this);
        this.menuManager = new MenuManager(this);
        this.eventManager = new EventManager();
        this.packetEventManager = minecraftVersion < 8 ? new PacketEventManager1_7() : new PacketEventManager();
        this.taskManager = new TaskManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.scoreManager = new ScoreManager(this);
        this.formManager = floodgate ? new FormManager() : null;

        Bukkit.getOnlinePlayers().forEach(player -> packetEventManager.create(player));

        getLog().loading("Loading plugins...");
        register(this);
        plugins.forEach((name, plugin) -> {
            getLog().info("Enabling " + plugin.getName() + "...");
            Bukkit.getPluginManager().enablePlugin(plugin);
            plugin.getCommandManager().registerCommands(plugin.loadCommands());
            eventManager.registerPlugin(plugin, plugin.loadEvents());
            packetEventManager.registerPlugin(plugin, plugin.loadPacketEvents());
            databaseManager.connectPlugin(plugin, plugin.loadEntites());
            scoreManager.registerScores(plugin.loadScores());
            BaseMenuBuilder<?,?>[] menus = plugin.loadMenus();
            if (menus != null) Arrays.stream(menus).forEach(menu -> menuManager.registerBuilder(menu.getId(), menu));
            if (formManager != null) {
                FormCreator<? extends Form,?>[] forms = plugin.loadGeyserForms();
                if (forms != null) Arrays.stream(forms).forEach(form -> formManager.registerCreator(form));
            }
            loadTasks();
        });

        getLog().loading("XG7Plugins enabled.");
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

    public Class<? extends Entity>[] loadEntites() {
        return new Class[]{PlayerLanguage.class};
    }
    @Override
    public ICommand[] loadCommands() {
        return new ICommand[]{new LangCommand(), new ReloadCommand(), new TaskCommands()};
    }

    @Override
    public Event[] loadEvents() {
        return new Event[]{new JoinAndQuit(), new ClickEventHandler(), new com.xg7plugins.libs.xg7npcs.event.ClickEventHandler(), minecraftVersion > 12 ? new CommandAntiTab() : null, new MenuListener(), new PlayerMenuListener(), new ScoreListener()};
    }

    @Override
    public PacketEvent[] loadPacketEvents() {
        return minecraftVersion < 13 ? new PacketEvent[]{new CommandAntiTabOlder()} : super.loadPacketEvents();
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


    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }

}
