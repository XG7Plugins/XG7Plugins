package com.xg7plugins.utils.text.newComponent.events.action;

public interface Action {

    <T extends Enum<T>> T toBukkitAction();
}
