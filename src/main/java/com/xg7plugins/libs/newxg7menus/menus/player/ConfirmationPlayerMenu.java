package com.xg7plugins.libs.newxg7menus.menus.player;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.events.MenuEvent;

public abstract class ConfirmationPlayerMenu extends PlayerMenu {


    protected ConfirmationPlayerMenu(Plugin plugin, String id, boolean storeOldItems) {
        super(plugin, id, storeOldItems);
    }

    public abstract <T extends MenuEvent> void confirm(T event);
    public abstract <T extends MenuEvent> void cancel(T event);

}
