package com.xg7plugins.tasks.plugin_tasks;

import com.google.common.util.concurrent.AtomicDouble;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.tasks.tasks.Task;
import com.xg7plugins.tasks.tasks.TimerTask;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;

public class TPSCalculator extends TimerTask {

    private long startNanoTime = System.nanoTime();

    private final AtomicInteger ticks = new AtomicInteger();
    private final AtomicDouble lastTps = new AtomicDouble();

    public TPSCalculator() {
        super(
                XG7Plugins.getInstance(),
                "tps-calculator",
                0,
                50,
                TaskState.RUNNING,
                true
        );
    }

    public double getTPS() {
        return lastTps.get();
    }

    @Override
    public void run() {

        long elapsed = System.nanoTime() - startNanoTime;
        double seconds = (double) elapsed / 1e+9;

        if (seconds >= 1) {
            double tps = ticks.get() / seconds;
            lastTps.set(tps);
            ticks.set(0);
            startNanoTime = System.nanoTime();
            return;
        }

        ticks.incrementAndGet(); // só conta tick se não reiniciou ainda
    }




}
