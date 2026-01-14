package com.xg7plugins.modules.xg7holograms.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.xg7plugins.modules.xg7holograms.hologram.line.HologramLine;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.*;

@Data
public class LivingHologram {

    private final Player player;
    private final Hologram hologram;

    private final Map<Integer, LivingLine> currentLines = new LinkedHashMap<>();

    public void spawn() {

        List<HologramLine> lines = new ArrayList<>(hologram.getLines());
        Collections.reverse(lines);

        Location location = hologram.getLocation().clone();

        float lastSpacing = 0;

        for (int i = 0; i < lines.size(); i++) {
            HologramLine line = lines.get(i);

            int logicalIndex = hologram.getLines().size() - i - 1;
            spawnLine(line, location.add(0, lastSpacing, 0).clone(), logicalIndex);

            lastSpacing = line.getSpacing();

        }

    }

    private void spawnLine(HologramLine line, Location location, int lineIndex) {
        int[] entityIDs = line.spawn(this, location);
        LivingLine livingLine = new LivingLine(line, location, entityIDs);

        line.getEquipment().forEach((slot, item) -> line.equip(this, livingLine, slot, item));

        currentLines.put(lineIndex, livingLine);
    }

    public void update() {

        if (currentLines.isEmpty()) return;

        for (int i = hologram.getLines().size() - 1; i >= 0; i--) {
            LivingLine line = currentLines.get(i);

            if (line == null) continue;

            line.getHologramLine().update(this, line);
        }
    }

    public void levitate(double offsetY) {
        if (currentLines.isEmpty()) return;

        List<Integer> entitiesToLevitate = new ArrayList<>();
        for (int i = hologram.getLines().size() - 1; i >= 0; i--) {
            LivingLine line = currentLines.get(i);
            if (line == null) continue;
            if (!line.getHologramLine().levitate()) continue;
            Arrays.stream(line.getSpawnedEntities()).forEach(entitiesToLevitate::add);
        }

        for (int entityID : entitiesToLevitate) {
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

        if (currentLines.isEmpty()) return;

        List<Integer> entitiesToDestroy = new ArrayList<>();

        for (LivingLine line : currentLines.values()) {
            for (int entityId : line.getSpawnedEntities()) {
                entitiesToDestroy.add(entityId);
            }
        }

        WrapperPlayServerDestroyEntities packet =
                new WrapperPlayServerDestroyEntities(
                        entitiesToDestroy.stream().mapToInt(i -> i).toArray()
                );

        PacketEvents.getAPI()
                .getPlayerManager()
                .sendPacket(player, packet);

        currentLines.clear();
    }

    public void killLine(int lineIndex) {

        if (lineIndex < 0 || lineIndex >= currentLines.size()) return;

        LivingLine line = currentLines.get(lineIndex);
        WrapperPlayServerDestroyEntities entities = new WrapperPlayServerDestroyEntities(line.getSpawnedEntities());
        PacketEvents.getAPI().getPlayerManager().sendPacket(getPlayer(), entities);

        currentLines.remove(lineIndex);
    }

    public void equip(int lineIndex, EquipmentSlot slot, Item item) {

        if (lineIndex < 0 || lineIndex >= currentLines.size()) return;

        LivingLine line = currentLines.get(lineIndex);
        if (line == null) return;
        line.getHologramLine().equip(this, line, slot, item);
    }

    public void modifyLine(int lineIndex, HologramLine newHologramLine) {

        if (lineIndex < 0 || lineIndex >= currentLines.size()) return;

        LivingLine line = currentLines.get(lineIndex);

        killLine(lineIndex);
        spawnLine(newHologramLine, line.getLocation(), lineIndex);

    }

    public Hologram checkHologramByEntityID(int entityID) {
        return currentLines.values()
                .stream()
                .anyMatch(l -> Arrays.stream(l.getSpawnedEntities())
                        .anyMatch(i -> i == entityID)
                ) ? getHologram() : null;
    }

    public List<Integer> getAllSpawnedEntitiesID() {
        List<Integer> entitiesIdList = new ArrayList<>();

        for (LivingLine line : currentLines.values()) {
            for (int entityId : line.getSpawnedEntities()) {
                entitiesIdList.add(entityId);
            }
        }

        return entitiesIdList;
    }

    @Data
    public static class LivingLine {

        private final HologramLine hologramLine;
        private final Location location;
        private final int[] spawnedEntities;

    }
}
