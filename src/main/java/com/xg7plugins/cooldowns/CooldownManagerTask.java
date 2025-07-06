package com.xg7plugins.cooldowns;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CooldownManagerTask extends TimerTask {

    private final CooldownManager manager;
    private final long timeFactor;

    public CooldownManagerTask(CooldownManager manager, long timeFactor) {
        super(
                XG7Plugins.getInstance(),
                "cooldown-manager",
                0,
                timeFactor,
                TaskState.IDLE,
                null
        );

        this.timeFactor = timeFactor;
        this.manager = manager;
    }


    @Override
    public void run() {

        List<Pair<UUID, CooldownManager.CooldownTask>> toRemove = new ArrayList<>();

        manager.getCooldowns().forEach((id, tasks) -> {
            Player player = Bukkit.getPlayer(id);
            System.out.println("Processing cooldowns for player UUID: " + id);

            tasks.values().forEach(task -> {
                long oldTime = task.getTime();
                task.setTime(oldTime - timeFactor);
                System.out.println("Task " + task.getId() + " time updated from " + oldTime + " to " + task.getTime());

                if (player == null) {
                    System.out.println("Player is null, removing task " + task.getId() + " for UUID " + id);
                    manager.removeCooldown(task.getId(), id);
                    return;
                }

                if (task.getTick() != null) {
                    System.out.println("Executing tick for task " + task.getId());
                    try {
                        task.getTick().accept(player);
                    } catch (Exception e) {
                        if (task.getOnFinish() != null) task.getOnFinish().accept(player, true);
                        toRemove.add(Pair.of(id, task));
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
                if (task.getTime() <= 0) {
                    try {
                        System.out.println("Task " + task.getId() + " completed, executing finish callback");
                        if (task.getOnFinish() != null) task.getOnFinish().accept(player, false);
                        toRemove.add(Pair.of(id, task));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        });

        for (Pair<UUID, CooldownManager.CooldownTask> pair : toRemove) {
            System.out.println("Processing removal for UUID: " + pair.getFirst() + ", task: " + pair.getSecond());
            if (manager.getCooldowns().get(pair.getFirst()) == null) {
                System.out.println("No cooldowns found for UUID: " + pair.getFirst());
                continue;
            }
            manager.removeCooldown(pair.getSecond().getId(), pair.getFirst());
            System.out.println("Removed task " + pair.getSecond());

            if (manager.getCooldowns().get(pair.getFirst()).isEmpty()) {
                System.out.println("Removing empty cooldown map for UUID: " + pair.getFirst());
                manager.getCooldowns().remove(pair.getFirst());
            }
        }

    }
}
