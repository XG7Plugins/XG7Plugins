package com.xg7plugins.commands.setup;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Command {

    default List<Command> getSubCommands() {
        return new ArrayList<>();
    }

    default void onCommand(CommandSender sender, CommandArgs args) {
        CommandMessages.SYNTAX_ERROR.send(sender, getCommandConfigurations().syntax());
    }

    default List<String> onTabComplete (CommandSender sender, CommandArgs args) {
        return Collections.emptyList();
    }

    Item getIcon();

    default CommandSetup getCommandConfigurations() {
        return getClass().getAnnotation(CommandSetup.class);
    }
    default Plugin getPlugin() {
        return XG7PluginsAPI.getXG7Plugin(getCommandConfigurations().pluginClass());
    }
}
