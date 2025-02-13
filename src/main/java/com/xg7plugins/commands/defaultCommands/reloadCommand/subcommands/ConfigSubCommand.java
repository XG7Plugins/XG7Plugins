package com.xg7plugins.commands.defaultCommands.reloadCommand.subcommands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@Command(
        name = "config",
        description = "Reload config Command",
        syntax = "/xg7plugins reload config (plugin)",
        permission = "xg7plugins.command.reload.config",
        isAsync = true
)
public class ConfigSubCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Plugin plugin = XG7Plugins.getInstance();

        if (args.len() != 0) plugin = XG7Plugins.getInstance().getPlugins().get(args.get(0, String.class));

        plugin.getConfigsManager().reloadConfigs();

        Plugin finalPlugin = plugin;
        Text.fromLang(sender,XG7Plugins.getInstance(),"reload-message.config").thenAccept(text ->
                text.replace("[PLUGIN]", finalPlugin.getName())
                        .send(sender)
        );
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.REDSTONE, this);
    }
}
