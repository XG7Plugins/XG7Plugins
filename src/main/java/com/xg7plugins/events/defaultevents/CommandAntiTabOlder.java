package com.xg7plugins.events.defaultevents;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.PacketEvent;
import com.xg7plugins.events.packetevents.PacketEventHandler;
import com.xg7plugins.utils.reflection.ReflectionObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CommandAntiTabOlder implements Event, PacketEvent {
    private final HashMap<String, String> commandPermissions = new HashMap<>();

    public CommandAntiTabOlder() {
        Bukkit.getScheduler().runTaskAsynchronously(XG7Plugins.getInstance(), () -> {
            XG7Plugins.getInstance().getLog().loading("Initializing anti-tab...");
            HashMap<String, Plugin> plugins = new HashMap<>(XG7Plugins.getInstance().getPlugins());
            plugins.put("XG7Plugins", XG7Plugins.getInstance());

            for (Plugin plugin : plugins.values()) {
                CommandManager manager = plugin.getCommandManager();

                for (Map.Entry<String, ICommand> entry : manager.getCommands().entrySet()) {
                    String commandName = entry.getKey();
                    ICommand iCommand = entry.getValue();
                    Command commandConfig = iCommand.getClass().getAnnotation(Command.class);

                    String aliasConfig = plugin.getConfigsManager().getConfig("commands").get(commandConfig.aliasesPath()).toString();
                    List<String> aliases = Arrays.asList(aliasConfig.split(", "));

                    commandPermissions.put("/" + commandName, commandConfig.perm());
                    aliases.forEach(alias -> commandPermissions.put("/" + alias, commandConfig.perm()));
                }
            }
            XG7Plugins.getInstance().getLog().loading("Anti-tab initialized.");
        });

    }

    @Override
    public boolean isEnabled() {
        return XG7Plugins.getInstance().getConfigsManager().getConfig("config").get("anti-tab");
    }


    @PacketEventHandler(
            packet = "PacketPlayOutTabComplete"
    )
    public Object onTabComplete(Player player, ReflectionObject packet) {
        try {
            if (player.hasPermission("xg7plugins.command.anti-tab.bypass")) {
                return packet.getObject();
            }

            List<String> suggestions = new ArrayList<>(Arrays.stream((String[]) packet.getField("a")).collect(Collectors.toList()));

            List<String> filteredSuggestions = suggestions.stream()
                    .filter(command -> {
                        String requiredPerm = commandPermissions.get(command.split(":")[0]);
                        return requiredPerm == null || requiredPerm.isEmpty() || player.hasPermission(requiredPerm);
                    })
                    .collect(Collectors.toList());

            packet.setField("a", filteredSuggestions.toArray(new String[0]));
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return packet.getObject();
    }


}
