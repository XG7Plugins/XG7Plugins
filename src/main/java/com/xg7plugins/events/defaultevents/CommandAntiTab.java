package com.xg7plugins.events.defaultevents;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.*;

public class CommandAntiTab implements Listener {

    public CommandAntiTab() {
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

                    List<String> aliases = plugin.getConfigsManager().getConfig("commands").get(commandConfig.name(), List.class).orElse(new ArrayList<>());


                    commandPermissions.put(commandName, commandConfig.permission());
                    aliases.forEach(alias -> commandPermissions.put(alias, commandConfig.permission()));
                }
            }
            XG7Plugins.getInstance().getLog().loading("Anti-tab initialized.");
        });
    }

    private final HashMap<String, String> commandPermissions = new HashMap<>();

    @Override
    public boolean isEnabled() {
        return XG7Plugins.getInstance().getConfigsManager().getConfig("config").get("anti-tab", Boolean.class).orElse(false);
    }

    @EventHandler
    public void onTabComplete(PlayerCommandSendEvent event) {
        if (event.getPlayer().hasPermission("xg7plugins.command.anti-tab.bypass")) return;

        for (String command : new ArrayList<>(event.getCommands())) {

            String requiredPerm = commandPermissions.get(command.split(":")[0]);
            if (requiredPerm != null && !requiredPerm.isEmpty() && !event.getPlayer().hasPermission(requiredPerm)) {
                event.getCommands().remove(command);
            }
        }

    }
}
