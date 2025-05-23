package com.xg7plugins.utils.text.component.events;

import com.xg7plugins.utils.text.component.events.action.Action;

public interface ChatEvent {

    String serialize();

    String content();

    <T extends Action> T action();

    <T> T toBukkitEvent();


}
