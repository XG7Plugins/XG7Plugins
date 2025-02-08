package com.xg7plugins.temp.xg7scores.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.temp.xg7scores.scores.XPBar;

import java.util.ArrayList;
import java.util.List;

public class XPBarBuilder extends ScoreBuilder<XPBar, XPBarBuilder> {

    private List<String> xp = new ArrayList<>();

    public XPBarBuilder(String id) {
        super(id);
    }

    public XPBarBuilder addXP(int level, float progress) {
        this.xp.add(level + ", " + progress);
        return this;
    }
    public XPBarBuilder addXP(String level) {
        this.xp.add(level);
        return this;
    }

    public XPBarBuilder setLevels(List<String> xp) {
        this.xp = xp;
        return this;
    }


    @Override
    public XPBar build(Object... args) {
        if (id == null || delayToUpdate == 0) throw new IllegalArgumentException("You must specify the id and the delay to update the score");

        return new XPBar(delayToUpdate, xp, id, condition, (Plugin) args[0]);
    }
}
