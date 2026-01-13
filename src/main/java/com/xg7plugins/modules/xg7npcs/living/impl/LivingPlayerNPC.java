package com.xg7plugins.modules.xg7npcs.living.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Data
public class LivingPlayerNPC implements LivingNPC {

    private final Player player;
    private final PlayerNPC playerNPC;

    private LivingHologram spawnedHologram = null;

    private Location currentLocation = null;
    private Skin currentSkin = null;

    private boolean moving = false;

    private int[] spawnedEntitiesID = null;

    public static final WrapperPlayServerTeams teams = new WrapperPlayServerTeams(
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

        if (spawnedEntitiesID != null) return;

        if (currentSkin == null) currentSkin = playerNPC.getSkin();

        if (currentLocation == null) currentLocation = playerNPC.getSpawnLocation().clone();

        if (PlayerNPC.USE_MANNEQUIN) spawnMannequin();
        else spawnPlayer();

    }

    private void spawnPlayer() {
        UserProfile profile = currentSkin.toBaseProfile();

        if (playerNPC.isUsePlayerSkin()) profile = Skin.ofPlayer(player).toBaseProfile();

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

        WrapperPlayServerTeams addNpcToTeam = new WrapperPlayServerTeams(
                teams.getTeamName(),
                WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
                teams.getTeamInfo().orElse(null),
                Collections.singletonList(profile.getName())
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, addNpcToTeam);

        defaultSpawn(profile.getUUID(), NPCMetaProvider.getPlayerNPCData());

        UserProfile finalProfile = profile;
        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of(() -> {
            PacketWrapper<?> remove = version.isOlderThan(ServerVersion.V_1_19_3) ? new WrapperPlayServerPlayerInfo(
                    WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER,
                    new WrapperPlayServerPlayerInfo.PlayerData(Component.text(" "), finalProfile, GameMode.SURVIVAL, 0)
            ) : new WrapperPlayServerPlayerInfoRemove(finalProfile.getUUID());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, remove);
        }), 1000L);

    }

    private void spawnMannequin() {
        ItemProfile profile = currentSkin.toItemProfile();

        if (playerNPC.isUsePlayerSkin()) profile = Skin.ofPlayer(player).toItemProfile();

        defaultSpawn(UUID.randomUUID(), NPCMetaProvider.getMannequinData(profile));
    }

    public void changeSkin(Skin skin) {
        if (spawnedEntitiesID == null) return;

        currentSkin = skin;

        kill();
        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of(this::spawn), 300L);

    }

    @Override
    public void kill() {
        if (spawnedEntitiesID == null) return;

        LivingNPC.super.kill();
    }
}
