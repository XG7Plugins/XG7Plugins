package com.xg7plugins.commands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7menus.item.Item;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MainCommand implements ICommand {

    private final Plugin plugin;


    @Override
    public Item getIcon() {
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        return XG7Plugins.getInstance().getCommandManager().getCommands().keySet().stream().map(s -> s.replace("xg7plugins", "")).collect(Collectors.toList());
    }
}
