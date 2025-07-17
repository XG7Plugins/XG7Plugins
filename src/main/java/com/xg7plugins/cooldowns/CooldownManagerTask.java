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


/**
 * This class is the task that manages cooldowns by periodically updating cooldown timers
 * and removing expired cooldowns
 */
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

            for (CooldownManager.CooldownTask task : new ArrayList<>(tasks.values())) {
                long oldTime = task.getTime();
                task.setTime(oldTime - timeFactor);

                if (player == null) {
                    toRemove.add(Pair.of(id, task));
                    continue;
                }

                if (task.getTick() != null) {
                    try {
                        task.getTick().accept(player);
                    } catch (Exception e) {
                        if (task.getOnFinish() != null) task.getOnFinish().accept(player, true);
                        toRemove.add(Pair.of(id, task));
                        e.printStackTrace();
                        return;
                    }

                }
                if (task.getTime() <= 0) {
                    try {
                        if (task.getOnFinish() != null) task.getOnFinish().accept(player, false);
                        toRemove.add(Pair.of(id, task));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                }
            }
        });

        for (Pair<UUID, CooldownManager.CooldownTask> pair : toRemove) {
            if (manager.getCooldowns().get(pair.getFirst()) == null) continue;

            manager.removeCooldown(pair.getSecond().getId(), pair.getFirst());

            if (manager.getCooldowns().get(pair.getFirst()).isEmpty()) manager.getCooldowns().remove(pair.getFirst());
        }

    }
}
