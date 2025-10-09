package com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar.updaters;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.score.ScoreFormat;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar.Sidebar;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
public class NewerSidebarUpdater implements SidebarUpdater {

    private final Sidebar sidebar;

    @Override
    public boolean checkVersion(Player player) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        if (user == null) return true;

        ClientVersion clientVersion = user.getClientVersion();

        return clientVersion.isNewerThanOrEquals(ClientVersion.V_1_13) && MinecraftVersion.isNewerOrEqual(13);
    }

    @Override
    public void setLine(Player player, int score, String text) {
        WrapperPlayServerUpdateScore updateScore = new WrapperPlayServerUpdateScore(
                score + "_" + player.getUniqueId(),
                WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
                "sb-" + sidebar.getId(),
                score,
                Text.detectLangs(player, sidebar.getPlugin(), text).join().toAdventureComponent(),
                ScoreFormat.blankScore()
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, updateScore);
    }

    @Override
    public void removeLine(Player player, int score) {
        WrapperPlayServerUpdateScore updateScore = new WrapperPlayServerUpdateScore(
                score + "_" + player.getUniqueId(),
                WrapperPlayServerUpdateScore.Action.REMOVE_ITEM,
                "sb-" + sidebar.getId(),
                Optional.of(0)
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, updateScore);
    }


}
