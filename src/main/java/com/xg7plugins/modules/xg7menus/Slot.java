package com.xg7plugins.modules.xg7menus;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class Slot {
    private final int row;
    private final int column;
    public Slot(int row, int column) {
        if (row < 1 || row > 6 || column < 1 || column > 9) throw new MenuException(MenuException.ExceptionCause.SLOT_OUT_OF_BOUNDS, "Inventory coordinate invalid!");
        this.row = row;
        this.column = column;
    }
    public static int get(int row, int column) {
        return 9 * row - (9 - column) - 1;
    }
    public int get() {
        return 9 * row - (9 - column) - 1;
    }
    public static Slot fromSlot(int slot) {
        if (slot == 0) return new Slot(1, 1);
        return new Slot((int) Math.ceil((double) slot / 9), slot % 9 == 0 ? 9 : slot % 9);
    }
    public static Slot of(int row, int column) {
        return new Slot(row, column);
    }
    public static Slot fromList(List<Integer> list) {
        return new Slot(list.get(0), list.get(1));
    }

    public static boolean isInside(Slot start, Slot end, Slot slot) {
        return slot.getRow() >= start.getRow() && slot.getRow() <= end.getRow() && slot.getColumn() >= start.getColumn() && slot.getColumn() <= end.getColumn();
    }
}
