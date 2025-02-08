package com.xg7plugins.commands.defaultCommands.reloadCommand.subcommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.cryptomorin.xseries.XMaterial;import com.xg7plugins.temp.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@Command(
        name = "all",
        description = "Reload All Command",
        syntax = "/xg7plugins reload all (plugin)",
        permission = "xg7plugins.command.reload.all",
        isAsync = true
)
public class AllSubCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Plugin plugin = XG7Plugins.getInstance();

        if (args.len() != 0) plugin = XG7Plugins.getInstance().getPlugins().get(args.get(0, String.class));

        if (plugin == XG7Plugins.getInstance()) {
            XG7Plugins.getInstance().getEventManager().unregisterEvents(plugin);
            XG7Plugins.getInstance().getEventManager().registerPlugin(plugin, plugin.loadEvents());
            XG7Plugins.getInstance().getPacketEventManager().unregisterPlugin(plugin);
            XG7Plugins.getInstance().getPacketEventManager().registerPlugin(plugin, plugin.loadPacketEvents());
            XG7Plugins.getInstance().getLangManager().getLangs().clear().join();
            XG7Plugins.getInstance().getLangManager().loadLangsFrom(plugin);
            XG7Plugins.getInstance().getDatabaseManager().disconnectPlugin(plugin);
            XG7Plugins.getInstance().getDatabaseManager().connectPlugin(plugin, plugin.loadEntites());
            plugin.getConfigsManager().reloadConfigs();
            Plugin finalPlugin = plugin;
            Text.formatLang(XG7Plugins.getInstance(),sender,"reload-message.all").thenAccept(text ->
                    text.replace("[PLUGIN]", finalPlugin.getName())
                            .send(sender)
            );
            return;
        }

        Plugin finalPlugin = plugin;
        XG7Plugins.taskManager().runSyncTask(XG7Plugins.getInstance(), () -> {
            XG7Plugins.reload(finalPlugin);
            Text.formatLang(XG7Plugins.getInstance(),sender,"reload-message.all").thenAccept(text ->
                    text.replace("[PLUGIN]", finalPlugin.getName())
                            .send(sender)
            );
        });

    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.ENDER_EYE, this);
    }
}
