package com.xg7plugins.commands.setup;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public interface Command {

    default Command[] getSubCommands() {
        return new Command[0];
    }

    default void onCommand(CommandSender sender, CommandArgs args) {
        CommandMessages.SYNTAX_ERROR.send(sender, getCommandsConfigurations().syntax());
    }

    default List<String> onTabComplete (CommandSender sender, CommandArgs args) {
        return Collections.emptyList();
    }

    Item getIcon();

    default CommandSetup getCommandsConfigurations() {
        return this.getClass().getAnnotation(CommandSetup.class);
    }
    default Plugin getPlugin() {
        return XG7Plugins.getXG7Plugin(getCommandsConfigurations().pluginClass());
    }
}
