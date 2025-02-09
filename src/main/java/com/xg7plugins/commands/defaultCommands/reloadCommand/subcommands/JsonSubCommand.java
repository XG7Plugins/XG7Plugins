package com.xg7plugins.commands.defaultCommands.reloadCommand.subcommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@Command(
        name = "jsoncache",
        description = "Reload Json Cache Command",
        syntax = "/xg7plugins reload jsoncache",
        permission = "xg7plugins.command.reload.json",
        isAsync = true
)
public class JsonSubCommand implements ICommand {

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        XG7Plugins.getInstance().getJsonManager().invalidateCache();
        Text.fromLang(sender,XG7Plugins.getInstance(),"reload-message.json").thenAccept(text ->
                text.send(sender)
        );
    }

//    @Override
//    public Item getIcon() {
//        return Item.commandIcon(XMaterial.PAPER, this);
//    }
}
