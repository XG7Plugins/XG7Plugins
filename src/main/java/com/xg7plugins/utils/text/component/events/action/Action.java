package com.xg7plugins.utils.text.component.events.action;

public interface Action {

    <T extends Enum<T>> T toBukkitAction();
}
