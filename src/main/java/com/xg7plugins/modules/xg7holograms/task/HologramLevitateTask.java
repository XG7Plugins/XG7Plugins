package com.xg7plugins.modules.xg7holograms.task;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7holograms.XG7Holograms;
import com.xg7plugins.modules.xg7holograms.hologram.LivingHologram;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;

import java.util.concurrent.atomic.AtomicInteger;

public class HologramLevitateTask extends TimerTask {

    private final XG7Holograms holograms;

    private final AtomicInteger counter = new AtomicInteger();

    public HologramLevitateTask(XG7Holograms holograms) {
        super(
                XG7Plugins.getInstance(),
                "holograms-levitating-task",
                0,
                ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().getTimeInMilliseconds("holograms-levitating-interval", 50L),
                TaskState.IDLE,
                null
        );

        this.holograms = holograms;
    }

    @Override
    public void run() {

        int index = counter.getAndIncrement();

        double angle = index * 0.1;
        float yaw = (float) ((angle * 180.0 / Math.PI) % 360.0);
        double deltaY = 0.5 * Math.sin(angle);

        for (LivingHologram spawner : holograms.getAllLivingHolograms()) {
            spawner.levitate(yaw, deltaY);
        }
    }
}
