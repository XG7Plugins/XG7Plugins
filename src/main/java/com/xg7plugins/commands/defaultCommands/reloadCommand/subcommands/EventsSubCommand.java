package com.xg7plugins.commands.defaultCommands.reloadCommand.subcommands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@Command(
        name = "events",
        description = "Reload Events Command",
        syntax = "/xg7plugins reload events (plugin)",
        permission = "xg7plugins.command.reload.events",
        isAsync = true
)
public class EventsSubCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Plugin plugin = XG7Plugins.getInstance();

        if (args.len() != 0) plugin = XG7Plugins.getInstance().getPlugins().get(args.get(0, String.class));

        EventManager eventManager = XG7Plugins.getInstance().getEventManager();
        eventManager.unregisterListeners(plugin);
        eventManager.registerListeners(plugin, plugin.loadEvents());

        PacketEventManager packetEventManager = XG7Plugins.getInstance().getPacketEventManager();

        packetEventManager.unregisterListeners(plugin);
        packetEventManager.registerListeners(plugin, plugin.loadPacketEvents());

        Plugin finalPlugin = plugin;
        Text.fromLang(sender,XG7Plugins.getInstance(),"reload-message.events").thenAccept(text ->
                text.replace("[PLUGIN]", finalPlugin.getName())
                        .send(sender)
        );
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.EMERALD, this);
    }
}
