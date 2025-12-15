package com.xg7plugins.modules.xg7menus.builders;

import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.BasicMenuConfigs;
import com.xg7plugins.modules.xg7menus.menus.MenuUpdateActions;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BasicMenuBuilder<B extends BasicMenuBuilder<B,M>, M extends BasicMenu> {

    protected BasicMenuConfigs menuConfigs;

    protected List<InventoryItem> items = new ArrayList<>();

    protected Consumer<ActionEvent> clickConsumer = e -> {};
    protected Consumer<DragEvent> dragConsumer = e -> {};
    protected Consumer<MenuEvent> openEventConsumer = e -> {};
    protected Consumer<MenuEvent> closeEventConsumer = e -> {};
    protected BiConsumer<BasicMenuHolder, MenuUpdateActions> updateAction = (h, a) -> {};
    protected Consumer<BasicMenuHolder> repeatingUpdateAction = h -> {};

    public BasicMenuBuilder(BasicMenuConfigs configs) {
        this.menuConfigs = configs;
    }

    @SuppressWarnings("unchecked")
    public B items(List<InventoryItem> items) {
        this.items = items;
        return (B) this;
    }

    public B items(InventoryItem... items) {
        return items(Arrays.asList(items));
    }

    @SuppressWarnings("unchecked")
    public B onClick(Consumer<ActionEvent> clickEvent) {
        this.clickConsumer = clickEvent;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B onDrag(Consumer<DragEvent> dragEvent) {
        this.dragConsumer = dragEvent;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B onOpen(Consumer<MenuEvent> openEvent) {
        this.openEventConsumer = openEvent;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B onClose(Consumer<MenuEvent> closeEvent) {
        this.closeEventConsumer = closeEvent;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B onUpdate(BiConsumer<BasicMenuHolder, MenuUpdateActions> updateAction) {
        this.updateAction = updateAction;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B onRepeatingUpdate(Consumer<BasicMenuHolder> repeatingUpdateAction) {
        this.repeatingUpdateAction = repeatingUpdateAction;
        return (B) this;
    }

    public abstract M build();
}
