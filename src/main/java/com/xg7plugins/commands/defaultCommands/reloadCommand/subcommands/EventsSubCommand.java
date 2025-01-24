package com.xg7plugins.commands.defaultCommands.reloadCommand.subcommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.events.packetevents.PacketEventManagerBase;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.libs.xg7menus.item.Item;
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
    public void onCommand(CommandSender sender, CommandArgs args) {
        Plugin plugin = XG7Plugins.getInstance();

        if (args.len() != 0) plugin = XG7Plugins.getInstance().getPlugins().get(args.get(0, String.class));

        EventManager eventManager = XG7Plugins.getInstance().getEventManager();
        eventManager.unregisterEvents(plugin);
        eventManager.registerPlugin(plugin, plugin.loadEvents());

        PacketEventManagerBase base = XG7Plugins.getInstance().getPacketEventManager();

        base.unregisterPlugin(plugin);
        base.registerPlugin(plugin, plugin.loadPacketEvents());

        Plugin finalPlugin = plugin;
        Text.formatLang(XG7Plugins.getInstance(),sender,"reload-message.events").thenAccept(text ->
                text.replace("[PLUGIN]", finalPlugin.getName())
                        .send(sender)
        );
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.EMERALD, this);
    }
}
