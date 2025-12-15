package com.xg7plugins.modules.xg7scores.scores.scoreboards;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class BelowNameScore extends Score {

    private final String integerValuePlaceholder;

    public BelowNameScore(long delay, List<String> healthIndicator, String integerValuePlaceholder, String id, Function<Player, Boolean> condition, Plugin plugin) {
        super(delay, healthIndicator, id, condition, plugin);

        this.integerValuePlaceholder = integerValuePlaceholder;

    }

    @Override
    public void update() {

        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;

            try {
                double score = Parser.DOUBLE.convert(Text.format(integerValuePlaceholder).textFor(player).getPlainText());
                int intScore = (int) score;

                WrapperPlayServerScoreboardObjective updateObjective = new WrapperPlayServerScoreboardObjective(
                        "bn-" + getId(),
                        WrapperPlayServerScoreboardObjective.ObjectiveMode.UPDATE,
                        Text.detectLangs(player, plugin, super.updateText.get(indexUpdating)).textFor(player).toAdventureComponent(),
                        WrapperPlayServerScoreboardObjective.RenderType.INTEGER
                );
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, updateObjective);

                WrapperPlayServerUpdateScore updateScore = new WrapperPlayServerUpdateScore(
                        player.getName(),
                        WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
                        "bn-" + getId(),
                        Optional.of(intScore)
                );

                Bukkit.getOnlinePlayers().forEach(p -> PacketEvents.getAPI().getPlayerManager().sendPacket(p, updateScore));
            } catch (Exception ignored) {
                XG7Plugins.getInstance().getDebug().warn("scores", "Invalid score format for below name scoreboard");
            }

        }

    }

    @Override
    public synchronized void addPlayer(Player player) {
        if (super.getPlayers().contains(player.getUniqueId())) return;
        super.addPlayer(player);
        WrapperPlayServerScoreboardObjective objective = new WrapperPlayServerScoreboardObjective(
                "bn-" + getId(),
                WrapperPlayServerScoreboardObjective.ObjectiveMode.CREATE,
                Text.detectLangs(player, plugin, super.updateText.get(indexUpdating)).textFor(player).toAdventureComponent(),
                WrapperPlayServerScoreboardObjective.RenderType.INTEGER
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, objective);

        WrapperPlayServerDisplayScoreboard displayScoreboard = new WrapperPlayServerDisplayScoreboard(2, "bn-" + getId());

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, displayScoreboard);
    }

    @Override
    public synchronized void removePlayer(Player player) {
        if (player == null) return;
        super.removePlayer(player);
        WrapperPlayServerDisplayScoreboard hideScoreboard = new WrapperPlayServerDisplayScoreboard(0, "");
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, hideScoreboard);

        WrapperPlayServerScoreboardObjective removeObjective = new WrapperPlayServerScoreboardObjective(
                "bn-" + getId(),
                WrapperPlayServerScoreboardObjective.ObjectiveMode.REMOVE,
                Component.text(""),
                WrapperPlayServerScoreboardObjective.RenderType.INTEGER
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeObjective);

    }
}
