package com.xg7plugins.utils.item.impl;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.text.Text;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if (MinecraftServerVersion.isNewerOrEqual(ServerVersion.V_1_13)) {
            player.openBook(this.itemStack);
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, this.itemStack);

        ReflectionClass packetDataSerializerClass = ReflectionClass.of("net.minecraft.server." + MinecraftServerVersion.getPackageName() + ".PacketDataSerializer");

        ReflectionObject byteBuf = MinecraftServerVersion.isOlderOrEqual(ServerVersion.V_1_7_10) ? ReflectionClass.of("net.minecraft.util.io.netty.buffer")
                .getMethod("buffer", int.class).invoke(256) : ReflectionObject.of(Unpooled.buffer(256));

        byteBuf.getMethod("setByte", int.class, int.class).invoke(0, 0);
        byteBuf.getMethod("writerIndex", int.class).invoke(1);

        ReflectionObject packetPlayOutCustomPayloadClass = ReflectionClass.of("net.minecraft.server." + MinecraftServerVersion.getPackageName() + ".PacketPlayOutCustomPayload")
                .getConstructor(String.class, packetDataSerializerClass.getAClass())
                .newInstance(
                        "MC|BOpen",
                        packetDataSerializerClass
                        .getConstructor(MinecraftServerVersion.isOlderOrEqual(ServerVersion.V_1_7_10) ? ReflectionClass.of("net.minecraft.util.io.netty.buffer.ByteBuf").getAClass() : ByteBuf.class)
                        .newInstance(byteBuf.getObject()).getObject()
                );

        ReflectionObject nmsPlayer = ReflectionObject.of(player);

        ReflectionObject.of(nmsPlayer.getMethod("getHandle").invokeToRObject().getField("playerConnection")).getMethod("sendPacket", ReflectionClass.of("net.minecraft.server." + MinecraftServerVersion.getPackageName() + ".Packet").getAClass()).invoke(packetPlayOutCustomPayloadClass.getObject());

        player.getInventory().setItem(slot, old);
    }

    public static List<List<String>> convertTextToBookPages(Text text) {

        final int MAX_WIDTH = 114;
        final int MAX_LINES = 14;

        List<List<String>> pages = new ArrayList<>();

        String rawText = text.getText();

        String[] rawPages = rawText.split("<endp>");

        Pattern tokenPattern = Pattern.compile("(\\s+|\\S+)");

        for (String rawPage : rawPages) {

            List<String> lines = new ArrayList<>();

            String[] baseLines = rawPage.split("<br>");

            for (String baseLine : baseLines) {

                // linha vazia
                if (baseLine.isEmpty()) {
                    lines.add("");
                    continue;
                }

                Matcher matcher = tokenPattern.matcher(baseLine);
                StringBuilder currentLine = new StringBuilder();

                while (matcher.find()) {
                    String token = matcher.group();

                    String testLine = currentLine + token;

                    if (Text.getTextWidth(testLine) <= MAX_WIDTH) {
                        currentLine.append(token);
                    } else {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder(token.trim().isEmpty() ? "" : token);
                    }

                    if (lines.size() == MAX_LINES) {
                        pages.add(new ArrayList<>(lines));
                        lines.clear();
                    }
                }

                if (currentLine.length() != 0) {
                    lines.add(currentLine.toString());
                }

                if (lines.size() == MAX_LINES) {
                    pages.add(new ArrayList<>(lines));
                    lines.clear();
                }
            }

            if (!lines.isEmpty()) {
                pages.add(new ArrayList<>(lines));
            }
        }

        return pages;
    }





}
