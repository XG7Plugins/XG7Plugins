package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

@Command(
        name = "test",
        permission = "xg7plugins.commands.test",
        description = "Test command",
        syntax = "/test",
        isPlayerOnly = true
)
public class TestCommand implements ICommand {
    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Text.format("[ACTION] Test").send(sender);
    }

    @Override
    public Item getIcon() {
        return Item.from(Material.ARROW).lore("asd","asd","asd","asd","asdfliojbhasdpofb");
    }
}
