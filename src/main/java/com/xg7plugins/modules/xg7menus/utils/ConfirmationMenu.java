package com.xg7plugins.modules.xg7menus.utils;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.MenuException;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.events.MenuEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

public class ConfirmationMenu extends Menu {

    private final Item yesItem;
    private final Item noItem;
    private final Item middleItem;

    private final Consumer<ActionEvent> onYesClick;
    private final Consumer<ActionEvent> onNoClick;

    public ConfirmationMenu(Plugin plugin, String id, String title, Item yesItem, Item noItem, Item middleItem, Consumer<ActionEvent> onYesClick, Consumer<ActionEvent> onNoClick, List<Pair<String,String>> placeholders) {
        super(MenuConfigurations.of(
                plugin,
                id,
                title,
                3,
                EnumSet.noneOf(MenuAction.class),
                true,
                placeholders
        ));
        this.yesItem = yesItem;
        this.noItem = noItem;
        this.middleItem = middleItem;
        this.onYesClick = onYesClick;
        this.onNoClick = onNoClick;
    }


    @Override
    public List<InventoryItem> getItems(Player player) {
        return Arrays.asList(
                yesItem.toInventoryItem(Slot.of(2, 3)),
                middleItem.toInventoryItem(Slot.of(2, 5)),
                noItem.toInventoryItem(Slot.of(2, 7))
        );
    }

    @Override
    public void onClick(ActionEvent event) {
        event.setCancelled(true);
        switch (event.getClickedSlot().get()) {
            case 11:
                if (onYesClick != null) {
                    onYesClick.accept(event);
                }
                return;
            case 15:
                if (onNoClick != null) {
                    onNoClick.accept(event);
                }
                return;
        }
    }

    @Override
    public void onClose(MenuEvent event) {
        event.setCancelled(true);
    }

    @RequiredArgsConstructor
    public static class Builder {

        private final Plugin plugin;
        private final String id;

        private String title;

        private Item yesItem;
        private Item noItem;
        private Item middleItem;

        private Consumer<ActionEvent> onYesClick;
        private Consumer<ActionEvent> onNoClick;

        private List<Pair<String,String>> placeholders = new ArrayList<>();

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder yesItem(Item yesItem) {
            this.yesItem = yesItem;
            return this;
        }

        public Builder noItem(Item noItem) {
            this.noItem = noItem;
            return this;
        }

        public Builder middleItem(Item middleItem) {
            this.middleItem = middleItem;
            return this;
        }

        public Builder onYesClick(Consumer<ActionEvent> onYesClick) {
            this.onYesClick = onYesClick;
            return this;
        }

        public Builder onNoClick(Consumer<ActionEvent> onNoClick) {
            this.onNoClick = onNoClick;
            return this;
        }

        public Builder addBuilderPlaceholders(List<Pair<String,String>> placeholders) {
            this.placeholders.addAll(placeholders);
            return this;
        }

        public ConfirmationMenu build() {
            if (title == null) {
                throw new MenuException(MenuException.ExceptionCause.BUILD_ERROR, "Title is null");
            }

            if (yesItem == null ||  noItem == null ||  middleItem == null) {
                throw new MenuException(MenuException.ExceptionCause.BUILD_ERROR, "Items is null");
            }

            return new ConfirmationMenu(
                    plugin,
                    id,
                    title,
                    yesItem,
                    noItem,
                    middleItem,
                    onYesClick,
                    onNoClick,
                    placeholders
            );
        }

        public static ConfirmationMenu.Builder create(Plugin plugin, String id) {
            return new Builder(plugin, id);
        }


    }
}
