package com.xg7plugins.libs.newxg7menus.events;

import com.xg7plugins.libs.newxg7menus.Menu;
import com.xg7plugins.libs.newxg7menus.item.Item;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;


@Getter
public class ClickEvent extends MenuEvent {

    private final int clickedSlot;
    private final Item clickedItem;

    public ClickEvent(HumanEntity whoClicked, ClickAction clickAction, Menu menu, int clickedSlot, Item clickedItem, Location locationClicked) {
        super(whoClicked, clickAction, menu, locationClicked);
        this.clickedSlot = clickedSlot;
        this.clickedItem = clickedItem;
    }
}
