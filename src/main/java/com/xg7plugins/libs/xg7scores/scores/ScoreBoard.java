package com.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class ScoreBoard extends Score {

    private String[] lines;

    private Scoreboard scoreboard;

    public ScoreBoard(String title, String[] lines, String id, ScoreCondition condition, long delay, Plugin plugin) {
        super(delay, new String[]{title},id, condition, plugin);
        this.lines = lines;

        Bukkit.getScheduler().runTask(plugin, () -> {

                    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

                    Objective objective = scoreboard.registerNewObjective(id, "dummy");
                    objective.setDisplayName(title);
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                    int index = lines.length + 1;

                    for (String s : lines) {
                        index--;

                        String entry = IntStream.range(0, index).mapToObj(i -> "§r").collect(Collectors.joining());

                        Team team = scoreboard.registerNewTeam(id + ":Team=" + index);

                        s = Text.format(s, plugin).getText();

                        String prefix = s.substring(0, Math.min(s.length(), 16));
                        String suffix = null;
                        if (s.length() > 16) {
                            suffix = XG7Plugins.getMinecraftVersion() > 12 ? s.substring(16) : s.substring(16, Math.min(s.length(), 32));
                            suffix = ChatColor.getLastColors(prefix) + suffix;
                            if (suffix.length() > 16) suffix = s.substring(0, 16);
                        }

                        team.setPrefix(prefix);
                        if (suffix != null) team.setSuffix(suffix);

                        team.addEntry(entry);

                        objective.getScore(entry).setScore(index);

                    }
                });

        XG7Plugins.getInstance().getScoreManager().registerScore(this);

    }

    public ScoreBoard(String[] title, String[] lines, String id, ScoreCondition condition, long taskDelay,Plugin plugin) {
        super(taskDelay, title,id,condition,plugin);
        this.lines = lines;

        Bukkit.getScheduler().runTask(plugin, () -> {

            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

            Objective objective = scoreboard.registerNewObjective(id, "dummy");
            objective.setDisplayName(title[0]);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            int index = lines.length + 1;

            for (String s : lines) {
                index--;

                String entry = IntStream.range(0, index).mapToObj(i -> "§r").collect(Collectors.joining());

                Team team = scoreboard.registerNewTeam(id + ":Team=" + index);

                s = Text.format(s, plugin).getText();

                String prefix = s.substring(0, Math.min(s.length(), 16));
                String suffix = null;
                if (s.length() > 16) {
                    suffix = XG7Plugins.getMinecraftVersion() > 12 ? s.substring(16) : s.substring(16, Math.min(s.length(), 32));
                    suffix = ChatColor.getLastColors(prefix) + suffix;
                    if (suffix.length() > 16) suffix = s.substring(0, 16);
                }

                team.setPrefix(prefix);
                if (suffix != null) team.setSuffix(suffix);

                team.addEntry(entry);


                objective.getScore(entry).setScore(index);

            }

            XG7Plugins.getInstance().getScoreManager().registerScore(this);
        });

    }

    @Override
    public void update() {


        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;

            Objective objective = scoreboard.getObjective(super.getId());

            objective.setDisplayName(Text.format(super.getToUpdate()[super.getIndexUpdating()],plugin).getWithPlaceholders(player));

            int index = lines.length + 1;

            for (String s : lines) {

                index--;

                String entry = IntStream.range(0, index).mapToObj(i -> "§r").collect(Collectors.joining());

                Team team = scoreboard.getTeam(super.getId() + ":Team=" + index);

                s = Text.format(s,plugin).getWithPlaceholders(player);

                String prefix = s.substring(0, Math.min(s.length(), 16));
                String suffix = null;
                if (s.length() > 16) {
                    suffix = XG7Plugins.getMinecraftVersion() > 12 ? s.substring(16) : s.substring(16, Math.min(s.length(), 32));
                    suffix = ChatColor.getLastColors(prefix) + suffix;
                    if (suffix.length() > 16) suffix = s.substring(0,16);
                }

                team.setPrefix(prefix);
                if (suffix != null) team.setSuffix(suffix);
                else team.setSuffix("");
            }

            player.setScoreboard(scoreboard);

        }

    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
