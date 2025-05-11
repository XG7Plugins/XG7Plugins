package com.xg7plugins.modules.xg7scores.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.XG7Scores;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicLong;

public class ScoreTask extends Task {

    private XG7Scores scores;

    public ScoreTask(XG7Scores scores) {
        super(
                XG7Plugins.getInstance(),
                "score-task",
                true,
                true,
                1,
                TaskState.IDLE,
                null
        );

        this.scores = scores;
    }

    @Override
    public void run() {
        AtomicLong counter = new AtomicLong();

        scores.getScores().values().forEach(score -> {
            scores.getPlayers().forEach(uuid -> {
                try {

                    Player p = Bukkit.getPlayer(uuid);

                    if (p == null) return;


                    if (score.getCondition().apply(p) && !p.isDead() && XG7Plugins.getInstance().isEnabled()) score.addPlayer(p);
                    else if (score.getPlayers().contains(p.getUniqueId())) {
                        if (!XG7Plugins.getInstance().isEnabled()) return;
                        Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> score.removePlayer(p));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            try {
                if (counter.get() % score.getDelay() == 0) {
                    score.update();
                    score.incrementIndex();
                }

                if (counter.get() == Long.MAX_VALUE) counter.set(0);
            } catch (Exception e) {
                e.printStackTrace();
            }


        });
        counter.incrementAndGet();
    }
}
