package com.xg7plugins.modules.xg7scores.builder;



import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.scores.TabList;

import java.util.ArrayList;
import java.util.List;

public class TablistBuilder extends ScoreBuilder<TabList, TablistBuilder> {

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
        this.footer = footer;
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

    public TabList build(Plugin plugin) {
        return new TabList(delayToUpdate, header, footer, playerPrefix, playerSuffix, id, condition, plugin);
    }
}
