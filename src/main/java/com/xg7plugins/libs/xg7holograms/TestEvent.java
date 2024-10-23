package com.xg7plugins.libs.xg7holograms;

import com.xg7plugins.events.Event;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.libs.xg7holograms.event.HologramClickEvent;

public class TestEvent implements Event {
    @Override
    public boolean isEnabled() {
        return true;
    }


    @EventHandler
    public void onHologramClick(HologramClickEvent event) {
        try {
            event.getPlayer().sendMessage("You clicked on a hologram!");
            event.getPlayer().sendMessage(event.getClickType().name());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
