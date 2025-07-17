package com.xg7plugins.commands;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTabComplete;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.events.packetevents.PacketListenerSetup;
import com.xg7plugins.events.packetevents.PacketEventType;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Anti-tab system for commands of XG7plugins
 * <p>
 * This class filters the commands shown to the player,
 * based on their permissions
 */
@AllArgsConstructor
@PacketListenerSetup(packet = PacketEventType.PLAY_SERVER_TAB_COMPLETE)
public class AntiTab implements PacketListener {

    private final CommandManager commandManager;

    @Override
    public void onPacketSend(PacketSendEvent event) {

        Player player = event.getPlayer();
        WrapperPlayServerTabComplete packet = new WrapperPlayServerTabComplete(event);

        List<WrapperPlayServerTabComplete.CommandMatch> suggestions = packet.getCommandMatches();

        List<WrapperPlayServerTabComplete.CommandMatch> filtered = suggestions.stream().filter(suggestion -> {
            String command = suggestion.getText();

            String label = command.startsWith("/") ? command.substring(1) : command;

            label = label.contains(" ") ? label.split(" ")[0] : label;
            label = label.contains(":") ? label.split(":")[0] : label;

            if (!commandManager.getMappedCommands().containsKey(label)) return true;

            String permission = commandManager.getMappedCommands().get(label).getCommandSetup().permission();

            return permission == null || permission.isEmpty()
                    || player.hasPermission(permission)
                    || player.hasPermission("xg7plugins.command.anti-tab-bypass");

        }).collect(Collectors.toList());

        packet.setCommandMatches(filtered);


    }

    @Override
    public boolean isEnabled() {
        return Config.mainConfigOf(XG7Plugins.getInstance()).get("anti-tab", Boolean.class).orElse(false);
    }
}
