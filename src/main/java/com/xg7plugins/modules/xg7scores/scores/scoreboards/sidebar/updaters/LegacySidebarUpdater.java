package com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar.updaters;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar.Sidebar;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@AllArgsConstructor
public class LegacySidebarUpdater implements SidebarUpdater {

    private final Map<UUID, Map<Integer, Pair<String, String>>> playerLastLines = new HashMap<>();
    private final Map<UUID, List<String>> playerUsedEntries = new HashMap<>();

    private final Sidebar sidebar;

    @Override
    public boolean checkVersion(Player player) {

        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        if (user == null) return MinecraftServerVersion.isOlderThan(ServerVersion.V_1_13);

        ClientVersion clientVersion = user.getClientVersion();

        return clientVersion.isOlderThan(ClientVersion.V_1_13) || MinecraftServerVersion.isOlderThan(ServerVersion.V_1_13);
    }

    @Override
    public void setLine(Player player, int score, String text) {
        UUID uuid = player.getUniqueId();

        String translatedText = Text.detectLangs(player, sidebar.getPlugin(), text).getText();

        Map<Integer, Pair<String, String>> lastLines = playerLastLines.computeIfAbsent(uuid, k -> new HashMap<>());
        List<String> usedEntries = playerUsedEntries.computeIfAbsent(uuid, k -> new ArrayList<>());

        if (lastLines.containsKey(score) && lastLines.get(score).getFirst().equals(translatedText)) {
            return;
        }

        String teamName = "team_" + score + "_" + uuid;

        if (lastLines.containsKey(score)) {
            String oldEntry = lastLines.get(score).getSecond();

            WrapperPlayServerTeams removeTeam = new WrapperPlayServerTeams(
                    teamName,
                    WrapperPlayServerTeams.TeamMode.REMOVE,
                    Optional.empty(),
                    Collections.emptyList()
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeTeam);

            WrapperPlayServerUpdateScore removeOldScore = new WrapperPlayServerUpdateScore(
                    oldEntry,
                    WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
                    "sb-" + sidebar.getId(),
                    Optional.of(score)
            );
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeOldScore);
            usedEntries.remove(oldEntry);
        }

        String prefix = translatedText.substring(0, Math.min(translatedText.length(), 16));
        String entry = translatedText.length() > 16 ? translatedText.substring(16, Math.min(translatedText.length(), 56)) : "";

        String entryColors = ChatColor.getLastColors(prefix);
        String finalEntry = entryColors + entry;
        int originalEntryLength = finalEntry.length();

        while (usedEntries.contains(finalEntry)) {
            finalEntry += "§r";
        }
        usedEntries.add(finalEntry);

        int addedChars = finalEntry.length() - originalEntryLength;

        int suffixStart = 56 + addedChars;
        String suffix = translatedText.length() > suffixStart ? ChatColor.getLastColors(finalEntry) + translatedText.substring(suffixStart, Math.min(translatedText.length(), 72 + addedChars)) : "";

        WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.text(teamName),
                Text.format(prefix).getComponent(),
                Text.format(suffix).getComponent(),
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
                "sb-" + sidebar.getId(),
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

            WrapperPlayServerTeams removeTeam = new WrapperPlayServerTeams(
                    "team_" + score + "_" + uuid,
                    WrapperPlayServerTeams.TeamMode.REMOVE,
                    Optional.empty(),
                    Collections.emptyList()
            );
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeTeam);

            lastLines.remove(score);

            WrapperPlayServerUpdateScore removeScore = new WrapperPlayServerUpdateScore(
                    entry,
                    WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
                    "sb-" + sidebar.getId(),
                    Optional.of(score)
            );
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeScore);

            List<String> usedEntries = playerUsedEntries.get(uuid);
            if (usedEntries != null) {
                usedEntries.remove(entry);
            }
        }
    }

    @Override
    public synchronized void prepareToRemove(Player player) {
        if (player == null) return;

        UUID uuid = player.getUniqueId();

        Map<Integer, Pair<String, String>> lastLines = playerLastLines.get(uuid);
        if (lastLines != null) {
            for (int score : lastLines.keySet()) {
                WrapperPlayServerTeams removeTeam = new WrapperPlayServerTeams(
                        "team_" + score + "_" + uuid,
                        WrapperPlayServerTeams.TeamMode.REMOVE,
                        Optional.empty(),
                        Collections.emptyList()
                );
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeTeam);
            }
        }

        playerLastLines.remove(uuid);
        playerUsedEntries.remove(uuid);
    }
}