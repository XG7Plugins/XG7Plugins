package com.xg7plugins.utils.text.sender;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessageLegacy;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.modules.xg7scores.scores.ActionBar;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ActionBarSender implements TextSender {
    @Override
    public void send(CommandSender sender, Text text) {
        if (text == null || text.getText() == null || text.getText().isEmpty()) return;
        if (MinecraftVersion.isOlderThan(8) || !(sender instanceof Player) ) {
            defaultSend(sender, text);
            return;
        }

        Player player = (Player) sender;

        ActionBar.addToBlacklist(player);

        if (MinecraftVersion.isNewerThan(8)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, text.getComponent());
            XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of(XG7Plugins.getInstance(), () -> ActionBar.removeFromBlacklist(player.getUniqueId())), 60L);
            return;
        }


        WrapperPlayServerChatMessage packetPlayOutChat = new WrapperPlayServerChatMessage(
                new ChatMessageLegacy(Component.text(text.getText()), ChatTypes.GAME_INFO)
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packetPlayOutChat);


        XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of(XG7Plugins.getInstance(), () -> ActionBar.removeFromBlacklist(player.getUniqueId())), 60L);

    }

    @Override
    public void apply(CommandSender sender, Text text) {

    }
}
