package com.xg7plugins.libs.xg7menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.libs.xg7menus.menus.player.PlayerMenu;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public abstract class BaseMenu {

    protected Map<Integer, ItemStack> items;
    protected Map<Integer, Consumer<ClickEvent>> clickEvents;
    protected Consumer<ClickEvent> defaultClickEvent;
    protected Consumer<MenuEvent> openEvent;
    protected Consumer<MenuEvent> closeEvent;
    protected EnumSet<MenuPermissions> permissions;
    protected HumanEntity player;

    public BaseMenu(
            String id,
            Consumer<ClickEvent> defaultClickEvent,
            Consumer<MenuEvent> openEvent,
            Consumer<MenuEvent> closeEvent,
            Map<Integer,ItemStack> items,
            Map<Integer,Consumer<ClickEvent>> clickEvents,
            EnumSet<MenuPermissions> permissions,
            HumanEntity player
    )
    {
        this.items = items;
        this.clickEvents = clickEvents;
        this.defaultClickEvent = defaultClickEvent;
        this.openEvent = openEvent;
        this.closeEvent = closeEvent;
        this.permissions = permissions;
        this.player = player;

        if (this instanceof PlayerMenu) return;

        XG7Plugins.getInstance().getMenuManager().getCachedMenus().put(id + ":" + player.getUniqueId(), this);
    }

}
