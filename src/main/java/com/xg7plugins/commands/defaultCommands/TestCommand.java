package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.utils.text.Text;

@Command(
        name = "test",
        description = "test",
        syntax = "test",
        aliasesPath = "test"
)
public class TestCommand implements ICommand {
    @Override
    public ItemBuilder getIcon() {
        return null;
    }

    @Override
    public void onCommand(org.bukkit.command.Command command, org.bukkit.command.CommandSender sender, String label) {
        Text.format("&aCCCCCCCCC", XG7Plugins.getInstance()).send(sender);


    }

}
