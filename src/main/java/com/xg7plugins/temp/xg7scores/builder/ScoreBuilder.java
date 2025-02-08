package com.xg7plugins.temp.xg7scores.builder;

import com.xg7plugins.temp.xg7scores.ScoreCondition;
import com.xg7plugins.utils.Builder;

public abstract class ScoreBuilder<S,B> implements Builder<S> {

    protected String id;
    protected long delayToUpdate;
    protected ScoreCondition condition;

    public ScoreBuilder(String id) {
        this.id = id;
        this.condition = player -> true;
    }

    public B delay(long delay) {
        delayToUpdate = delay;
        return (B) this;
    }
    public B condition(ScoreCondition condition) {
        this.condition = condition;
        return (B) this;
    }

    public static ScoreBoardBuilder scoreBoard(String id) {
        return new ScoreBoardBuilder(id);
    }
    public static TablistBuilder tablist(String id) {
        return new TablistBuilder(id);
    }
    public static XPBarBuilder XPBar(String id) {
        return new XPBarBuilder(id);
    }
    public static BossBarBuilder bossBar(String id) {
        return new BossBarBuilder(id);
    }
    public static ActionBarBuilder actionBar(String id) {
        return new ActionBarBuilder(id);
    }






}
