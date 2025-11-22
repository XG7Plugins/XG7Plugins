package com.xg7plugins.modules.xg7holograms;

import com.xg7plugins.events.Listener;
import com.xg7plugins.modules.Module;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.modules.xg7holograms.listeners.HologramClickListener;
import com.xg7plugins.modules.xg7holograms.listeners.HologramListener;
import com.xg7plugins.modules.xg7holograms.task.HologramLevitateTask;
import com.xg7plugins.modules.xg7holograms.task.HologramTask;
import com.xg7plugins.tasks.tasks.TimerTask;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class XG7Holograms implements Module {

    private boolean enabled;

    private final HologramTask hologramTask = new HologramTask(this);

    private final HashMap<String, Hologram> registeredHolograms = new HashMap<>();
    private final HashMap<UUID, List<LivingHologram>> livingHolograms = new HashMap<>();

    @Override
    public void onInit() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onReload() {

        livingHolograms.values().forEach(listLiving -> listLiving.forEach(LivingHologram::kill));

    }

    @Override
    public String getName() {
        return "XG7Holograms";
    }

    @Override
    public List<Listener> loadListeners() {
        return Arrays.asList(new HologramListener(), new HologramClickListener());
    }

    public List<TimerTask> loadTasks() {
        return Arrays.asList(new HologramTask(this), new HologramLevitateTask(this));
    }

    public void registerHologram(Hologram hologram) {
        if (hologram == null) return;
        this.registeredHolograms.put(hologram.getId(), hologram);
    }

    public List<LivingHologram> getAllLivingHolograms() {
        List<LivingHologram> livingHolograms = new ArrayList<>();

        for (List<LivingHologram> hologram : this.livingHolograms.values()) livingHolograms.addAll(hologram);

        return livingHolograms;
    }

    public void registerLivingHologram(LivingHologram livingHologram) {
        this.livingHolograms.putIfAbsent(livingHologram.getPlayer().getUniqueId(), new ArrayList<>());
        this.livingHolograms.get(livingHologram.getPlayer().getUniqueId()).add(livingHologram);
    }
    public void unregisterLivingHologram(UUID playerUUID, String hologramId) {

        if (!this.livingHolograms.containsKey(playerUUID)) return;

        this.livingHolograms.get(playerUUID).removeIf(hologram -> {
            if (hologram.getHologram().getId().equals(hologramId)) {
                hologram.kill();
                return true;
            }
            return false;
        });
    }

    public @NotNull List<LivingHologram> getLivingHologramsByUUID(UUID uuid) {
        return this.livingHolograms.getOrDefault(uuid, new ArrayList<>());
    }

    public LivingHologram getLivingHologram(UUID uuid, String hologramId) {
        if (!this.livingHolograms.containsKey(uuid)) return null;

        return this.livingHolograms.get(uuid).stream().filter(h -> h.getHologram().getId().equals(hologramId)).findFirst().orElse(null);
    }

    public void unregisterAllLivingHolograms(UUID uuid) {
        if (!this.livingHolograms.containsKey(uuid)) return;

        this.livingHolograms.get(uuid).forEach(LivingHologram::kill);

        this.livingHolograms.remove(uuid);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
