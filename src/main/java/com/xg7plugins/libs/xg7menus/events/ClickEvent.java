package com.xg7plugins.libs.xg7menus.events;

import com.xg7plugins.libs.xg7menus.BaseMenu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public class ClickEvent implements Cancellable {

    private boolean cancelled = true;
    private final Player whoClicked;
    private final ClickAction clickAction;
    private final int clickedSlot;
    private final ItemStack clickedItem;
    private final BaseMenu clickedMenu;
    //On player menus
    private final Location locationClicked;


    public <T extends BaseMenu> T getClickedMenu() {
        return (T) clickedMenu;
    }


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
            return this == NUMBER_KEY || this == DROP || this == CONTROL_DROP;
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
