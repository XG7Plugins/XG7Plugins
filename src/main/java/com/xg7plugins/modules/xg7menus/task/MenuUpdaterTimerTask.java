package com.xg7plugins.modules.xg7menus.task;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.player.PlayerMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MenuUpdaterTimerTask extends TimerTask {

    private final XG7Menus menus;
    private final AtomicLong counter = new AtomicLong();

    public MenuUpdaterTimerTask(XG7Menus menus) {
        super(
                XG7Plugins.getInstance(),
                "menu-task",
                0,
                1,
                TaskState.RUNNING,
                null
        );

        this.menus = menus;
    }

    @Override
    public void run() {

        Predicate<? super BasicMenuHolder> filter = holder -> holder.getMenu().getMenuConfigs().repeatingUpdateMills() >= 0 && counter.get() % holder.getMenu().getMenuConfigs().repeatingUpdateMills() == 0;

        List<BasicMenuHolder> allHoldersCopy;

        synchronized (menus.getMenuHolders()) {
            allHoldersCopy = new ArrayList<>(new ArrayList<>(menus.getMenuHolders().values()));
        }
        synchronized (menus.getPlayerMenusMap()) {
            allHoldersCopy.addAll(new ArrayList<>(menus.getPlayerMenusMap().values()));
        }

        allHoldersCopy.stream()
                .filter(filter)
                .forEach(holder -> holder.getMenu().onRepeatingUpdate(holder));


        counter.incrementAndGet();
    }
}
