package com.xg7plugins.modules.xg7menus.item.clickable.impl;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.item.impl.BookItem;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OpenBookClickableItem extends ClickableItem {

    private final List<List<String>> book;
    private final Player player;

    public OpenBookClickableItem(Slot slot, ItemStack stack, Player player, List<List<String>> book) {
        super(stack, slot);

        this.player = player;
        this.book = book;
    }

    @Override
    public void onClick(ActionEvent event) {
        BookItem bookItem = BookItem.newBook();

        for (List<String> lines : book) {
            bookItem.addPage(String.join("", lines));
        }

        player.closeInventory();
        bookItem.openBook(player.getPlayer());
    }

    public static OpenBookClickableItem get(Slot slot, Item item, Player player, List<List<String>> book) {
        return new OpenBookClickableItem(slot, item.getItemStack(), player, book);
    }
}
