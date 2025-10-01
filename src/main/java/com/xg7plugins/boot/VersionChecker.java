package com.xg7plugins.boot;

import com.google.gson.JsonObject;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.http.HTTP;
import com.xg7plugins.utils.http.HTTPResponse;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class VersionChecker {


    public CompletableFuture<List<VersionModel>> getVersions() {
        return CompletableFuture.supplyAsync(() -> {

            List<VersionModel> models = new ArrayList<>();

            try {
                HTTPResponse response = HTTP.get("https://raw.githubusercontent.com/XG7Plugins/Versions/refs/heads/main/versions.json");

                if (response.getStatusCode() != 200) return Collections.emptyList();

                XG7PluginsAPI.getAllXG7PluginsNames().forEach(name -> {
                    JsonObject object = response.getJson().getAsJsonObject(name);
                    if (object == null) return;

                    models.add(new VersionModel(XG7PluginsAPI.getXG7Plugin(name), object.get("version").getAsString(), object.get("download_url").getAsString()));
                });

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return models;

        });
    }

    public void notify(List<CommandSender> senders) {
        if (ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("disable-version-check")) return;
        XG7Plugins.getInstance().getDebug().info("Checking for plugin updates...");
        getVersions().thenAccept(versions -> {

            for (VersionModel version : versions) {
                String currentVersion = version.getPlugin().getDescription().getVersion();

                if (currentVersion.equalsIgnoreCase(version.getNewVersion())) return;

                for (CommandSender sender : senders) {

                    if (sender instanceof Player) {
                        ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 10);
                    }

                    System.out.println("sending for " + sender);
                    Text.format(" ").send(sender);
                    Text.sendTextFromLang(sender, version.getPlugin(), "plugin-update-available", Pair.of("plugin", version.getPlugin().getName()), Pair.of("version", "<click:SUGGEST_COMMAND:" + version.getDownloadUrl() + ">§n" + version.getNewVersion() + "</click>")).join();
                    Text.format(" ").send(sender);
                }

            }
        });
    }

    @AllArgsConstructor
    @Getter
    public static class VersionModel {

        private final Plugin plugin;
        private final String newVersion;
        private final String downloadUrl;

    }


}
