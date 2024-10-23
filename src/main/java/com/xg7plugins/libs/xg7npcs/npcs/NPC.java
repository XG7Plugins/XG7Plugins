package com.xg7plugins.libs.xg7npcs.npcs;

import com.mojang.authlib.GameProfile;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7holograms.HologramBuilder;
import com.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.utils.Location;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public abstract class NPC {

    protected Hologram name;
    protected GameProfile skin;
    protected Location location;

    public NPC(Plugin plugin, List<String> name, GameProfile skin, Location location) {
        this.name = HologramBuilder.creator(plugin).setLines(name).setLocation(location.add(0,-0.2,0)).build();
        this.skin = skin;
        this.location = location;
    }

    public abstract void spawn(Player player);
    public abstract void destroy(Player player);
    public abstract void update(Player player);
    public abstract void setSkin(GameProfile skin);

}
