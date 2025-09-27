package com.xg7plugins.utils;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.tasks.TaskState;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XG7PluginsPlaceholderExpansion extends PlaceholderExpansion {

    private static final Pattern pattern = Pattern.compile("format\\((.*?)\\)");

    @Override
    public @NotNull String getIdentifier() {
        return "xg7plugins";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DaviXG7";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {

        if (player == null) return null;

        switch (identifier) {
            case "tasks_running":
                return XG7PluginsAPI.taskManager().getTimerTaskMap().values().stream().filter(task -> task.getTaskState() == TaskState.RUNNING).count() + "";
            case "tasks_idle":
                return XG7PluginsAPI.taskManager().getTimerTaskMap().values().stream().filter(task -> task.getTaskState() == TaskState.IDLE).count() + "";
            case "tasks_total":
                    return XG7PluginsAPI.taskManager().getTimerTaskMap().values().size() + "";
            case "is_bedrock_player":
                return (XG7PluginsAPI.dependencyManager().exists("floodgate") && FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) + "";
            case "geyser_forms_enabled":
                return XG7PluginsAPI.isGeyserFormsEnabled() + "";
        }

        if (identifier.startsWith("player_")) {
            PlayerData playerData = XG7PluginsAPI.getPlayerData(player.getUniqueId());

            if (playerData == null) return null;

            if (identifier.equals("player_lang")) return playerData.getLangId();

            if (identifier.startsWith("player_firstjoin_")) {
                //player_firstjoin_format(dd/MM/yyyy HH:mm:ss)

                String format = identifier.split("_")[2];

                Matcher matcher = pattern.matcher(format);

                if (!matcher.find()) return null;

                return playerData.getFirstJoin().formatDate(matcher.group(1));
            }
        }

        return null;
    }
}
