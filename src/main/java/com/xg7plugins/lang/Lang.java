package com.xg7plugins.lang;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@Getter
@AllArgsConstructor
public class Lang {

    private final Plugin plugin;
    private final Config langConfiguration;
    private final String langId;
    private final boolean selected;

    public Lang(Plugin plugin, Config langConfiguration, String langId) {
        this(plugin, langConfiguration, langId, false);
    }

    public static CompletableFuture<Lang> of(Plugin plugin, Player player) {
        LangManager langManager = XG7PluginsAPI.langManager();
        return langManager.getLangByPlayer(plugin, player);
    }

    public String get(String path) {
        return langConfiguration.get(path, String.class).orElse("Path not found on lang: " + langId + " in plugin " + plugin.getName());
    }

}
