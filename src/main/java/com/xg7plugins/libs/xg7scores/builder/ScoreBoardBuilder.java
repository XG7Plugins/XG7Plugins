package com.xg7plugins.libs.xg7scores.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7scores.scores.ScoreBoard;

import java.util.ArrayList;
import java.util.List;

public class ScoreBoardBuilder extends ScoreBuilder<ScoreBoardBuilder> {

    private List<String> title = new ArrayList<>();
    private List<String> lines = new ArrayList<>();

    public ScoreBoardBuilder(String id) {
        super(id);
    }

    public ScoreBoardBuilder title(List<String> title) {
        this.title = title;
        return this;
    }
    public ScoreBoardBuilder addTitleUpdate(String title) {
        this.title.add(title);
        return this;
    }
    public ScoreBoardBuilder lines(List<String> lines) {
        this.lines = lines;
        return this;
    }
    public ScoreBoardBuilder addLine(String line) {
        this.lines.add(line);
        return this;
    }

    @Override
    public ScoreBoard build(Plugin plugin) {
        if (id == null || delayToUpdate == 0) throw new IllegalArgumentException("You must specify the id and the delay to update the score");

        return new ScoreBoard(title.toArray(new String[0]),lines.toArray(new String[0]),id,condition,delayToUpdate,plugin);
    }
}
