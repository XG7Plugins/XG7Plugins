package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@Command(
        name = "test",
        description = "test",
        syntax = "test",
        permission = "xg7plugins.commands.test"
)
public class TestCommand implements ICommand {
    @Override
    public Item getIcon() {
        return null;
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Text.format("lang:[commands.test]", XG7Plugins.getInstance()).send(sender);
    }

}
