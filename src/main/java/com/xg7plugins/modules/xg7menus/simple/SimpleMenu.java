package com.xg7plugins.modules.xg7menus.simple;

import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.DragEvent;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.MenuUpdateActions;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleMenu extends Menu {

    private List<InventoryItem> items = new ArrayList<>();

    private Consumer<ActionEvent> clickConsumer = e -> {};
    private Consumer<DragEvent> dragConsumer = e -> {};
    private Consumer<MenuEvent> openEventConsumer = e -> {};
    private Consumer<MenuEvent> closeEventConsumer = e -> {};
    private BiConsumer<BasicMenuHolder, MenuUpdateActions> updateConsumer = (h, a) -> {};
    private Consumer<BasicMenuHolder> repeatingUpdateConsumer = h -> {};

    public SimpleMenu(MenuConfigurations menuConfigs) {
        super(menuConfigs);
    }

    public SimpleMenu(MenuConfigurations menuConfigs, List<InventoryItem> items, Consumer<ActionEvent> clickConsumer, Consumer<DragEvent> dragConsumer, Consumer<MenuEvent> openEventConsumer, Consumer<MenuEvent> closeEventConsumer, BiConsumer<BasicMenuHolder, MenuUpdateActions> updateConsumer, Consumer<BasicMenuHolder> repeatingUpdateConsumer) {
        super(menuConfigs);
        this.items = items;
        this.clickConsumer = clickConsumer;
        this.dragConsumer = dragConsumer;
        this.openEventConsumer = openEventConsumer;
        this.closeEventConsumer = closeEventConsumer;
        this.updateConsumer = updateConsumer;
        this.repeatingUpdateConsumer = repeatingUpdateConsumer;
    }

    @Override
    public List<InventoryItem> getItems(Player player) {
        return items;
    }

    @Override
    public void onClick(ActionEvent event) {
        super.onClick(event);
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
}
