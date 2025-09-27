package com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.score.ScoreFormat;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Sidebar extends GenericSidebar {
    public Sidebar(List<String> title, List<String> lines, String id, Function<Player, Boolean> condition, long taskDelay, Plugin plugin) {
        super(title, lines, id, condition, taskDelay, plugin);
    }

    @Override
    public void setLine(Player player, int score, String text) {
        WrapperPlayServerUpdateScore updateScore = new WrapperPlayServerUpdateScore(
                score + "_" + player.getUniqueId(),
                WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
                "sb-" + getId(),
                score,
                Text.detectLangs(player, plugin, text).join().toAdventureComponent(),
                ScoreFormat.blankScore()
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, updateScore);
    }

    @Override
    public void removeLine(Player player, int score) {
        WrapperPlayServerUpdateScore updateScore = new WrapperPlayServerUpdateScore(
                score + "_" + player.getUniqueId(),
                WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
                "sb-" + getId(),
                Optional.of(0)
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, updateScore);
    }


}
