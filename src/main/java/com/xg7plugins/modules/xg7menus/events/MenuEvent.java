package com.xg7plugins.modules.xg7menus.events;

import com.xg7plugins.modules.xg7menus.menus.holders.BasicMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.holders.MenuHolder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

@Getter
public class MenuEvent implements Cancellable {

    @Setter
    private boolean cancelled;
    protected final BasicMenuHolder holder;

    public MenuEvent(BasicMenuHolder holder) {
        this.holder = holder;
    }

}
