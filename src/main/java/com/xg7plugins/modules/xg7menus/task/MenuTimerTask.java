package com.xg7plugins.modules.xg7menus.task;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.tasks.tasks.AsyncTask;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;

import java.util.concurrent.atomic.AtomicLong;

public class MenuTimerTask extends TimerTask {

    private final XG7Menus menus;
    private final AtomicLong counter = new AtomicLong();

    public MenuTimerTask(XG7Menus menus) {
        super(
                XG7Plugins.getInstance(),
                "menu-task",
                0,
                1,
                TaskState.IDLE,
                null
        );

        this.menus = menus;
    }

    @Override
    public void run() {
        menus.getRegisteredMenus().values().forEach(menu -> {
            if (menu.getMenuConfigs().repeatingUpdateMills() < 0) return;
            if (counter.get() % menu.getMenuConfigs().repeatingUpdateMills() != 0) return;

            menus.getMenuHolders().values()
                    .stream()
                    .filter(holder -> holder.getMenu().getMenuConfigs().getId().equals(menu.getMenuConfigs().getId()))
                    .forEach(holder -> holder.getMenu().onRepeatingUpdate(holder));

            menus.getPlayerMenusMap().values()
                    .stream()
                    .filter(holder -> holder.getMenu().getMenuConfigs().getId().equals(menu.getMenuConfigs().getId()))
                    .forEach(holder -> holder.getMenu().onRepeatingUpdate(holder));
        });
        counter.incrementAndGet();
    }
}
