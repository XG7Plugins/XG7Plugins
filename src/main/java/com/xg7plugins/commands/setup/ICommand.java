package com.xg7plugins.commands.setup;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public interface ICommand {

    default boolean isEnabled() {return true;}

    default ISubCommand[] getSubCommands() {
        return new ISubCommand[0];
    }

    default void onCommand(Command command, CommandSender sender, String label) {
        syntaxError(sender,this.getClass().getAnnotation(com.xg7plugins.commands.setup.Command.class).syntax());
    }
    default void onCommand(Command command, Player player, String label) {
        syntaxError(player,this.getClass().getAnnotation(com.xg7plugins.commands.setup.Command.class).syntax());
    }

    default List<String> onTabComplete(Command command, CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }

    ItemBuilder getIcon();

    default void syntaxError(CommandSender sender, String syntax) {
        Text.format("lang:[commands.syntax-error]", XG7Plugins.getInstance())
                .replace("[SYNTAX]", syntax)
                .send(sender);
    }
}
