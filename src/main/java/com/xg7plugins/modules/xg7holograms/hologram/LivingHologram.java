package com.xg7plugins.modules.xg7holograms.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7holograms.hologram.line.HologramLine;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class LivingHologram {

    private final Player player;
    private final Hologram hologram;

    private final List<Integer> spawnedEntitiesID = new ArrayList<>();


    public void spawn() {

        List<HologramLine> lines = new ArrayList<>(hologram.getLines());
        Collections.reverse(lines);

        Location location = hologram.getLocation().clone();

        float lastSpacing = 0;

        for (HologramLine line : lines) {

            int entityID = line.spawn(this, location.add(0, lastSpacing, 0));

            lastSpacing = line.getSpacing();

            spawnedEntitiesID.add(entityID);
        }

    }

    public void update() {

        if (spawnedEntitiesID.isEmpty()) return;

        for (int i = hologram.getLines().size() - 1; i >= 0; i--) {
            int entityID = spawnedEntitiesID.get(hologram.getLines().size() - 1 - i);
            HologramLine line = hologram.getLines().get(i);

            line.update(this, entityID);
        }
    }

    public void levitate(double offsetY) {
        if (spawnedEntitiesID.isEmpty()) return;


        for (int i = hologram.getLines().size() - 1; i >= 0; i--) {
            int entityID = spawnedEntitiesID.get(hologram.getLines().size() - 1 - i);
            HologramLine line = hologram.getLines().get(i);

            if (!line.levitate()) continue;

            WrapperPlayServerEntityRelativeMove packet =
                    new WrapperPlayServerEntityRelativeMove(
                            entityID,
                            0,
                            offsetY * 0.07,
                            0,
                            false
                    );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        }
    }


    public void kill() {
        List<Integer> spawnedEntitiesID = getSpawnedEntitiesID();

        if (spawnedEntitiesID.isEmpty()) return;

        int[] ids = spawnedEntitiesID.stream().mapToInt(Integer::intValue).toArray();
        WrapperPlayServerDestroyEntities entities = new WrapperPlayServerDestroyEntities(ids);

        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), entities);
    }

    public void equip(int lineIndex, EquipmentSlot slot, Item item) {
        List<Integer> spawnedEntitiesID = getSpawnedEntitiesID();

        if (lineIndex < 0 || lineIndex >= spawnedEntitiesID.size()) return;

        int entityID = spawnedEntitiesID.get(hologram.getLines().size() - 1 - lineIndex);
        HologramLine line = hologram.getLines().get(lineIndex);

        line.equip(this, entityID, slot, item);
    }

    public Hologram checkHologramByEntityID(int entityID) {
        return getSpawnedEntitiesID().contains(entityID) ? getHologram() : null;
    }
}
