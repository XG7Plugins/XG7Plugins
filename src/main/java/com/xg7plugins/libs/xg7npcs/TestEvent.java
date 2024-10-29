package com.xg7plugins.libs.xg7npcs;

import com.xg7plugins.events.Event;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.libs.xg7holograms.event.HologramClickEvent;
import com.xg7plugins.libs.xg7npcs.event.NPCClickEvent;

public class TestEvent implements Event {
    @Override
    public boolean isEnabled() {
        return true;
    }


    @EventHandler
    public void onNPCClick(NPCClickEvent event) {
        try {
            event.getPlayer().sendMessage("You clicked on a npc!");
            event.getPlayer().sendMessage(event.getClickType().name());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
