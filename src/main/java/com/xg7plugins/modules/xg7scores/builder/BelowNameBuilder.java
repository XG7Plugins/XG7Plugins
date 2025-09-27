package com.xg7plugins.modules.xg7scores.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.scores.scoreboards.BelowNameScore;

import java.util.List;

public class BelowNameBuilder extends ScoreBuilder<BelowNameScore, BelowNameBuilder> {

    List<String> healthIndicator;
    String integerValuePlaceholder;

    public BelowNameBuilder(String id) {
        super(id);
    }

    public BelowNameBuilder healthIndicator(List<String> healthIndicator) {
        this.healthIndicator = healthIndicator;
        return this;
    }
    public BelowNameBuilder integerValuePlaceholder(String integerValuePlaceholder) {
        this.integerValuePlaceholder = integerValuePlaceholder;
        return this;
    }

    @Override
    public BelowNameScore build(Plugin plugin) {
        return new BelowNameScore(delayToUpdate, healthIndicator, integerValuePlaceholder, id, condition, plugin);
    }
}
