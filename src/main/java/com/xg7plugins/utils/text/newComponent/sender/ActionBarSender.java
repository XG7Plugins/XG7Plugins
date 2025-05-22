package com.xg7plugins.utils.text.newComponent.sender;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessageLegacy;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7scores.scores.ActionBar;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.text.newComponent.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ActionBarSender implements TextSender {
    @Override
    public void send(CommandSender sender, TextComponent component) {
        if (MinecraftVersion.isOlderThan(8) || !(sender instanceof Player) ) {
            defaultSend(sender, component);
            return;
        }

        Player player = (Player) sender;

        ActionBar.addToBlacklist(player);

        if (MinecraftVersion.isNewerThan(8)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(component.getText()));
            Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> ActionBar.removeFromBlacklist(player.getUniqueId()), 60L);
            return;
        }

        WrapperPlayServerChatMessage packetPlayOutChat = new WrapperPlayServerChatMessage(
                new ChatMessageLegacy(net.kyori.adventure.text.Component.text(component.getText()), ChatTypes.GAME_INFO)
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packetPlayOutChat);

        Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> ActionBar.removeFromBlacklist(player.getUniqueId()), 60L);

        return;
    }
}
