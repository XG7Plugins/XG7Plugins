package com.xg7plugins.libs.xg7scores.builder;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7scores.scores.ActionBar;

import java.util.ArrayList;
import java.util.List;

public class ActionBarBuilder extends ScoreBuilder<ActionBar, ActionBarBuilder> {

    private List<String> text = new ArrayList<>();

    public ActionBarBuilder(String id) {
        super(id);
    }

    public ActionBarBuilder text(List<String> text) {
        this.text = text;
        return this;
    }
    public ActionBarBuilder addTextUpdate(String text) {
        this.text.add(text);
        return this;
    }

    @Override
    public ActionBar build(Object... args) {
        if (id == null || delayToUpdate == 0) throw new IllegalArgumentException("You must specify the id and the delay to update the score");

        return new ActionBar(delayToUpdate,text.toArray(new String[0]),id,condition, (Plugin) args[0]);
    }
}
