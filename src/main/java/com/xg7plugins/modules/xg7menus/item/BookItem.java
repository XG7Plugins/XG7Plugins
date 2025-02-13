package com.xg7plugins.modules.xg7menus.item;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenBook;
import com.xg7plugins.XG7Plugins;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookItem extends Item {


    public BookItem() {
        super(new ItemStack(Material.WRITTEN_BOOK));
        title("Blank");
        author("None");
    }
    public BookItem(ItemStack book) {
        super(book);
        if (!book.getType().equals(Material.WRITTEN_BOOK)) throw new IllegalArgumentException("This item isn't a writable book!");
        title("Blank");
        author("None");
    }

    public static BookItem newBook() {
        return new BookItem();
    }

    public static BookItem from(ItemStack book) {
        return new BookItem(book);
    }

    public BookItem title(String title) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.setTitle(title);
        super.meta(meta);
        return this;
    }
    public BookItem author(String author) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.setAuthor(author);
        super.meta(meta);
        return this;
    }
    public BookItem addPage(String text) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.addPage(text);
        super.meta(meta);
        return this;
    }

    @SneakyThrows
    public void openBook(Player player) {

        if (XG7Plugins.getMinecraftVersion() > 13) {
            player.openBook(this.itemStack);
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, this.itemStack);

        WrapperPlayServerOpenBook wrapper = new WrapperPlayServerOpenBook(InteractionHand.MAIN_HAND);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper);

        player.getInventory().setItem(slot, old);
    }
}
