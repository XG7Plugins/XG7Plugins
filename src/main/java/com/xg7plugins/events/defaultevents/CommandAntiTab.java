package com.xg7plugins.events.defaultevents;

import com.xg7plugins.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.CommandManager;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CommandAntiTab implements Event {

    public CommandAntiTab() {
        Bukkit.getScheduler().runTaskAsynchronously(XG7Plugins.getInstance(), () -> {
            XG7Plugins.getInstance().getLogger().info("Initializing anti-tab...");
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

                    commandPermissions.put(commandName, commandConfig.perm());
                    aliases.forEach(alias -> commandPermissions.put(alias, commandConfig.perm()));
                }
            }
        });
    }

    private final HashMap<String, String> commandPermissions = new HashMap<>();

    @Override
    public boolean isEnabled() {
        return XG7Plugins.getInstance().getConfigsManager().getConfig("config").get("anti-tab");
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
