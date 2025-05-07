package com.xg7plugins.modules.xg7menus.events;

import com.xg7plugins.modules.xg7menus.menus.holders.MenuHolder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

@Getter
public class MenuEvent implements Cancellable {

    @Setter
    private boolean cancelled;
    protected final MenuHolder holder;

    public MenuEvent(MenuHolder holder) {
        this.holder = holder;
    }

}
