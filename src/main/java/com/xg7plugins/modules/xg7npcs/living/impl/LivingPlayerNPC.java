package com.xg7plugins.modules.xg7npcs.living.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import com.xg7plugins.modules.xg7npcs.living.NPCMetaProvider;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.modules.xg7npcs.npc.impl.PlayerNPC;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.skin.Skin;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Collections;

@Data
public class LivingPlayerNPC implements LivingNPC {

    private final Player player;
    private final PlayerNPC playerNPC;

    private LivingHologram spawnedHologram = null;

    private Location currentLocation = null;
    private Skin currentSkin = null;

    private boolean moving = false;

    private int spawnedEntityID = -1;

    private final WrapperPlayServerTeams teams = new WrapperPlayServerTeams(
                "npc_hidden_name",
                WrapperPlayServerTeams.TeamMode.CREATE,
                new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                        Component.text(""),
                        Component.text(""),
                        Component.text(""),
                        WrapperPlayServerTeams.NameTagVisibility.NEVER,
                        WrapperPlayServerTeams.CollisionRule.NEVER,
                        NamedTextColor.WHITE,
                        WrapperPlayServerTeams.OptionData.ALL
                )
    );

    public LivingPlayerNPC(Player player, PlayerNPC playerNPC) {
        this.player = player;
        this.playerNPC = playerNPC;
    }


    @Override
    public NPC getNPC() {
        return playerNPC;
    }

    @Override
    public void spawn() {

        if (spawnedEntityID >= 0) return;

        if (currentSkin == null) currentSkin = playerNPC.getSkin();

        if (currentLocation == null) currentLocation = playerNPC.getSpawnLocation().clone();

        UserProfile profile = currentSkin.toBaseProfile();

        if (playerNPC.isUsePlayerSkin()) {
            UserProfile playerProfile = PacketEvents.getAPI().getPlayerManager().getUser(player).getProfile();
            profile.setTextureProperties(playerProfile.getTextureProperties());
        }

        ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();

        PacketWrapper<?> addInfo = version.isOlderThan(ServerVersion.V_1_19_3) ? new WrapperPlayServerPlayerInfo(
                WrapperPlayServerPlayerInfo.Action.ADD_PLAYER,
                new WrapperPlayServerPlayerInfo.PlayerData(
                        Component.text(" "), profile, GameMode.SURVIVAL, 0
                )
        ) :
                new WrapperPlayServerPlayerInfoUpdate(
                        WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
                        new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(profile)
                );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, addInfo);

        System.out.println("Pacote de info: " + addInfo);

        teams.setTeamName("npc_hidden_name_" + spawnedEntityID);
        teams.setTeamMode(WrapperPlayServerTeams.TeamMode.CREATE);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, teams);

        WrapperPlayServerTeams addNpcToTeam = new WrapperPlayServerTeams(
                teams.getTeamName(),
                WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
                teams.getTeamInfo().orElse(null),
                Collections.singletonList(profile.getName())
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, addNpcToTeam);

        defaultSpawn(profile.getUUID(), NPCMetaProvider.getPlayerNPCData());

        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of(() -> {
            PacketWrapper<?> remove = version.isOlderThan(ServerVersion.V_1_19_3) ? new WrapperPlayServerPlayerInfo(
                    WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER,
                    new WrapperPlayServerPlayerInfo.PlayerData(Component.text(" "), profile, GameMode.SURVIVAL, 0)
            ) : new WrapperPlayServerPlayerInfoRemove(profile.getUUID());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, remove);
        }), 1000L);
    }

    public void changeSkin(Skin skin) {
        if (spawnedEntityID < 0) return;

        currentSkin = skin;

        kill();
        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of(this::spawn), 300L);

    }

    @Override
    public void kill() {
        if (getSpawnedEntityID() < 0) return;

        LivingNPC.super.kill();

        teams.setTeamName("npc_hidden_name_" + spawnedEntityID);
        teams.setTeamMode(WrapperPlayServerTeams.TeamMode.REMOVE);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, teams);
    }
}
