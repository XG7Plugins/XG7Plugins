package com.xg7plugins.modules.xg7scores.scores.scoreboards;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
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

            double score = Parser.DOUBLE.convert(Text.format(integerValuePlaceholder).textFor(player).getPlainText());
            int intScore = (int) score;

            WrapperPlayServerUpdateScore updateScore = new WrapperPlayServerUpdateScore(
                    player.getName(),
                    WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
                    "tl-" + getId(),
                    Optional.of(intScore)
            );
            Bukkit.getOnlinePlayers().forEach(p -> PacketEvents.getAPI().getPlayerManager().sendPacket(p, updateScore));
        }

    }

    @Override
    public synchronized void addPlayer(Player player) {
        if (super.getPlayers().contains(player.getUniqueId())) return;
        super.addPlayer(player);
        WrapperPlayServerScoreboardObjective objective = new WrapperPlayServerScoreboardObjective(
                "tl-" + getId(),
                WrapperPlayServerScoreboardObjective.ObjectiveMode.CREATE,
                Component.text(player.getUniqueId() + "_" + getId()),
                WrapperPlayServerScoreboardObjective.RenderType.INTEGER
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, objective);

        WrapperPlayServerDisplayScoreboard displayScoreboard = new WrapperPlayServerDisplayScoreboard(0, "tl-" + getId());

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, displayScoreboard);
    }

    @Override
    public synchronized void removePlayer(Player player) {
        if (player == null) return;
        super.removePlayer(player);
        WrapperPlayServerDisplayScoreboard hideScoreboard = new WrapperPlayServerDisplayScoreboard(0, "");
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, hideScoreboard);

        WrapperPlayServerScoreboardObjective removeObjective = new WrapperPlayServerScoreboardObjective(
                "tl-" + getId(),
                WrapperPlayServerScoreboardObjective.ObjectiveMode.REMOVE,
                Component.text(""),
                WrapperPlayServerScoreboardObjective.RenderType.INTEGER
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeObjective);

    }
}