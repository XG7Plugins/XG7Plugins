package com.xg7plugins.data.lang;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.reflection.PlayerNMS;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Getter
public class LangManager {

    private final Plugin plugin;
    private final Cache<String, YamlConfiguration> langs;
    private final String mainLang;
    private final String[] defLangs;
    private final PlayerLanguageDAO playerLanguageDAO;

    public LangManager(Plugin plugin, String[] defaultLangs) {
        this.plugin = plugin;
        this.defLangs = defaultLangs;
        this.playerLanguageDAO = new PlayerLanguageDAO(plugin);

        plugin.getLog().loading("Loading langs...");

        Config config = plugin.getConfigsManager().getConfig("config");

        this.mainLang = config.get("main-lang");
        this.langs = Caffeine.newBuilder()
                .expireAfterAccess(Text.convertToMilliseconds(plugin, config.get("lang-cache-expires")), TimeUnit.MILLISECONDS)
                .build();

        loadAllLangs();
        plugin.getLog().loading("Loaded!");
    }

    @SneakyThrows
    public void loadAllLangs() {
        File dir = new File(plugin.getDataFolder(), "langs");
        if (!dir.exists()) dir.mkdirs();
        if (dir.listFiles() != null && Objects.requireNonNull(dir.listFiles()).length != 0) {
            Arrays.stream(dir.listFiles()).forEach(file ->
                    langs.put(file.getName().substring(0, file.getName().length() - 4), YamlConfiguration.loadConfiguration(file)));
        }
        for (String lang : defLangs) {
            File file = new File(dir, lang + ".yml");
            if (!file.exists()) plugin.saveResource("langs/" + lang + ".yml", false);
            langs.put(lang, YamlConfiguration.loadConfiguration(file));
        }
    }

    public YamlConfiguration getLang(String lang) {
        YamlConfiguration config = langs.getIfPresent(lang);
        if (config != null) return config;


        File file = new File(plugin.getDataFolder(), "langs/" + lang + ".yml");
        if (!file.exists()) plugin.saveResource("langs/" + lang + ".yml", false);

        YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(file);

        langs.put(lang, newConfig);

        return newConfig;
    }

    public String getPath(Player player, String path) {
        YamlConfiguration langConfig = getLangByPlayer(player);
        return langConfig.getString(path);
    }

    public YamlConfiguration getLangByPlayer(Player player) {
        if (player == null) return getLang(mainLang);

        PlayerLanguage language = playerLanguageDAO.getLanguage(player.getUniqueId());

        if (language != null) return getLang(language.getLangId());

        Config config = plugin.getConfigsManager().getConfig("config");

        String langId = mainLang;

        String playerLocale = XG7Plugins.getMinecraftVersion() >= 12 ? player.getLocale() : PlayerNMS.cast(player).getCraftPlayerHandle().getField("locale");
        if (config.get("auto-chose-lang")) if (langs.asMap().containsKey(playerLocale)) langId = playerLocale;
        PlayerLanguage newLang = new PlayerLanguage(player.getUniqueId(), langId);
        playerLanguageDAO.addPlayerLanguage(newLang);
        return getLang(langId);

    }


}
