package com.xg7plugins.modules.xg7menus.newMenuSystemAgain;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.item.Item;

import java.util.List;

public interface IBaseMenu {
    Plugin getPlugin();

    List<Item> getDefaultItems();

    void open();
    void close();

    default void onClick() {

    }
    default void onDrag() {

    }
    default void onOpen() {

    }
    default void onClose() {

    }

    static void refresh() {

    }
    static void update() {

    }
}
