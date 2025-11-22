package com.xg7plugins.utils.text.sender;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7scores.scores.ActionBar;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.text.Text;
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
            XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of( () -> ActionBar.removeFromBlacklist(player.getUniqueId())), 3000L);
            return;
        }

        ReflectionClass componentClass = ReflectionClass.of("net.minecraft.server." + MinecraftVersion.getPackageName() + ".ChatComponentText");

        ReflectionObject chatComponentOb = componentClass
                .getConstructor(String.class)
                .newInstance(text.getText());

        ReflectionObject packet = ReflectionClass.of("net.minecraft.server." + MinecraftVersion.getPackageName() + ".PacketPlayOutChat")
                .getConstructor(ReflectionClass.of("net.minecraft.server." + MinecraftVersion.getPackageName() + ".IChatBaseComponent").getAClass(), byte.class)
                .newInstance(chatComponentOb.getObject(), (byte) 2);

        ReflectionObject.of(player)
                .getMethod("getHandle")
                .invokeToRObject()
                .getFieldRObject("playerConnection")
                .getMethod("sendPacket", ReflectionClass.of("net.minecraft.server." + MinecraftVersion.getPackageName() + ".Packet").getAClass())
                .invoke(packet.getObject());

        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of( () -> ActionBar.removeFromBlacklist(player.getUniqueId())), 3000L);

    }

    @Override
    public void apply(CommandSender sender, Text text) {

    }
}
