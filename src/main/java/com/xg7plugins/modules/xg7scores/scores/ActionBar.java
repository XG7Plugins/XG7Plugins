package com.xg7plugins.modules.xg7scores.scores;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessageLegacy;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Getter
public class ActionBar extends Score {

    private static final List<UUID> sendActionBlackList = new ArrayList<>();

    public ActionBar(long delay, List<String> text, String id, Function<Player, Boolean> condition, Plugin plugin) {
        super(delay, text, id, condition, plugin);
        if (MinecraftVersion.isOlderThan(8)) throw new RuntimeException("This version doesn't support ActionBar");
    }

    @Override
    public void update() {
        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;
            if (containsPlayer(id)) continue;

            String message = Text.detectLangs(player, plugin, super.updateText.get(indexUpdating)).join().getPlainText();

            if (MinecraftVersion.isNewerThan(8)) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                return;
            }

            WrapperPlayServerChatMessage packetPlayOutChat = new WrapperPlayServerChatMessage(new ChatMessageLegacy(net.kyori.adventure.text.Component.text(message), ChatTypes.GAME_INFO));

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packetPlayOutChat);

        }
    }

    public static boolean containsPlayer(UUID id) {
        return sendActionBlackList.contains(id);
    }
    public static void addToBlacklist(Player player) {
        sendActionBlackList.add(player.getUniqueId());
    }
    public static void removeFromBlacklist(UUID id) {
        sendActionBlackList.removeIf(id::equals);
    }



}
