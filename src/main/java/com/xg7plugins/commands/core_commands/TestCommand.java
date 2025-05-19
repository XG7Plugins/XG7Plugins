package com.xg7plugins.commands.core_commands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@CommandSetup(
        name = "test", description = "", syntax = "", pluginClass = XG7Plugins.class
)
public class TestCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Text.format("I like <click:SUGGEST_COMMAND:OI>eggs</click> TESTATATATA <hover:SHOW_TEXT:Opa>Q LEGAU</hover> asdfafsdfasdfa dfasdf <click:suggest_command:Legau><hover:show_text:Legau>Click e hover</hover></click> dfsgsdgsdgfdgsdgs").send(sender);
    }

    @Override
    public Item getIcon() {
        return null;
    }
}
