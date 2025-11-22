package com.xg7plugins.modules.xg7scores.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7scores.XG7Scores;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import org.bukkit.Bukkit;

public class TabListSorterTask extends TimerTask {

    private final XG7Scores scores;

    public TabListSorterTask(XG7Scores scores) {
        super(
                XG7Plugins.getInstance(),
                "tablist-sorter",
                20L,
                1000L,
                TaskState.RUNNING,
                null
        );

        this.scores = scores;
    }

    @Override
    public void run() {

        if (scores == null || scores.getOrganizer() == null) return;

        Bukkit.getOnlinePlayers().forEach(player -> scores.getOrganizer().updatePlayer(player));
    }
}