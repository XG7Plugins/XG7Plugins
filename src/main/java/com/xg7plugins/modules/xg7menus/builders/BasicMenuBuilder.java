package com.xg7plugins.modules.xg7menus.builders;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.item.ClickableItem;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.IBasicMenu;
import com.xg7plugins.modules.xg7menus.menus.IBasicMenuConfigs;
import com.xg7plugins.modules.xg7menus.menus.MenuUpdateActions;
import com.xg7plugins.modules.xg7menus.menus.holders.BasicMenuHolder;
import com.xg7plugins.utils.Builder;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BasicMenuBuilder<B extends BasicMenuBuilder<B,M>, M extends IBasicMenu> implements Builder<M> {

    protected IBasicMenuConfigs menuConfigs;

    protected List<Item> items = new ArrayList<>();
    protected List<ClickableItem> clickableItems = null;

    protected Consumer<ActionEvent> clickConsumer = e -> {};
    protected Consumer<DragEvent> dragConsumer = e -> {};
    protected Consumer<MenuEvent> openEventConsumer = e -> {};
    protected Consumer<MenuEvent> closeEventConsumer = e -> {};
    protected BiConsumer<BasicMenuHolder, MenuUpdateActions> updateAction = (h, a) -> {};
    protected Consumer<BasicMenuHolder> repeatingUpdateAction = h -> {};
    public BasicMenuBuilder(IBasicMenuConfigs configs) {
        this.menuConfigs = configs;
    }

    @SuppressWarnings("unchecked")
    public B items(List<Item> items) {
        List<Item> itemList = new ArrayList<>();
        List<ClickableItem> clickableItemList = new ArrayList<>();

        for (Item item : items) {
            if (item instanceof ClickableItem) {
                clickableItemList.add((ClickableItem) item);
                continue;
            }
            itemList.add(item);
        }
        this.items = itemList;
        this.clickableItems = clickableItemList;
        return (B) this;
    }

    public B items(Item... items) {
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

    @Override
    public abstract M build(Object... args);
}
