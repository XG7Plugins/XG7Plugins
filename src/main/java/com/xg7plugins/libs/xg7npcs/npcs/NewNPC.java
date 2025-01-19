package com.xg7plugins.libs.xg7npcs.npcs;

import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7holograms.HologramBuilder;
import com.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.utils.location.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class NewNPC {

    private UserProfile profile;
    private Hologram hologramName;
    private boolean lookAtPlayer;
    private boolean playerSkin;
    private Location location;
    private String id;
    private Plugin plugin;

    public NewNPC(List<String> name, boolean lookAtPlayer, boolean playerSkin, String id, Location location, Plugin plugin) {
        this.hologramName = HologramBuilder.creator(plugin,id + ":name").setLines(name).setLocation(location.add(0,-0.2,0)).build();
        this.lookAtPlayer = lookAtPlayer;
        this.playerSkin = playerSkin;
        this.id = id;
        this.location = location;
        this.plugin = plugin;
    }

    public NPCState create(Player player) {

        // Enviar o pacote PlayerInfo para adicionar o NPC na tablist
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo(
                WrapperPlayServerPlayerInfo.Action.ADD_PLAYER
        );
        playerInfo.action = WrapperPlayServerPlayerInfo.Action.ADD_PLAYER;
        playerInfo.entries.add(new WrapperPlayServerPlayerInfo.Entry(npcProfile, 0, EntityType.PLAYER));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, playerInfo);

        // Enviar o pacote SpawnPlayer para exibir o NPC no mundo
        WrapperPlayServerSpawnPlayer spawnPlayer = new WrapperPlayServerSpawnPlayer();


    }
}
