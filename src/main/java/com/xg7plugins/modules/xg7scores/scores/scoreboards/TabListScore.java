package com.xg7plugins.modules.xg7scores.scores.scoreboards;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public class TabListScore extends Score {

    private final String integerValuePlaceholder;

    public TabListScore(long delay, String integerValuePlaceholder, String id, Function<Player, Boolean> condition, Plugin plugin) {
        super(delay, Collections.emptyList(), id, condition, plugin);
        this.integerValuePlaceholder = integerValuePlaceholder;
    }

    @Override
    public void update() {
        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;

            int intScore = (int) Parser.DOUBLE.convert(Text.format(integerValuePlaceholder).textFor(player).getPlainText());

            PacketContainer updateScorePacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
            updateScorePacket.getStrings().write(0, player.getName());             // jogador
            updateScorePacket.getStrings().write(1, "tl-" + getId());              // objetivo
            updateScorePacket.getIntegers().write(0, intScore);                    // valor
            updateScorePacket.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE);

            // envia para todos online
            Bukkit.getOnlinePlayers().forEach(p -> {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(p, updateScorePacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public synchronized void addPlayer(Player player) {
        if (super.getPlayers().contains(player.getUniqueId())) return;
        super.addPlayer(player);

        sendCreateObjective(player);
        sendDisplayObjective(player);
        sendInitialScore(player);
    }

    @Override
    public synchronized void removePlayer(Player player) {
        if (player == null) return;
        super.removePlayer(player);

        sendHideDisplay(player);
        sendRemoveObjective(player);
    }

    /* ------------------- Funções auxiliares ------------------- */

    private void sendCreateObjective(Player player) {
        PacketContainer objectivePacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        objectivePacket.getStrings().write(0, "tl-" + getId());
        objectivePacket.getChatComponents().write(0, WrappedChatComponent.fromLegacyText(player.getUniqueId() + "_" + getId()));
        objectivePacket.getIntegers().write(0, 0); // CREATE = 0
        objectivePacket.getStrings().write(1, "INTEGER");

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, objectivePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendDisplayObjective(Player player) {
        PacketContainer displayPacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        displayPacket.getIntegers().write(0, 0); // slot 0 (tablist)
        displayPacket.getStrings().write(0, "tl-" + getId());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, displayPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendInitialScore(Player player) {
        int intScore = (int) Parser.DOUBLE.convert(Text.format(integerValuePlaceholder).textFor(player).getPlainText());

        PacketContainer updateScorePacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        updateScorePacket.getStrings().write(0, player.getName());
        updateScorePacket.getStrings().write(1, "tl-" + getId());
        updateScorePacket.getIntegers().write(0, intScore);
        updateScorePacket.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE);

        Bukkit.getOnlinePlayers().forEach(p -> {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, updateScorePacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void sendHideDisplay(Player player) {
        PacketContainer hidePacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        hidePacket.getIntegers().write(0, 0); // slot
        hidePacket.getStrings().write(0, "");  // vazio = esconde

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, hidePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRemoveObjective(Player player) {
        PacketContainer removeObjective = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        removeObjective.getStrings().write(0, "tl-" + getId());
        removeObjective.getChatComponents().write(0, WrappedChatComponent.fromLegacyText(""));
        removeObjective.getIntegers().write(0, 1); // REMOVE = 1
        removeObjective.getStrings().write(1, "INTEGER");

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, removeObjective);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}