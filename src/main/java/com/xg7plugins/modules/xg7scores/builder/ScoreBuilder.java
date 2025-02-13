package com.xg7plugins.modules.xg7scores.builder;

import com.xg7plugins.utils.Builder;
import org.bukkit.entity.Player;

import java.util.function.Function;

public abstract class ScoreBuilder<S,B extends ScoreBuilder<S,B>> implements Builder<S> {

    protected String id;
    protected long delayToUpdate;
    protected Function<Player, Boolean> condition;

    public ScoreBuilder(String id) {
        this.id = id;
        this.condition = player -> true;
    }

    public B delay(long delay) {
        delayToUpdate = delay;
        return (B) this;
    }
    public B condition(Function<Player, Boolean> condition) {
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
