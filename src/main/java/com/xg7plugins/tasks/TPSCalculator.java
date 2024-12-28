package com.xg7plugins.tasks;

import com.xg7plugins.XG7Plugins;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;

public class TPSCalculator {

    private int bukkitTaskId;
    private long startTick;
    @Getter
    private TaskState state;
    private final AtomicInteger ticks = new AtomicInteger();


    public void start() {
        this.startTick = System.currentTimeMillis();

        this.bukkitTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(XG7Plugins.getInstance(), () -> {
            if (System.currentTimeMillis() - startTick >= 1000) {
                startTick += 1000;
                ticks.set(0);
            }
            ticks.getAndIncrement();
        }, 0, 1).getTaskId();
        this.state = TaskState.RUNNING;
    }

    public double getTPS() {
        return ticks.get() / ((System.currentTimeMillis() - startTick) / 1000.0);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(bukkitTaskId);
        this.state = TaskState.IDLE;
    }



}
