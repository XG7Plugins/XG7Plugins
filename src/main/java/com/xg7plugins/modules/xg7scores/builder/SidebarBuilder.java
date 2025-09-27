package com.xg7plugins.modules.xg7scores.builder;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar.GenericSidebar;
import com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar.LegacySidebar;
import com.xg7plugins.modules.xg7scores.scores.scoreboards.sidebar.Sidebar;
import com.xg7plugins.server.MinecraftVersion;

import java.util.ArrayList;
import java.util.List;

public class SidebarBuilder<T extends GenericSidebar> extends ScoreBuilder<T, SidebarBuilder<T>> {

    private List<String> title = new ArrayList<>();
    private List<String> lines = new ArrayList<>();

    public SidebarBuilder(String id) {
        super(id);
    }

    public SidebarBuilder<T> title(List<String> title) {
        this.title = title;
        return this;
    }
    public SidebarBuilder<T> addTitleUpdate(String title) {
        this.title.add(title);
        return this;
    }
    public SidebarBuilder<T> lines(List<String> lines) {
        this.lines = lines;
        return this;
    }
    public SidebarBuilder<T> addLine(String line) {
        this.lines.add(line);
        return this;
    }

    public T build(Plugin plugin) {
        if (id == null || delayToUpdate == 0) throw new IllegalArgumentException("You must specify the id and the delay to update the score");

        if (MinecraftVersion.isOlderThan(13)) return (T) new LegacySidebar(title,lines,id,condition,delayToUpdate,plugin);

        return (T) new Sidebar(title,lines,id,condition,delayToUpdate,plugin);
    }
}
