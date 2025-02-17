package com.xg7plugins.utils.text.component.event;

import com.xg7plugins.utils.text.component.event.action.Action;

public interface Event {

    String content();

    <T extends Action> T action();

    <T> T toBukkitEvent();


}
