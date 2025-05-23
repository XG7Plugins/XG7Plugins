package com.xg7plugins.commands.core_commands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

import java.awt.*;

@CommandSetup(
        name = "test", description = "", syntax = "", pluginClass = XG7Plugins.class
)
public class TestCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Text.format("[CENTER:CHAT] Sou legau  <click:suggest_command:Comando>Click aqui</click> <hover:show_text:mesmo n>Aqui não</hover> <click:suggest_command:aaaaa><hover:show_text:aaaaaaaaa>Nem aqui</hover></click>").send(sender);
    }

    @Override
    public Item getIcon() {
        return null;
    }
}
