package com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar.updaters.LegacySidebarUpdater;
import com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar.updaters.NewerSidebarUpdater;
import com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar.updaters.SidebarUpdater;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Sidebar extends Score {

    protected final List<String> lines;

    private final List<SidebarUpdater> updaters = Arrays.asList(new LegacySidebarUpdater(this), new NewerSidebarUpdater(this));


    public Sidebar(List<String> title, List<String> lines, String id, Function<Player, Boolean> condition, long taskDelay, Plugin plugin) {
        super(taskDelay, title,id,condition,plugin);
        this.lines = lines.stream().map(l -> {
            if (l.isEmpty()) return " ";
            return l;
        }).collect(Collectors.toList());
    }

    private SidebarUpdater choseByPlayer(Player player) {
        return updaters.stream().filter(updater -> updater.checkVersion(player)).findFirst().orElse(null);
    }

    @Override
    public void update() {

        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);

            if (player == null) continue;

            WrapperPlayServerScoreboardObjective objective = new WrapperPlayServerScoreboardObjective(
                    "sb-" + getId(),
                    WrapperPlayServerScoreboardObjective.ObjectiveMode.UPDATE,
                    Text.detectLangs(player, plugin, super.updateText.get(indexUpdating)).textFor(player).getComponent(),
                    WrapperPlayServerScoreboardObjective.RenderType.INTEGER
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, objective);

            SidebarUpdater updater = choseByPlayer(player);

            for (int i = 0; i < lines.size(); i++) {
                String lineText = Text.format(lines.get(i)).textFor(player).getText();
                int score = lines.size() - i;

                if (lineText.isEmpty()) {
                    updater.removeLine(player, score);
                    continue;
                }
                updater.setLine(player, score, lineText);
            }
        }

    }

    @Override
    public synchronized void addPlayer(Player player) {
        if (super.getPlayers().contains(player.getUniqueId())) return;
        super.addPlayer(player);
        WrapperPlayServerScoreboardObjective objective = new WrapperPlayServerScoreboardObjective(
                "sb-" + getId(),
                WrapperPlayServerScoreboardObjective.ObjectiveMode.CREATE,
                Text.detectLangs(player, plugin, super.updateText.get(0)).textFor(player).getComponent(),
                WrapperPlayServerScoreboardObjective.RenderType.INTEGER
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, objective);

        WrapperPlayServerDisplayScoreboard displayScoreboard = new WrapperPlayServerDisplayScoreboard(1, "sb-" + getId());

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, displayScoreboard);
    }

    @Override
    public synchronized void removePlayer(Player player) {
        if (player == null) return;

        SidebarUpdater updater = choseByPlayer(player);

        updater.prepareToRemove(player);


        super.removePlayer(player);
        WrapperPlayServerDisplayScoreboard hideScoreboard = new WrapperPlayServerDisplayScoreboard(0, "");
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, hideScoreboard);

        WrapperPlayServerScoreboardObjective removeObjective = new WrapperPlayServerScoreboardObjective(
                "sb-" + getId(),
                WrapperPlayServerScoreboardObjective.ObjectiveMode.REMOVE,
                Component.text(""),
                WrapperPlayServerScoreboardObjective.RenderType.INTEGER
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, removeObjective);

    }

}
