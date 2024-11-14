package com.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreCondition;

public abstract class GenericBossBar extends Score {

    public GenericBossBar(long delay, String[] toUpdate, String id, ScoreCondition condition, Plugin plugin) {
        super(delay, toUpdate, id, condition, plugin);
    }
}