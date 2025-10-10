package com.xg7plugins.modules.xg7scores.organizer;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class TabListSorter {

    private final Map<String, TabListRule> rules = new HashMap<>();
    private final Map<String, String> teamByPlayer = new HashMap<>();
    private final Set<String> globallyCreatedTeams = new HashSet<>();
    private final Set<UUID> updateList = new HashSet<>();

    public TabListSorter(List<TabListRule> rules) {
        rules.forEach(rule -> this.rules.put(rule.getId(), rule));
    }

    public void addRule(TabListRule rule) {
        rules.put(rule.getId(), rule);
    }

    public TabListRule findRuleByPlayer(Player player) {
        return rules.values().stream()
                .sorted(Comparator.comparingInt(TabListRule::priority))
                .filter(rule -> rule.condition(player))
                .findFirst()
                .orElse(null);
    }

    public void addToUpdateList(UUID uuid) { updateList.add(uuid); }
    public void removeFromUpdateList(UUID uuid) { updateList.remove(uuid); }

    public void addPlayer(Player player) {
        TabListRule rule = findRuleByPlayer(player);
        if (rule == null) return;
        String teamId = rule.getId();

        if (!globallyCreatedTeams.contains(teamId)) createTeamGlobally(player, teamId);

        addPlayerToTeam(player, teamId);
        teamByPlayer.put(player.getUniqueId().toString(), teamId);
    }

    public void updatePlayer(Player player) {
        if (!updateList.contains(player.getUniqueId())) return;
        TabListRule correctRule = findRuleByPlayer(player);
        if (correctRule == null) {
            removePlayer(player);
            return;
        }

        String correctTeamId = correctRule.getId();
        String currentTeamId = teamByPlayer.get(player.getUniqueId().toString());
        if (Objects.equals(currentTeamId, correctTeamId)) return;

        if (currentTeamId != null) removePlayerFromTeam(player, currentTeamId);
        if (!globallyCreatedTeams.contains(correctTeamId)) createTeamGlobally(player, correctTeamId);

        addPlayerToTeam(player, correctTeamId);
        teamByPlayer.put(player.getUniqueId().toString(), correctTeamId);
    }

    public void removePlayer(Player player) {
        String currentTeamId = teamByPlayer.get(player.getUniqueId().toString());
        if (currentTeamId == null) return;
        removePlayerFromTeam(player, currentTeamId);
        teamByPlayer.remove(player.getUniqueId().toString());
    }

    private void createTeamGlobally(Player player, String teamId) {
        globallyCreatedTeams.add(teamId);
        PacketContainer packet = createTeamPacket(teamId, true, Collections.emptyList());
        Bukkit.getOnlinePlayers().forEach(p -> sendPacket(p, packet));
    }

    private void addPlayerToTeam(Player player, String teamId) {
        PacketContainer packet = createTeamPacket(teamId, false, Collections.singletonList(player.getName()));
        Bukkit.getOnlinePlayers().forEach(p -> sendPacket(p, packet));
    }

    private void removePlayerFromTeam(Player player, String teamId) {
        PacketContainer packet = createRemovePlayersPacket(teamId, Collections.singletonList(player.getName()));
        Bukkit.getOnlinePlayers().forEach(p -> sendPacket(p, packet));
    }

    private PacketContainer createTeamPacket(String teamName, boolean create, List<String> players) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_TEAM
        );

        packet.getStrings().write(0, teamName); // team name
        packet.getIntegers().write(0, create ? 0 : 2); // 0 = create, 2 = add entities
        packet.getSpecificModifier(List.class).write(0, players); // players list
        packet.getChatComponents().write(0, WrappedChatComponent.fromLegacyText(teamName)); // display name
        packet.getChatComponents().write(1, WrappedChatComponent.fromLegacyText("")); // prefix
        packet.getChatComponents().write(2, WrappedChatComponent.fromLegacyText("")); // suffix
        return packet;
    }

    private PacketContainer createRemovePlayersPacket(String teamName, List<String> players) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_TEAM
        );
        packet.getStrings().write(0, teamName);
        packet.getIntegers().write(0, 4); // 4 = remove players
        packet.getSpecificModifier(List.class).write(0, players);
        return packet;
    }

    private void sendPacket(Player player, PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception ignored) {}
    }

    public void createAllTeamsForPlayer(Player player) {
        // Cria todas as teams existentes para esse player
        for (String teamId : globallyCreatedTeams) {
            PacketContainer packet = createTeamPacket(teamId, true, Collections.emptyList());
            sendPacket(player, packet);
        }

        // Adiciona os players corretos em cada team
        teamByPlayer.forEach((uuidStr, teamId) -> {
            PacketContainer packet = createTeamPacket(teamId, false, Collections.singletonList(Bukkit.getOfflinePlayer(UUID.fromString(uuidStr)).getName()));
            sendPacket(player, packet);
        });
    }


}
