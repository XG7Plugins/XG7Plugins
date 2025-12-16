package com.xg7plugins.modules.xg7scores.organizer;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class TabListSorter {

    private final Map<String, TabListRule> rules = new HashMap<>();
    private final Map<String, WrapperPlayServerTeams> teams = new HashMap<>();
    private final Map<UUID, String> teamByPlayer = new HashMap<>();
    private final Set<String> globallyCreatedTeams = new HashSet<>();
    private final Set<UUID> updateList = new HashSet<>();

    public TabListSorter(List<TabListRule> rules) {
        rules.forEach(rule -> this.rules.put(rule.getId(), rule));
        rules.forEach(rule -> teams.put(rule.getId(), createTeam(rule)));
    }

    public void addRule(TabListRule rule) {
        rules.put(rule.getId(), rule);
        teams.put(rule.getId(), createTeam(rule));
    }

    public TabListRule getRule(String id) {
        return rules.get(id);
    }

    public TabListRule getRuleByPlayer(Player player) {
        return rules.get(teamByPlayer.get(player.getUniqueId()));
    }

    public TabListRule findRuleByPlayer(Player player) {
        return rules.values().stream()
                .sorted(Comparator.comparingInt(TabListRule::priority))
                .filter(rule -> rule.condition(player))
                .findFirst()
                .orElse(null);
    }

    public void addToUpdateList(UUID uuid) {
        updateList.add(uuid);
    }
    public void removeFromUpdateList(UUID uuid) {
        updateList.remove(uuid);
    }

    public void addPlayer(Player player) {
        TabListRule rule = findRuleByPlayer(player);
        if (rule == null) return;

        String teamId = rule.getId();

        if (!globallyCreatedTeams.contains(teamId)) createTeamGlobally(teamId);

        addPlayerToTeam(player, teamId);
        teamByPlayer.put(player.getUniqueId(), teamId);
    }

    public void updatePlayer(Player player) {
        if (!updateList.contains(player.getUniqueId())) return;
        TabListRule correctRule = findRuleByPlayer(player);
        if (correctRule == null) {
            removePlayer(player);
            return;
        }

        String correctTeamId = correctRule.getId();
        String currentTeamId = teamByPlayer.get(player.getUniqueId());

        if (Objects.equals(currentTeamId, correctTeamId)) return;

        if (currentTeamId != null) removePlayerFromTeam(player, currentTeamId);

        if (!globallyCreatedTeams.contains(correctTeamId)) createTeamGlobally(correctTeamId);

        addPlayerToTeam(player, correctTeamId);

        teamByPlayer.put(player.getUniqueId(), correctTeamId);

    }

    public void removePlayer(Player player) {
        String currentTeamId = teamByPlayer.get(player.getUniqueId());
        if (currentTeamId == null) return;

        removePlayerFromTeam(player, currentTeamId);
        teamByPlayer.remove(player.getUniqueId());
    }

    private void createTeamGlobally(String teamId) {
        WrapperPlayServerTeams team = teams.get(teamId);
        if (team == null) return;

        team.setTeamMode(WrapperPlayServerTeams.TeamMode.CREATE);
        team.setPlayers(Collections.emptyList());

        Bukkit.getOnlinePlayers().forEach(p ->
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, team));

        globallyCreatedTeams.add(teamId);
    }

    private void addPlayerToTeam(Player player, String teamId) {
        WrapperPlayServerTeams team = teams.get(teamId);
        if (team == null) return;

        team.setTeamMode(WrapperPlayServerTeams.TeamMode.ADD_ENTITIES);
        team.setPlayers(Collections.singletonList(player.getName()));

        Bukkit.getOnlinePlayers().forEach(p ->
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, team));
    }

    private void removePlayerFromTeam(Player player, String teamId) {
        WrapperPlayServerTeams team = teams.get(teamId);
        if (team == null) return;

        team.setTeamMode(WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES);
        team.setPlayers(Collections.singletonList(player.getName()));

        Bukkit.getOnlinePlayers().forEach(p ->
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, team));
    }

    private WrapperPlayServerTeams createTeam(TabListRule rule) {
        String teamName = String.format("%04d_%s", rule.priority(), rule.getId());

        return new WrapperPlayServerTeams(
                teamName,
                WrapperPlayServerTeams.TeamMode.CREATE,
                new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                        Component.text(teamName),
                        Component.text(""),
                        Component.text(""),
                        WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                        WrapperPlayServerTeams.CollisionRule.ALWAYS,
                        NamedTextColor.WHITE,
                        WrapperPlayServerTeams.OptionData.ALL
                )
        );
    }

    public void createAllTeamsForPlayer(Player player) {
        teams.values().forEach(team -> {
            team.setTeamMode(WrapperPlayServerTeams.TeamMode.CREATE);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, team);
        });
        teamByPlayer.forEach((key, value) -> {

            WrapperPlayServerTeams team = teams.get(value);

            if (team == null) return;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(key);

            team.setTeamMode(WrapperPlayServerTeams.TeamMode.ADD_ENTITIES);
            team.setPlayers(Collections.singletonList(Objects.requireNonNull(offlinePlayer.getName())));


            PacketEvents.getAPI().getPlayerManager().sendPacket(player, team);


        });
    }

    public void deleteAllTeamsForPlayer(Player player) {
        teams.values().forEach(team -> {
            team.setTeamMode(WrapperPlayServerTeams.TeamMode.REMOVE);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, team);
        });
    }
}