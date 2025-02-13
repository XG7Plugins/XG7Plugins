package com.xg7plugins.commands.defaultCommands.reloadCommand.subcommands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.database.DatabaseManager;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@Command(
        name = "database",
        description = "Reload Database Command",
        syntax = "/xg7plugins reload database (plugin)",
        permission = "xg7plugins.command.reload.database",
        isAsync = true
)
public class DatabaseSubCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Plugin plugin = XG7Plugins.getInstance();

        if (args.len() != 0) plugin = XG7Plugins.getInstance().getPlugins().get(args.get(0, String.class));

        DatabaseManager dbManager = XG7Plugins.getInstance().getDatabaseManager();

        dbManager.disconnectPlugin(plugin);
        dbManager.connectPlugin(plugin, plugin.loadEntites());

        Plugin finalPlugin = plugin;
        Text.fromLang(sender,XG7Plugins.getInstance(),"reload-message.database").thenAccept(text ->
                text.replace("[PLUGIN]", finalPlugin.getName())
                        .send(sender)
        );
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.CHEST, this);
    }
}
