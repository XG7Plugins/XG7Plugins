package com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public class LegacySidebar extends GenericSidebar {

    private final Map<UUID, Map<Integer, String>> playerLineNames = new HashMap<>();
    private final Map<UUID, List<String>> lastLines = new HashMap<>();

    public LegacySidebar(List<String> title, List<String> lines, String id, Function<Player, Boolean> condition, long taskDelay, Plugin plugin) {
        super(title, lines, id, condition, taskDelay, plugin);
    }

    public void setLine(Player player, int score, String text) {

        removeLine(player, score);

        String line = text;

        while (lastLines.containsKey(player.getUniqueId()) && lastLines.get(player.getUniqueId()).contains(line)) {
            line = ChatColor.RESET + line;
        }

        line = Text.detectLangs(player, plugin, line).join().getText();

        WrapperPlayServerUpdateScore updateScore = new WrapperPlayServerUpdateScore(
                line,
                WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
                "sb-" + getId(),
                Optional.of(score)
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, updateScore);

        lastLines.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(line);

        playerLineNames.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(score, line);

    }

    public void removeLine(Player player, int score) {

        Map<Integer, String> lines = playerLineNames.get(player.getUniqueId());
        if (lines != null && lines.containsKey(score)) {
            String lineName = lines.get(score);

            WrapperPlayServerUpdateScore updateScore = new WrapperPlayServerUpdateScore(
                    lineName,
                    WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
                    "sb-" + getId(),
                    Optional.of(score)
            );
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, updateScore);

            lines.remove(score);
            lastLines.get(player.getUniqueId()).remove(lineName);
        }
    }

}