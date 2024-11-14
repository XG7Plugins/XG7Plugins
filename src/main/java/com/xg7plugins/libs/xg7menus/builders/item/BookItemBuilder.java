package com.xg7plugins.libs.xg7menus.builders.item;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.MenuException;
import com.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.utils.reflection.PlayerNMS;
import com.xg7plugins.utils.reflection.ReflectionObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

public class BookItemBuilder extends BaseItemBuilder<BookItemBuilder> {
    public BookItemBuilder(Plugin plugin) {
        super(new ItemStack(Material.WRITTEN_BOOK),plugin);
        title("Blank");
        author("None");
    }
    public BookItemBuilder(ItemStack book,Plugin plugin) {
        super(book,plugin);
        title("Blank");
        author("None");
    }

    public static @NotNull BookItemBuilder from(@NotNull ItemStack book,Plugin plugin) {
        if (!book.getType().equals(Material.WRITTEN_BOOK)) throw new MenuException("This item isn't a writable book!");
        return new BookItemBuilder(book,plugin);
    }
    public static @NotNull BookItemBuilder builder(Plugin plugin) {
        return new BookItemBuilder(plugin);
    }
    public BookItemBuilder title(String title) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.setTitle(title);
        super.meta(meta);
        return this;
    }
    public BookItemBuilder author(String author) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.setAuthor(author);
        super.meta(meta);
        return this;
    }
    public BookItemBuilder addPage(String text) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.addPage(text);
        super.meta(meta);
        return this;
    }
    public BookItemBuilder addPage(BaseComponent[] components) {

        try {
            BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
            meta.spigot().addPage(components);
            super.meta(meta);
            return this;
        } catch (Exception ignored) {
            if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", "")) < 8) {
                XG7Plugins.getInstance().getLog().warn("Books with base component is not supported on this version!");
                return this;
            }
        }

        return this;
    }

    @SneakyThrows
    public void openBook(Player player) {

        if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", "")) > 13) {
            player.openBook(this.itemStack);
            return;
        }

        if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", "")) < 8) {
            XG7Plugins.getInstance().getLog().warn("Books is not supported on version under of 1.8!");
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, this.itemStack);

        ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, 0);
        buf.writerIndex(1);

        ReflectionObject packet = NMSUtil.getNMSClass("PacketPlayOutCustomPayload")
                .getConstructor(String.class, NMSUtil.getNMSClass("PacketDataSerializer").getAClass())
                .newInstance("MC|BOpen", NMSUtil.getNMSClass("PacketDataSerializer").getConstructor(ByteBuf.class).newInstance(buf).getObject());

        PlayerNMS.cast(player).sendPacket(packet.getObject());

        player.getInventory().setItem(slot, old);
    }
}
