package com.xg7plugins.libs.xg7holograms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.libs.xg7holograms.holograms.Hologram1_17_1_XX;
import com.xg7plugins.libs.xg7holograms.holograms.Hologram1_8_1_16;
import com.xg7plugins.utils.Builder;
import com.xg7plugins.utils.Location;

import java.util.ArrayList;
import java.util.List;

public class HologramBuilder extends Builder<Hologram> {

    private List<String> lines;
    private Plugin plugin;
    private Location location;
    private String  id;

    public HologramBuilder(Plugin plugin, String id) {
        this.plugin = plugin;
        this.id = id;
        lines = new ArrayList<>();
    }

    public HologramBuilder addLine(String line) {
        lines.add(line);
        return this;
    }
    public HologramBuilder setLines(List<String> lines) {
        this.lines = lines;
        return this;
    }
    public HologramBuilder setLocation(Location location) {
        this.location = location;
        return this;
    }

    public Hologram build(Object... args) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        return XG7Plugins.getMinecraftVersion() < 17 ? new Hologram1_8_1_16(plugin, id,lines, location) : new Hologram1_17_1_XX(plugin,id, lines, location);
    }

    public static HologramBuilder creator(Plugin plugin, String id) {
        return new HologramBuilder(plugin,id);
    }


}
