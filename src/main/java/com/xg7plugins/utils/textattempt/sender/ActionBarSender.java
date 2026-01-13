package com.xg7plugins.utils.textattempt.sender;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7scores.scores.ActionBar;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.textattempt.Text;
import net.md_5.bungee.api.ChatMessageType;
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

        ActionBar.addToBlacklist(player);

        XG7Plugins.getAPI().getAdventure().sender(player).sendActionBar(text.getComponent());

        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of( () -> ActionBar.removeFromBlacklist(player.getUniqueId())), 3000L);

    }

    @Override
    public void apply(CommandSender sender, Text text) {

    }
}
