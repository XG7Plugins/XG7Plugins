package com.xg7plugins.lang;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.XG7Plugins;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.data.playerdata.PlayerDataRepository;

import com.xg7plugins.utils.FileUtil;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Getter
public class LangManager {

    private final XG7Plugins plugin;
    private final ObjectCache<String, Lang> langs;
    private final String mainLang;
    private final String[] defLangs;
    private final boolean langEnabled;

    public LangManager(XG7Plugins plugin, String[] defaultLangs) {
        this.plugin = plugin;
        this.defLangs = defaultLangs;

        ConfigSection config = ConfigFile.mainConfigOf(plugin).root();

        this.langEnabled = config.get("lang-enabled", false);

        this.mainLang = config.get("main-lang", "en");
        this.langs = new ObjectCache<>(
                plugin,
                config.getTimeInMilliseconds("lang-cache-expires", 30 * 60 * 1000L),
                false,
                "langs",
                true,
                String.class,
                Lang.class
        );

        plugin.getDebug().info("langs", "Loaded!");
    }

    public CompletableFuture<Void> loadLangsFrom(Plugin plugin) {
        return CompletableFuture.runAsync(() -> {
            if (!langEnabled) {
                loadLang(plugin, mainLang).join();
                return;
            }
            for (String lang : defLangs) loadLang(plugin, lang).join();

        }, XG7Plugins.getAPI().taskManager().getExecutor("files"));
    }

    public CompletableFuture<Void> loadLang(Plugin plugin, String lang) {
        return CompletableFuture.runAsync(() -> {
            if (langs.containsKey(plugin.getName() + ":" + lang).join()) return;

            File langFolder = new File(plugin.getJavaPlugin().getDataFolder(), "langs");
            if (!langFolder.exists()) langFolder.mkdirs();

            FileUtil.createOrSaveResource(plugin, "langs/" +  lang + ".yml");
            langs.put(plugin.getName() + ":" + lang, new Lang(plugin, ConfigFile.of("langs/" + lang, plugin), lang));
        }, XG7Plugins.getAPI().taskManager().getExecutor("files"));
    }

    public CompletableFuture<Lang> getLang(Plugin plugin, String lang) {

        String finalLang;

        if (!langEnabled) finalLang = mainLang;
        else finalLang = lang;

        if (langs.containsKey(plugin.getName() + ":" + finalLang).join()) {
            return CompletableFuture.completedFuture(langs.get(plugin.getName() + ":" + finalLang).join());
        }

        return CompletableFuture.supplyAsync(() -> {

            loadLang(plugin, finalLang).join();

            return langs.get(plugin.getName() + ":" + finalLang).join();

        }, XG7Plugins.getAPI().taskManager().getExecutor("langs"));
    }

    public CompletableFuture<Pair<Boolean, Lang>> getLangByPlayer(Plugin plugin, Player player) {
        if (!langEnabled || player == null)
            return CompletableFuture.supplyAsync(() -> new Pair<>(false, getLang(plugin, mainLang).join()), XG7Plugins.getAPI().taskManager().getExecutor("langs"));

        if (XG7Plugins.getAPI().database().containsCachedEntity(this.plugin, player.getUniqueId().toString()).join()) {
            PlayerData data = (PlayerData) XG7Plugins.getAPI().database().getCachedEntity(this.plugin, player.getUniqueId().toString()).join();
            return getLangByPlayerData(plugin, data);
        }

        return CompletableFuture.supplyAsync(() -> {
            PlayerData playerData = XG7Plugins.getAPI().getRepository(PlayerDataRepository.class).get(player.getUniqueId());
            return getLangByPlayerData(plugin, playerData).join();
        }, XG7Plugins.getAPI().taskManager().getExecutor("langs"));
    }

    private CompletableFuture<Pair<Boolean, Lang>> getLangByPlayerData(Plugin plugin, PlayerData playerData) {
        return CompletableFuture.supplyAsync(() -> new Pair<>(true, getLang(plugin, playerData == null || playerData.getLangId() == null ? mainLang : playerData.getLangId()).join()), XG7Plugins.getAPI().taskManager().getExecutor("langs"));
    }

    public CompletableFuture<String> getNewLangFor(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null!");

        

        if (!langEnabled) return CompletableFuture.completedFuture(mainLang);

        return CompletableFuture.supplyAsync(() -> {

            if(!ConfigFile.mainConfigOf(plugin).root().get("auto-chose-lang", true)) return mainLang;

            loadLangsFrom(XG7Plugins.getInstance()).join();

            List<Lang> langs = this.langs.asMap().join().entrySet().stream().filter(e -> e.getKey().startsWith(XG7Plugins.getInstance().getName() + ":")).map(Map.Entry::getValue).collect(Collectors.toList());

            String locale = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_13) ? player.getLocale() : ReflectionObject.of(player).getMethod("getHandle").invokeToRObject().getField("locale");

            return langs.stream().filter(lang -> lang.get("locale").equalsIgnoreCase(locale)).findFirst().map(lang -> lang.getLangConfigFile().getName().replace("langs/", "")).orElse(mainLang);
        }, XG7Plugins.getAPI().taskManager().getExecutor("langs"));

    }

    public void clearCache() {
        langs.clear().join();
    }

    public boolean hasLang(String lang) {
        return langs.containsKey(lang).join();
    }




}
