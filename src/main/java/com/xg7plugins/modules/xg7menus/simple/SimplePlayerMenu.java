package com.xg7plugins.modules.xg7menus.simple;

import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.item.ClickableItem;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.MenuUpdateActions;
import com.xg7plugins.modules.xg7menus.menus.holders.BasicMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menus.player.PlayerMenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.menus.player.PlayerMenu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimplePlayerMenu extends PlayerMenu {

    private List<Item> items = new ArrayList<>();
    private List<ClickableItem> defaultClickableItems = null;

    private Consumer<ActionEvent> clickConsumer = e -> {};
    private Consumer<DragEvent> dragConsumer = e -> {};
    private Consumer<MenuEvent> openEventConsumer = e -> {};
    private Consumer<MenuEvent> closeEventConsumer = e -> {};
    private BiConsumer<BasicMenuHolder, MenuUpdateActions> updateConsumer = (h, a) -> {};
    private Consumer<BasicMenuHolder> repeatingUpdateConsumer = h -> {};

    private Consumer<ActionEvent> dropConsumer = e -> {};
    private Consumer<ActionEvent> pickupConsumer = e -> {};
    private Consumer<ActionEvent> breakBlocksConsumer = e -> {};
    private Consumer<ActionEvent> placeBlocksConsumer = e -> {};

    public SimplePlayerMenu(PlayerMenuConfigurations menuConfigurations) {
        super(menuConfigurations);
    }

    public SimplePlayerMenu(PlayerMenuConfigurations menuConfigs, List<Item> items, List<ClickableItem> defaultClickableItems, Consumer<ActionEvent> clickConsumer, Consumer<DragEvent> dragConsumer, Consumer<MenuEvent> openEventConsumer, Consumer<MenuEvent> closeEventConsumer, BiConsumer<BasicMenuHolder, MenuUpdateActions> updateConsumer, Consumer<BasicMenuHolder> repeatingUpdateConsumer, Consumer<ActionEvent> dropConsumer, Consumer<ActionEvent> pickupConsumer, Consumer<ActionEvent> breakBlocksConsumer, Consumer<ActionEvent> placeBlocksConsumer) {
        super(menuConfigs);
        this.items = items;
        this.defaultClickableItems = defaultClickableItems;
        this.clickConsumer = clickConsumer;
        this.dragConsumer = dragConsumer;
        this.openEventConsumer = openEventConsumer;
        this.closeEventConsumer = closeEventConsumer;
        this.updateConsumer = updateConsumer;
        this.repeatingUpdateConsumer = repeatingUpdateConsumer;
        this.dropConsumer = dropConsumer;
        this.pickupConsumer = pickupConsumer;
        this.breakBlocksConsumer = breakBlocksConsumer;
        this.placeBlocksConsumer = placeBlocksConsumer;
    }

    @Override
    public List<Item> getItems(Player player) {
        return items;
    }

    @Override
    public List<ClickableItem> getDefaultClickableItems() {
        return defaultClickableItems;
    }

    @Override
    public void onClick(ActionEvent event) {
        clickConsumer.accept(event);
    }

    @Override
    public void onDrag(DragEvent event) {
        dragConsumer.accept(event);
    }

    @Override
    public void onOpen(MenuEvent event) {
        openEventConsumer.accept(event);
    }

    @Override
    public void onClose(MenuEvent event) {
        closeEventConsumer.accept(event);
    }

    @Override
    public void onUpdate(BasicMenuHolder holder, MenuUpdateActions actions) {
        updateConsumer.accept(holder, actions);
    }

    @Override
    public void onRepeatingUpdate(BasicMenuHolder holder) {
        repeatingUpdateConsumer.accept(holder);
    }

    @Override
    public void onDrop(ActionEvent event) {
        dropConsumer.accept(event);
    }

    @Override
    public void onPickup(ActionEvent event) {
        pickupConsumer.accept(event);
    }

    @Override
    public void onBreakBlocks(ActionEvent event) {
        breakBlocksConsumer.accept(event);
    }

    @Override
    public void onPlaceBlocks(ActionEvent event) {
        placeBlocksConsumer.accept(event);
    }
}
