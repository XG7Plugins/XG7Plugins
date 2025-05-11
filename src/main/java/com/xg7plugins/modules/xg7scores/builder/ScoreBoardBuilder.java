package com.xg7plugins.modules.xg7scores.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.scores.scoreboard.ScoreBoard;

import java.util.ArrayList;
import java.util.List;

public class ScoreBoardBuilder extends ScoreBuilder<ScoreBoard, ScoreBoardBuilder> {

    private List<String> title = new ArrayList<>();
    private List<String> lines = new ArrayList<>();

    private boolean healthDisplay;
    private boolean sideBar;

    private String healthDisplaySuffix;

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

    public ScoreBoardBuilder allowHealthDisplay(boolean healthDisplay) {
        this.healthDisplay = healthDisplay;
        return this;
    }

    public ScoreBoardBuilder allowSideBar(boolean sideBar) {
        this.sideBar = sideBar;
        return this;
    }

    public ScoreBoardBuilder healthDisplaySuffix(String healthDisplaySuffix) {
        this.healthDisplaySuffix = healthDisplaySuffix;
        return this;
    }
    @Override
    public ScoreBoard build(Object... args) {
        if (id == null || delayToUpdate == 0) throw new IllegalArgumentException("You must specify the id and the delay to update the score");

        return new ScoreBoard(title,lines,id,condition,delayToUpdate,(Plugin) args[0],healthDisplay,sideBar,healthDisplaySuffix);
    }
}
