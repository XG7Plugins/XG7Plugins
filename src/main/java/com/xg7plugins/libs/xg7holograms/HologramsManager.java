package com.xg7plugins.libs.xg7holograms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.libs.xg7holograms.holograms.HologramState;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HologramsManager {

    private final XG7Plugins plugin;

    @Getter
    private final Task task;

    private final HashMap<String, Hologram> holograms = new HashMap<>();
    private final ConcurrentHashMap<UUID, HologramState> hologramStates = new ConcurrentHashMap<>();

    public HologramsManager(XG7Plugins plugin) {
        this.plugin = plugin;
        long delay = plugin.getConfigsManager().getConfig("config").getTime("holograms-update-delay").orElse(100L);
        this.task = new Task(
                plugin,
                "holograms",
                true,
                true,
                delay,
                TaskState.IDLE,
                () -> {
                    holograms.values().forEach(hologram -> Bukkit.getOnlinePlayers().forEach(player -> {
                        if (!player.getWorld().getName().equals(hologram.getLocation().getWorldName())) {
                            return;
                        }
                        hologramStates.put(player.getUniqueId(), hologram.create(player));
                    }));

                    hologramStates.values().forEach(hologram -> {
                        if (!hologram.getPlayer().getWorld().getName().equals(hologram.getHologram().getLocation().getWorldName())) {
                            hologram.destroy();
                            hologramStates.remove(hologram.getPlayer().getUniqueId());
                            return;
                        }

                        hologram.update();
                    });
                }
        );
    }

    public void addHologram(Hologram hologram) {
        holograms.put(hologram.getId(), hologram);
    }
    public void removeHologram(Hologram hologram) {
        holograms.remove(hologram.getId());
    }

    public void cancelTask() {
        plugin.getTaskManager().cancelTask(task);
    }

    public HologramState getHologramState(Player player) {
        return hologramStates.get(player.getUniqueId());
    }


}
