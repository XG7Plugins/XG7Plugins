package com.xg7plugins.libs.xg7scores.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7scores.scores.Tablist;

import java.util.ArrayList;
import java.util.List;

public class TablistBuilder extends ScoreBuilder<TablistBuilder> {

    private List<String> header = new ArrayList<>();
    private List<String> footer = new ArrayList<>();

    private String playerPrefix = "";
    private String playerSuffix = "";

    public TablistBuilder(String id) {
        super(id);
    }

    public TablistBuilder header(List<String> header) {
        this.header = header;
        return this;
    }
    public TablistBuilder addHeaderLine(String line) {
        this.header.add(line);
        return this;
    }
    public TablistBuilder footer(List<String> footer) {
        this.footer = header;
        return this;
    }
    public TablistBuilder addFooterLine(String line) {
        this.footer.add(line);
        return this;
    }
    public TablistBuilder playerPrefix(String prefix) {
        this.playerPrefix = prefix;
        return this;
    }
    public TablistBuilder playerSuffix(String suffix) {
        this.playerSuffix = suffix;
        return this;
    }

    @Override
    public Tablist build(Plugin plugin) {
        return new Tablist(delayToUpdate, header.toArray(new String[0]), footer.toArray(new String[0]), playerPrefix,playerSuffix,id, condition, plugin);
    }
}
