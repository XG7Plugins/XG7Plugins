package com.xg7plugins.modules.xg7scores.scores;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerListHeaderAndFooter;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.utils.text.Text;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Tablist extends Score {

    private final AtomicInteger headerIndex = new AtomicInteger(0);
    private final AtomicInteger footerIndex = new AtomicInteger(0);

    private final List<String> header;
    private final List<String> footer;

    private final String playerPrefix;
    private final String playerSuffix;

    public Tablist(long delay, List<String> header, List<String> footer, String playerPrefix, String playerSuffix, String id, Function<Player, Boolean> condition, Plugin plugin) {
        super(delay, header.size() > footer.size() ? header : footer, id, condition, plugin);
        if (XG7Plugins.getMinecraftVersion() < 8) throw new RuntimeException("This version doesn't support Tablist");
        this.header = header;
        this.footer = footer;
        this.playerPrefix = playerPrefix;
        this.playerSuffix = playerSuffix;
    }

    @Override
    public void update() {
        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;
            player.setPlayerListName(Text.detectLangs(player, plugin,playerPrefix).join().getText() + player.getName() + Text.detectLangs(player, plugin,playerSuffix).join().getText());
            String headerl = header.get(headerIndex.getAndIncrement());
            String footerl = footer.get(footerIndex.getAndIncrement());

            if (headerIndex.get() >= header.size()) headerIndex.set(0);
            if (footerIndex.get() >= footer.size()) footerIndex.set(0);

            send(player, Text.detectLangs(player, plugin,headerl).join().getPlainText(), Text.detectLangs(player, plugin,footerl).join().getPlainText());
        }
    }

    @SneakyThrows
    public void send(Player player, String header, String footer) {

        if (header == null) header = "";
        if (footer == null) footer = "";

        if (XG7Plugins.getMinecraftVersion() >= 13) {
            player.setPlayerListHeader(header);
            player.setPlayerListFooter(footer);
            return;
        }

        WrapperPlayServerPlayerListHeaderAndFooter headerComponent = new WrapperPlayServerPlayerListHeaderAndFooter(Component.text(header), Component.text(footer));

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, headerComponent);
    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        send(player,"","");
        if (XG7Plugins.getMinecraftVersion() > 7) player.setPlayerListName(player.getName());

    }

    @Override
    public synchronized void addPlayer(Player player) {
        super.addPlayer(player);
    }
}
