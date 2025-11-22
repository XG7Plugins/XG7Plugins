package com.xg7plugins.modules.xg7npcs.task;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7npcs.XG7NPCs;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.location.Location;

public class NPCsLookTask extends TimerTask {
    private final XG7NPCs npcs;

    public NPCsLookTask(XG7NPCs npcs) {
        super(
                XG7Plugins.getInstance(),
                "npcs-task",
                0,
                1,
                TaskState.IDLE,
                null
        );

        this.npcs = npcs;
    }

    @Override
    public void run() {

        for (LivingNPC npc : npcs.getAllLivingNPCs()) {
            if (npc.getNPC().isLookAtPlayer()) npc.lookAt(Location.fromBukkit(npc.getPlayer().getLocation()));
        }
    }
}
