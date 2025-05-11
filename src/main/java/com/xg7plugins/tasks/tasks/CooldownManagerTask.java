package com.xg7plugins.tasks.tasks;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.CooldownManager;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CooldownManagerTask extends Task {

    private final CooldownManager manager;
    private final long timeFactor;

    public CooldownManagerTask(CooldownManager manager, long timeFactor) {
        super(
                XG7Plugins.getInstance(),
                "cooldown-manager",
                true,
                true,
                timeFactor,
                TaskState.IDLE,
                null
        );

        this.timeFactor = timeFactor;
        this.manager = manager;
    }

    @Override
    public void run() {

        manager.getCooldowns().forEach((id, tasks) -> {
            Player player = Bukkit.getPlayer(id);

            tasks.values().forEach(task -> {
                task.setTime(task.getTime() - timeFactor);
                if (player == null) {
                    manager.removePlayer(task.getId(), id);
                    return;
                }

                if (task.getTick() != null) task.getTick().accept(player);
                if (task.getTime() <= 0) {
                    if (task.getOnFinish() != null) task.getOnFinish().accept(player, false);
                    manager.getToRemove().add(new Pair<>(id, task.getId()));
                }
            });
        });

        for (Pair<UUID, String> pair : manager.getToRemove()) {
            if (manager.getCooldowns().get(pair.getFirst()) == null) continue;
            manager.getCooldowns().get(pair.getFirst()).remove(pair.getSecond());

            if (manager.getCooldowns().get(pair.getFirst()).isEmpty()) manager.getCooldowns().remove(pair.getFirst());
        }

        manager.getToRemove().clear();

    }

}
