package com.xg7plugins.libs.xg7scores.builder;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.scores.bossbar.BossBar;
import com.xg7plugins.libs.xg7scores.scores.bossbar.LegacyBossBar;
import com.xg7plugins.libs.xg7scores.scores.bossbar.PublicBossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.ArrayList;
import java.util.List;

public class BossBarBuilder extends ScoreBuilder<Score, BossBarBuilder> {

    private Object color;
    private Object style;
    private float progress;
    private boolean isPublic = false;
    private List<String> title = new ArrayList<>();

    public BossBarBuilder(String id) {
        super(id);
    }
    public BossBarBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }
    public BossBarBuilder color(BarColor color) {
        this.color = color;
        return this;
    }

    public BossBarBuilder style(BarStyle style) {
        this.style = style;
        return this;
    }

    public BossBarBuilder progress(float progress) {
        this.progress = progress / 100f;
        return this;
    }

    public BossBarBuilder title(List<String> title) {
        this.title = title;
        return this;
    }
    public BossBarBuilder addTitleUpdate(String title) {
        this.title.add(title);
        return this;
    }

    @Override
    public Score build(Object... args) {

        Plugin plugin = (Plugin) args[0];

        if (id == null || delayToUpdate == 0) throw new IllegalArgumentException("You must specify the id and the delay to update the score");

        if (progress == 0) throw new IllegalArgumentException("You must specify the progress of the boss bar");

        if (XG7Plugins.getMinecraftVersion() <= 8) {
            return new LegacyBossBar(delayToUpdate, title,id,condition,progress,plugin);
        }

        if (isPublic) return new PublicBossBar(delayToUpdate, title,id,condition, (BarColor) color, (BarStyle) style,progress,plugin);

        return new BossBar(delayToUpdate,id,condition, title, (BarColor) color, (BarStyle) style,progress,plugin);
    }
}
