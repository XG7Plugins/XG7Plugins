package com.xg7plugins.libs.xg7scores.builder;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreCondition;

public abstract class ScoreBuilder<B extends ScoreBuilder<B>> {

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

    public abstract <S extends Score> S build(Plugin plugin);







}
