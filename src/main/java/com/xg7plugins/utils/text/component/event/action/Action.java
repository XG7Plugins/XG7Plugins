package com.xg7plugins.utils.text.component.event.action;

public interface Action {

    <T extends Enum<T>> T toBukkitAction();
}
