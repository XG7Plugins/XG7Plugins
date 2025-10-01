package com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public class LegacySidebar extends GenericSidebar {

    private final Map<UUID, Map<Integer, Pair<String, String>>> playerLastLines = new HashMap<>();
    private final Map<UUID, List<String>> playerUsedEntries = new HashMap<>();

    public LegacySidebar(List<String> title, List<String> lines, String id, Function<Player, Boolean> condition, long taskDelay, Plugin plugin) {
        super(title, lines, id, condition, taskDelay, plugin);
    }

    @Override
    public void setLine(Player player, int score, String text) {
        UUID uuid = player.getUniqueId();

        String translatedText = Text.detectLangs(player, plugin, text).join().getText();

        Map<Integer, Pair<String, String>> lastLines = playerLastLines.computeIfAbsent(uuid, k -> new HashMap<>());
        List<String> usedEntries = playerUsedEntries.computeIfAbsent(uuid, k -> new ArrayList<>());

        if (lastLines.containsKey(score) && lastLines.get(score).getFirst().equals(translatedText)) {
            return;
        }

        if (lastLines.containsKey(score)) {
            String oldEntry = lastLines.get(score).getSecond();

            WrapperPlayServerUpdateScore removeOldScore = new WrapperPlayServerUpdateScore(
                    oldEntry,
                    WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
                    "sb-" + getId(),
                    Optional.of(score)
            );
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeOldScore);

            WrapperPlayServerTeams removeTeam = new WrapperPlayServerTeams(
                    "team_" + score,
                    WrapperPlayServerTeams.TeamMode.REMOVE,
                    Optional.empty(),
                    Collections.emptyList()
            );
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeTeam);

            usedEntries.remove(oldEntry);
        }

        String prefix = translatedText.substring(0, Math.min(translatedText.length(), 16));
        String entry = translatedText.length() > 16 ? translatedText.substring(16, Math.min(translatedText.length(), 56)) : "";
        String suffix = translatedText.length() > 56 ? translatedText.substring(56, Math.min(translatedText.length(), MinecraftVersion.isNewerThan(12) ? translatedText.length() : 72)) : "";

        if (MinecraftVersion.isNewerOrEqual(13)) {
            suffix = ChatColor.getLastColors(prefix) + entry + suffix;
            entry = "";
        }

        String finalEntry = entry;
        while (usedEntries.contains(finalEntry)) {
            finalEntry += "§r" + ChatColor.getLastColors(prefix);
        }
        usedEntries.add(finalEntry);

        String teamName = "team_" + score;
        WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.text(teamName),
                Text.format(prefix).toAdventureComponent(),
                Text.format(suffix).toAdventureComponent(),
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.ALWAYS,
                NamedTextColor.WHITE,
                WrapperPlayServerTeams.OptionData.NONE
        );

        WrapperPlayServerTeams createTeam = new WrapperPlayServerTeams(
                teamName,
                WrapperPlayServerTeams.TeamMode.CREATE,
                Optional.of(teamInfo),
                Collections.singletonList(finalEntry)
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, createTeam);

        WrapperPlayServerUpdateScore updateScore = new WrapperPlayServerUpdateScore(
                finalEntry,
                WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
                "sb-" + getId(),
                Optional.of(score)
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, updateScore);

        lastLines.put(score, Pair.of(translatedText, finalEntry));
    }

    @Override
    public void removeLine(Player player, int score) {
        UUID uuid = player.getUniqueId();
        Map<Integer, Pair<String, String>> lastLines = playerLastLines.get(uuid);

        if (lastLines != null && lastLines.containsKey(score)) {
            String entry = lastLines.get(score).getSecond();

            WrapperPlayServerUpdateScore removeScore = new WrapperPlayServerUpdateScore(
                    entry,
                    WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
                    "sb-" + getId(),
                    Optional.of(score)
            );
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeScore);

            WrapperPlayServerTeams removeTeam = new WrapperPlayServerTeams(
                    "team_" + score,
                    WrapperPlayServerTeams.TeamMode.REMOVE,
                    Optional.empty(),
                    Collections.emptyList()
            );
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeTeam);

            lastLines.remove(score);
            List<String> usedEntries = playerUsedEntries.get(uuid);
            if (usedEntries != null) {
                usedEntries.remove(entry);
            }
        }
    }

    @Override
    public synchronized void removePlayer(Player player) {
        if (player == null) return;

        UUID uuid = player.getUniqueId();

        Map<Integer, Pair<String, String>> lastLines = playerLastLines.get(uuid);
        if (lastLines != null) {
            for (int score : lastLines.keySet()) {
                WrapperPlayServerTeams removeTeam = new WrapperPlayServerTeams(
                        "team_" + score,
                        WrapperPlayServerTeams.TeamMode.REMOVE,
                        Optional.empty(),
                        Collections.emptyList()
                );
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeTeam);
            }
        }

        playerLastLines.remove(uuid);
        playerUsedEntries.remove(uuid);

        super.removePlayer(player);
    }
}