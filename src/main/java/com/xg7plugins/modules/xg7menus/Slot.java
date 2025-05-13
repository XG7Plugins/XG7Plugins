package com.xg7plugins.modules.xg7menus;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

@Getter
@ToString
public class Slot {
    private final int row;
    private final int column;
    public Slot(int row, int column, boolean ignoreBounds) {
        if ((!ignoreBounds && (row > 6 || column > 9)) || row < 1 || column < 1) throw new MenuException(MenuException.ExceptionCause.SLOT_OUT_OF_BOUNDS, "Inventory coordinate invalid!");
        this.row = row;
        this.column = column;
    }
    public Slot(int row, int column) {
        this(row, column, false);
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
    public static Slot of(int row, int column, boolean ignoreBounds) {
        return new Slot(row, column, ignoreBounds);
    }
    public static Slot fromList(List<Integer> list) {
        return new Slot(list.get(0), list.get(1));
    }

    public static int areaOf(Slot slot1, Slot slot2) {

        int startRow = Math.min(slot1.getRow(), slot2.getRow());
        int finalRow = Math.max(slot1.getRow(), slot2.getRow());
        int startColumn = Math.min(slot1.getColumn(), slot2.getColumn());
        int finalColumn = Math.max(slot1.getColumn(), slot2.getColumn());

        return Math.abs((finalRow - startRow + 1) * (finalColumn - startColumn + 1));
    }

    public static boolean isInside(Slot start, Slot end, Slot slot) {
        return slot.getRow() >= start.getRow() && slot.getRow() <= end.getRow() && slot.getColumn() >= start.getColumn() && slot.getColumn() <= end.getColumn();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return row == slot.row && column == slot.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
