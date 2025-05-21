package com.xg7plugins.utils.text.newComponent.events;

import com.xg7plugins.utils.text.newComponent.events.action.Action;

public interface ChatEvent {

    String content();

    <T extends Action> T action();

    <T> T toBukkitEvent();


}
