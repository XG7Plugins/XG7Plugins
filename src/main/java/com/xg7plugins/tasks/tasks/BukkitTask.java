package com.xg7plugins.tasks.tasks;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.TaskState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter
@Setter
public abstract class BukkitTask extends Task {

    public int bukkitTaskId;
    private final boolean isAsync;

    public BukkitTask(Plugin plugin) {
        super(plugin);
        this.isAsync = false;
    }
    public BukkitTask(Plugin plugin, boolean isAsync) {
        super(plugin);
        this.isAsync = isAsync;
    }
    public static BukkitTask of(Plugin plugin, Runnable runnable) {
        return new BukkitTask(plugin) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }
    public static BukkitTask of(Plugin plugin, boolean isAsync, Runnable runnable) {
        return new BukkitTask(plugin, isAsync) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }


    @Override
    public void cancel() {
        Bukkit.getScheduler().cancelTask(bukkitTaskId);
    }
}
