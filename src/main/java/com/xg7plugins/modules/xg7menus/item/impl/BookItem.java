package com.xg7plugins.modules.xg7menus.item.impl;

import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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

        if (MinecraftVersion.isNewerThan(13)) {
            player.openBook(this.itemStack);
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, this.itemStack);

        ReflectionClass packetDataSerializerClass = ReflectionClass.of("net.minecraft.server." + MinecraftVersion.getPackageName() + ".PacketDataSerializer");

        ReflectionObject byteBuf = MinecraftVersion.isOlderOrEqual(7) ? ReflectionClass.of("net.minecraft.util.io.netty.buffer")
                .getMethod("buffer", int.class).invoke(256) : ReflectionObject.of(Unpooled.buffer(256));

        byteBuf.getMethod("setByte", int.class, int.class).invoke(0, 0);
        byteBuf.getMethod("writerIndex", int.class).invoke(1);

        ReflectionObject packetPlayOutCustomPayloadClass = ReflectionClass.of("net.minecraft.server." + MinecraftVersion.getPackageName() + ".PacketPlayOutCustomPayload")
                .getConstructor(String.class, packetDataSerializerClass.getAClass())
                .newInstance(
                        "MC|BOpen",
                        packetDataSerializerClass
                        .getConstructor(MinecraftVersion.isOlderOrEqual(7) ? ReflectionClass.of("net.minecraft.util.io.netty.buffer.ByteBuf").getAClass() : ByteBuf.class)
                        .newInstance(byteBuf.getObject()).getObject()
                );

        ReflectionObject nmsPlayer = ReflectionObject.of(player);

        ReflectionObject.of(nmsPlayer.getMethod("getHandle").invokeToRObject().getField("playerConnection")).getMethod("sendPacket", ReflectionClass.of("net.minecraft.server." + MinecraftVersion.getPackageName() + ".Packet").getAClass()).invoke(packetPlayOutCustomPayloadClass.getObject());

        player.getInventory().setItem(slot, old);
    }
}
