package com.xg7plugins.lang;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.utils.reflection.nms.PlayerNMS;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Getter
public class LangManager {

    private final XG7Plugins plugin;
    private final ObjectCache<String, YamlConfiguration> langs;
    private final String mainLang;
    private final String[] defLangs;

    public LangManager(XG7Plugins plugin, String[] defaultLangs) {
        this.plugin = plugin;
        this.defLangs = defaultLangs;

        Config config = XG7Plugins.getInstance().getConfigsManager().getConfig("config");

        this.mainLang = config.get("main-lang", String.class).orElse("en-us");
        this.langs = new ObjectCache<>(
                plugin,
                config.getTime("lang-cache-expires").orElse(60 * 10 * 1000L),
                false,
                "langs",
                true,
                String.class,
                YamlConfiguration.class
        );

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
                        if (langs.containsKey(lang).join()) continue;
                        File file = new File(dir, lang + ".yml");
                        if (!file.exists()) plugin.saveResource("langs/" + lang + ".yml", false);
                        langs.put(plugin.getName() + ":" + lang, YamlConfiguration.loadConfiguration(file));
                    }
                }
        , XG7Plugins.taskManager().getAsyncExecutors().get("files"));

    }

    public CompletableFuture<YamlConfiguration> getLang(Plugin plugin, final String lang) {
        return CompletableFuture.supplyAsync(() -> {
            String finalLang = lang;
            if (finalLang == null) finalLang = mainLang;
            YamlConfiguration config = langs.get(plugin.getName() + ":" + finalLang).join();
            if (config != null) return config;

            File file = new File(plugin.getDataFolder(), "langs/" + (finalLang.contains(":") ? finalLang.split(":")[1] : finalLang) + ".yml");
            if (!file.exists()) plugin.saveResource("langs/" + (finalLang.contains(":") ? finalLang.split(":")[1] : finalLang) + ".yml", false);

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

        return XG7Plugins.getInstance().getPlayerDataDAO().get(player.getUniqueId()).thenCompose(lang -> {

            if (lang != null) return getLang(plugin, lang.getLangId());

            String langId = mainLang;

            if (XG7Plugins.getInstance().getConfig("config").get("auto-chose-lang", Boolean.class).orElse(false)){
                String playerLocale = XG7Plugins.getMinecraftVersion() >= 12 ? player.getLocale() : PlayerNMS.cast(player).getCraftPlayerHandle().getField("locale");
                plugin.getLog().info(player.getName() + " language: " + playerLocale);
                for (Map.Entry<String, YamlConfiguration> entry : langs.asMap().join().entrySet()) {
                    if (entry.getValue().getString("locale").equals(playerLocale)) {
                        langId = entry.getKey();
                        break;
                    }
                }
            }
            PlayerData newLang = new PlayerData(player.getUniqueId(), langId);
            try {
                final String finalLangID = langId;
                return XG7Plugins.getInstance().getPlayerDataDAO().add(newLang).thenCompose(v -> getLang(plugin, finalLangID));
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }


}
