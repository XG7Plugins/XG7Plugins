package com.xg7plugins.temp.xg7menus.events;

import com.xg7plugins.temp.xg7menus.menus.holders.MenuHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;

@Getter
@RequiredArgsConstructor
public class MenuEvent implements Cancellable {

    private final HumanEntity whoClicked;
    private final ClickAction clickAction;
    private final MenuHolder inventoryHolder;
    private boolean cancelled = false;
    //On player menus
    private final Location locationClicked;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }


    public enum ClickAction {
        LEFT,
        DRAG,
        SHIFT_LEFT,
        RIGHT,
        SHIFT_RIGHT,
        WINDOW_BORDER_LEFT,
        WINDOW_BORDER_RIGHT,
        MIDDLE,
        NUMBER_KEY,
        KEYBOARD,
        DOUBLE_CLICK,
        DROP,
        CONTROL_DROP,
        CREATIVE,
        SWAP_OFFHAND,
        UNKNOWN,

        LEFT_CLICK_BLOCK,
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_AIR,
        RIGHT_CLICK_AIR,
        PHYSICAL;
        public boolean isKeyboardClick() {
            return this == NUMBER_KEY || this == DROP || this == CONTROL_DROP || this == KEYBOARD;
        }

        public boolean isCreativeAction() {
            return this == MIDDLE || this == CREATIVE;
        }

        public boolean isRightClick() {
            return this == RIGHT || this == SHIFT_RIGHT || this == RIGHT_CLICK_AIR || this == RIGHT_CLICK_BLOCK;
        }

        public boolean isLeftClick() {
            return this == LEFT || this == SHIFT_LEFT || this == DOUBLE_CLICK || this == CREATIVE || this == LEFT_CLICK_AIR || this == LEFT_CLICK_BLOCK;
        }

        public boolean isShiftClick() {
            return this == SHIFT_LEFT || this == SHIFT_RIGHT || this == CONTROL_DROP;
        }
    }
}
