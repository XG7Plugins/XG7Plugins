package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.temp.xg7menus.item.Item;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        Text.format("Test").sendActionBar((Player) sender);
    }

    @Override
    public Item getIcon() {
        return null;
    }
}
