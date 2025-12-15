package com.xg7plugins.loader;

import com.google.gson.JsonObject;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class VersionChecker {


    public CompletableFuture<List<VersionModel>> getVersions(Set<Plugin> pluginsToCheck) {
        return CompletableFuture.supplyAsync(() -> {

            List<VersionModel> models = new ArrayList<>();

            try {
                HTTPResponse response = HTTP.get("https://raw.githubusercontent.com/XG7Plugins/Versions/refs/heads/main/versions.json");

                if (response.getStatusCode() != 200) return Collections.emptyList();

                pluginsToCheck.forEach(plugin -> {
                    JsonObject object = response.getJson().getAsJsonObject(plugin.getName());
                    if (object == null) return;

                    models.add(new VersionModel(XG7Plugins.getAPI().getXG7Plugin(plugin.getName()), object.get("version").getAsString(), object.get("download_url").getAsString()));
                });

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return models;

        });
    }

    public void notify(List<CommandSender> senders, Set<Plugin> pluginsToCheck) {
        if (ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().get("disable-version-check")) return;
        XG7Plugins.getInstance().getDebug().info("http-requests", "Checking for plugin updates...");
        getVersions(pluginsToCheck).thenAccept(versions -> {

            for (VersionModel version : versions) {
                String currentVersion = version.getPlugin().getVersion();

                if (currentVersion.equalsIgnoreCase(version.getNewVersion())) return;

                for (CommandSender sender : senders) {

                    if (sender instanceof Player) {
                        ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 10);
                    }

                    Text.format(" ").send(sender);
                    Text.sendTextFromLang(sender, XG7Plugins.getInstance(), "plugin-update-available", Pair.of("plugin", version.getPlugin().getName()), Pair.of("version", "<hover:SHOW_TEXT:Download><click:RUN_COMMAND:/xg7plugins update>§n" + version.getNewVersion() + "</click></hover>"));
                    Text.format(" ").send(sender);
                }

            }
        });
    }

    public CompletableFuture<Pair<UpdateSate, String>> updatePlugin(Plugin plugin) {
        return getVersions(Collections.singleton(plugin)).thenApply(versions -> {

            for (VersionModel version : versions) {

                String currentVersion = version.getPlugin().getVersion();

                // Sem update
                if (currentVersion.equalsIgnoreCase(version.getNewVersion()))
                    return Pair.of(UpdateSate.NO_UPDATE, currentVersion);

                try {
                    File currentFile = new File(plugin.getJavaPlugin().getClass()
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI());
                    File pluginsFolder = currentFile.getParentFile();

                    System.out.println(plugin.getClass()
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI());

                    String newName = plugin.getName() + "-" + version.getNewVersion() + ".jar";
                    File newFile = new File(pluginsFolder, newName);

                    File tempFile = new File(pluginsFolder, newName + ".tmp");

                    // Download
                    try (InputStream in = HTTP.get(version.downloadUrl).getInputStream();
                         FileOutputStream out = new FileOutputStream(tempFile)) {

                        byte[] buffer = new byte[8192];
                        int bytesRead;

                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }

                    Files.move(
                            tempFile.toPath(),
                            newFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE
                    );

                    return Pair.of(UpdateSate.SUCCESS, version.getNewVersion());

                } catch (Exception e) {
                    XG7Plugins.getInstance().getDebug().severe("Failed to update plugin " + plugin.getName() + " to version " + version.getNewVersion());
                    e.printStackTrace();
                    return Pair.of(UpdateSate.ERROR, currentVersion);
                }
            }

            return Pair.of(UpdateSate.NO_UPDATE, "...");
        });
    }



    @AllArgsConstructor
    @Getter
    public static class VersionModel {

        private final Plugin plugin;
        private final String newVersion;
        private final String downloadUrl;

    }

    public enum UpdateSate {
        ERROR,
        NO_UPDATE,
        SUCCESS
    }


}
