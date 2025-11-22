package com.xg7plugins.modules.xg7scores.scores;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
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

            if (containsPlayerInBlacklist(id)) continue;


            String message = Text.detectLangs(player, plugin, super.updateText.get(indexUpdating)).join().getText();

            if (MinecraftVersion.isNewerThan(8)) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                continue;
            }

            ReflectionClass componentClass = ReflectionClass.of("net.minecraft.server." + MinecraftVersion.getPackageName() + ".ChatComponentText");

            ReflectionObject chatComponentOb = componentClass
                    .getConstructor(String.class)
                    .newInstance(message);

            ReflectionObject packet = ReflectionClass.of("net.minecraft.server." + MinecraftVersion.getPackageName() + ".PacketPlayOutChat")
                    .getConstructor(ReflectionClass.of("net.minecraft.server." + MinecraftVersion.getPackageName() + ".IChatBaseComponent").getAClass(), byte.class)
                    .newInstance(chatComponentOb.getObject(), (byte) 2);

            ReflectionObject.of(player)
                    .getMethod("getHandle")
                    .invokeToRObject()
                    .getFieldRObject("playerConnection")
                    .getMethod("sendPacket", ReflectionClass.of("net.minecraft.server." + MinecraftVersion.getPackageName() + ".Packet").getAClass())
                    .invoke(packet.getObject());

        }
    }

    public static boolean containsPlayerInBlacklist(UUID id) {
        return sendActionBlackList.contains(id);
    }
    public static void addToBlacklist(Player player) {
        sendActionBlackList.add(player.getUniqueId());
    }
    public static void removeFromBlacklist(UUID id) {
        sendActionBlackList.removeIf(id::equals);
    }



}
