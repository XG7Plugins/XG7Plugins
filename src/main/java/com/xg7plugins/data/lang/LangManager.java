package com.xg7plugins.data.lang;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.reflection.nms.PlayerNMS;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Getter
public class LangManager {

    private final XG7Plugins plugin;
    private final Cache<String, YamlConfiguration> langs;
    private final String mainLang;
    private final String[] defLangs;
    private final PlayerLanguageDAO playerLanguageDAO;

    public LangManager(XG7Plugins plugin, String[] defaultLangs) {
        this.plugin = plugin;
        this.defLangs = defaultLangs;
        this.playerLanguageDAO = new PlayerLanguageDAO();

        Config config = XG7Plugins.getInstance().getConfigsManager().getConfig("config");

        this.mainLang = config.get("main-lang", String.class).orElse("en-us");
        this.langs = Caffeine.newBuilder()
                .expireAfterAccess(config.getTime("lang-cache-expires").orElse(30 * 60 * 1000L), TimeUnit.MILLISECONDS)
                .build();

        plugin.getLog().loading("Loaded!");
    }

    @SneakyThrows
    public CompletableFuture<Void> loadLangsFrom(Plugin plugin) {
        return CompletableFuture.runAsync(() -> {
                    File dir = new File(plugin.getDataFolder(), "langs");
                    if (!dir.exists()) dir.mkdirs();
                    if (dir.listFiles() != null && dir.listFiles().length != 0) {
                        Arrays.stream(dir.listFiles()).forEach(file -> langs.put(plugin.getName() + ":" + file.getName().substring(0, file.getName().length() - 4), YamlConfiguration.loadConfiguration(file)));
                    }
                    for (String lang : defLangs) {
                        if (langs.asMap().containsKey(lang)) continue;
                        File file = new File(dir, lang + ".yml");
                        if (!file.exists()) plugin.saveResource("langs/" + lang + ".yml", false);
                        langs.put(plugin.getName() + ":" + lang, YamlConfiguration.loadConfiguration(file));
                    }
                }
        );

    }

    public CompletableFuture<YamlConfiguration> getLang(Plugin plugin, String lang) {
        if (lang == null) lang = mainLang;
        YamlConfiguration config = langs.getIfPresent(plugin.getName() + ":" + lang);
        if (config != null) return CompletableFuture.completedFuture(config);

        String finalLang = lang;
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(plugin.getDataFolder(), "langs/" + finalLang.split(":")[1] + ".yml");
            if (!file.exists()) plugin.saveResource("langs/" + finalLang.split(":")[1] + ".yml", false);

            YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(file);

            langs.put(finalLang, newConfig);

            return newConfig;
        }, XG7Plugins.taskManager().getAsyncExecutors().get("files"));
    }

    public CompletableFuture<String> getPath(Plugin plugin, Player player, String path) {
        return getLangByPlayer(plugin, player).thenApply(config -> config.getString(path));
    }

    @SneakyThrows
    public CompletableFuture<YamlConfiguration> getLangByPlayer(Plugin plugin, Player player) {
        if (player == null) return getLang(plugin, mainLang);

        return playerLanguageDAO.get(player.getUniqueId()).thenCompose(lang -> {

            if (lang != null) return getLang(plugin, lang.getLangId());

            String langId = mainLang;

            if (XG7Plugins.getInstance().getConfig("config").get("auto-chose-lang", Boolean.class).orElse(false)){
                String playerLocale = XG7Plugins.getMinecraftVersion() >= 12 ? player.getLocale() : PlayerNMS.cast(player).getCraftPlayerHandle().getField("locale");
                System.out.println("Player locale " + playerLocale);
                if (langs.asMap().containsKey(playerLocale)) langId = playerLocale;
            }
            PlayerLanguage newLang = new PlayerLanguage(player.getUniqueId(), langId);
            try {
                final String finalLangID = langId;
                return playerLanguageDAO.add(newLang).thenCompose(v -> getLang(plugin, finalLangID));
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }


}
