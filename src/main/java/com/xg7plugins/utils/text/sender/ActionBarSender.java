package com.xg7plugins.utils.text.sender;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7scores.scores.ActionBar;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.text.Text;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ActionBarSender implements TextSender {
    @Override
    public void send(CommandSender sender, Text text) {
        if (text == null || text.getText() == null || text.getText().isEmpty()) return;
        if (MinecraftServerVersion.isOlderThan(ServerVersion.V_1_8) || !(sender instanceof Player) ) {
            defaultSend(sender, text);
            return;
        }

        Player player = (Player) sender;

        ActionBar.addToBlacklist(
                player,
                Bukkit.getScheduler().runTaskLater(
                                XG7Plugins.getInstance().getJavaPlugin(),
                                () -> ActionBar.removeFromBlacklist(player.getUniqueId()),
                                ConfigFile.mainConfigOf(XG7Plugins.getInstance()).root().getTimeInTicks("cooldown-for-showing-action-messages")
                        )
                        .getTaskId()
        );

        if (MinecraftServerVersion.isNewerThan(ServerVersion.V_1_8_8)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text.getText()));
            return;
        }

        ReflectionClass componentClass = ReflectionClass.of("net.minecraft.server." + MinecraftServerVersion.getPackageName() + ".ChatComponentText");

        ReflectionObject chatComponentOb = componentClass
                .getConstructor(String.class)
                .newInstance(text.getText());

        ReflectionObject packet = ReflectionClass.of("net.minecraft.server." + MinecraftServerVersion.getPackageName() + ".PacketPlayOutChat")
                .getConstructor(ReflectionClass.of("net.minecraft.server." + MinecraftServerVersion.getPackageName() + ".IChatBaseComponent").getAClass(), byte.class)
                .newInstance(chatComponentOb.getObject(), (byte) 2);

        ReflectionObject.of(player)
                .getMethod("getHandle")
                .invokeToRObject()
                .getFieldRObject("playerConnection")
                .getMethod("sendPacket", ReflectionClass.of("net.minecraft.server." + MinecraftServerVersion.getPackageName() + ".Packet").getAClass())
                .invoke(packet.getObject());

    }

    @Override
    public void apply(CommandSender sender, Text text) {

    }
}
