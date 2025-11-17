package com.xg7plugins.modules.xg7menus.item.clickable.impl;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.item.clickable.ClickableItem;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.item.impl.BookItem;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OpenBookClickableItem extends ClickableItem {

    private final List<String> book;
    private final Player player;

    public OpenBookClickableItem(Slot slot, ItemStack stack, Player player, List<String> book) {
        super(stack, slot);

        this.player = player;
        this.book = book;
    }

    @Override
    public void onClick(ActionEvent event) {
        BookItem bookItem = BookItem.newBook();

        List<List<String>> pages = new ArrayList<>();
        List<String> currentPage = new ArrayList<>();

        for (String line : book) {

            currentPage.add(Text.detectLangs(player, XG7Plugins.getInstance(),line).join()
                    .replace("discord", "discord.gg/jfrn8w92kF")
                    .replace("github", "github.com/DaviXG7")
                    .replace("website", "xg7plugins.com")
                    .replace("version", XG7Plugins.getInstance().getDescription().getVersion())
                    .getText());
            if (currentPage.size() == 10) {
                pages.add(new ArrayList<>(currentPage));
                currentPage.clear();
            }
        }
        if (!currentPage.isEmpty()) {
            pages.add(currentPage);
        }

        for (List<String> page : pages) {
            bookItem.addPage(String.join("\n", page));
        }

        player.closeInventory();
        bookItem.openBook(player.getPlayer());
    }

    public static OpenBookClickableItem get(Slot slot, Item item, Player player, List<String> book) {
        return new OpenBookClickableItem(slot, item.getItemStack(), player, book);
    }
}
