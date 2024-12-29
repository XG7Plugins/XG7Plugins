package com.xg7plugins.commands.defaultCommands.reloadCommand.subcommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.item.Item;
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
    public void onCommand(CommandSender sender, CommandArgs args) {
        XG7Plugins.getInstance().getJsonManager().invalidateCache();
        Text.format("lang:[reload-message.json]", XG7Plugins.getInstance()).send(sender);
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.PAPER, this);
    }
}
