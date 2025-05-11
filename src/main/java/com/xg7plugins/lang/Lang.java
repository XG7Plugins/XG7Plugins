package com.xg7plugins.lang;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Getter
@AllArgsConstructor
public class Lang {

    private final Plugin plugin;
    private final Config langConfiguration;
    private final String langid;
    private final boolean selected;

    public Lang(Plugin plugin, Config langConfiguration, String langid) {
        this(plugin, langConfiguration, langid, false);
    }

    public static CompletableFuture<Lang> of(Plugin plugin, Player player) {
        return CompletableFuture.supplyAsync(() -> {
            LangManager langManager = XG7PluginsAPI.langManager();
            return langManager.getLangByPlayer(plugin, player).join();
        });
    }

    public String get(String path) {
        return langConfiguration.get(path, String.class).orElse("Path not found on lang: " + langid + " in plugin " + plugin.getName());
    }

}
