package com.xg7plugins.lang;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@Getter
@AllArgsConstructor
public class Lang {

    private final Plugin plugin;
    private final ConfigFile langConfigFile;
    private final String langId;

    public static CompletableFuture<Pair<Boolean, Lang>> of(Plugin plugin, Player player) {
        return XG7PluginsAPI.langManager().getLangByPlayer(plugin, player);
    }

    public String get(String path) {
        return langConfigFile.root().get(path, path + " not found on lang: " + langId + " in plugin " + plugin.getName());
    }

    public ConfigSection getLangConfiguration() {
        return langConfigFile.root();
    }

}
