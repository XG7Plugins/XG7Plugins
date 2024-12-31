package com.xg7plugins.tasks;

import com.google.common.util.concurrent.AtomicDouble;
import com.xg7plugins.XG7Plugins;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;

public class TPSCalculator {

    private int bukkitTaskId;
    private long startNanoTime;
    @Getter
    private TaskState state;
    private final AtomicInteger ticks = new AtomicInteger();
    private final AtomicDouble lastTps = new AtomicDouble();

    public void start() {
        this.startNanoTime = System.nanoTime();
        this.bukkitTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(XG7Plugins.getInstance(), () -> {
            long elapsed = System.nanoTime() - startNanoTime;
            double seconds = elapsed / 1_000_000_000.0;

            if (seconds >= 1) {
                double tps = ticks.get() / seconds;
                lastTps.set(tps);
                ticks.set(0);
                startNanoTime = System.nanoTime();
            }
            ticks.getAndIncrement();
        }, 0, 1).getTaskId();

        this.state = TaskState.RUNNING;
    }

    public double getTPS() {
        return lastTps.get();
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(bukkitTaskId);
        this.state = TaskState.IDLE;
    }



}
