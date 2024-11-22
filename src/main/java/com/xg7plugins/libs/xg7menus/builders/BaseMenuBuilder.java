package com.xg7plugins.libs.xg7menus.builders;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7menus.BaseMenu;
import com.xg7plugins.libs.xg7menus.MenuPermissions;
import com.xg7plugins.libs.xg7menus.builders.menu.MenuBuilder;
import com.xg7plugins.libs.xg7menus.builders.menu.PageMenuBuilder;
import com.xg7plugins.libs.xg7menus.builders.menu.PlayerMenuBuilder;
import com.xg7plugins.libs.xg7menus.builders.menu.StorageMenuBuilder;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.utils.Builder;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class BaseMenuBuilder<M,B> extends Builder<M> {

    protected Map<Integer, BaseItemBuilder<? extends BaseItemBuilder>> items = new HashMap<>();
    protected Map<Integer,Consumer<ClickEvent>> clickEventMap = new HashMap<>();
    protected Consumer<ClickEvent> defaultClickEvent;
    protected Consumer<MenuEvent> openMenuEvent;
    protected Consumer<MenuEvent> closeMenuEvent;
    protected EnumSet<MenuPermissions> allowedPermissions = EnumSet.noneOf(MenuPermissions.class);

    @Getter
    protected String id;

    public BaseMenuBuilder(String id) {
        this.id = id;
    }

    public B setItems(Map<Integer, BaseItemBuilder<? extends BaseItemBuilder>> items) {
        this.items = items;
        return (B) this;
    }
    public B setItem(int slot, BaseItemBuilder<? extends BaseItemBuilder> itemBuilder) {
        this.items.put(slot, itemBuilder);
        if (itemBuilder.getEvent() != null) this.clickEventMap.put(slot, itemBuilder.getEvent());
        return (B) this;
    }
    public B setClickEvent(int slot, Consumer<ClickEvent> clickEvent) {
        this.clickEventMap.put(slot,clickEvent);
        return (B) this;
    }
    public B setDefaultClickEvent(Consumer<ClickEvent> defaultClickEvent) {
        this.defaultClickEvent = defaultClickEvent;
        return (B) this;
    }
    public B setOpenMenuEvent(Consumer<MenuEvent> openMenuEvent) {
        this.openMenuEvent = openMenuEvent;
        return (B) this;
    }
    public B setCloseMenuEvent(Consumer<MenuEvent> closeMenuEvent) {
        this.closeMenuEvent = closeMenuEvent;
        return (B) this;
    }
    public B deny(MenuPermissions... permissions) {
        allowedPermissions.removeAll(Arrays.asList(permissions));
        return (B) this;
    }
    public B allow(MenuPermissions... permissions) {
        allowedPermissions.addAll(Arrays.asList(permissions));
        return (B) this;
    }

    public static @NotNull MenuBuilder gui(String id) {
        return new MenuBuilder(id);
    }
    public static @NotNull StorageMenuBuilder storage(String id) {
        return new StorageMenuBuilder(id);
    }
    public static @NotNull PageMenuBuilder page(String id) {
        return new PageMenuBuilder(id);
    }
    public static @NotNull PlayerMenuBuilder player(String id) {
        return new PlayerMenuBuilder(id);
    }


}
