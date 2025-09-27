package com.xg7plugins.modules.xg7scores.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.scores.scoreboards.TabListScore;

import java.util.List;

public class TabListScoreBuild extends ScoreBuilder<TabListScore, TabListScoreBuild> {
    String integerValuePlaceholder;

    public TabListScoreBuild(String id) {
        super(id);
    }

    public TabListScoreBuild integerValuePlaceholder(String integerValuePlaceholder) {
        this.integerValuePlaceholder = integerValuePlaceholder;
        return this;
    }

    @Override
    public TabListScore build(Plugin plugin) {
        return new TabListScore(delayToUpdate, integerValuePlaceholder, id, condition, plugin);
    }

}
