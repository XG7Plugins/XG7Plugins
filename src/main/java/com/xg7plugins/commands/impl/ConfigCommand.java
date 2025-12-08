package com.xg7plugins.commands.impl;

import com.github.retrooper.packetevents.protocol.dialog.Dialog;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.commands.utils.CommandState;
import org.bukkit.command.CommandSender;

@CommandSetup(
    name = "config",
    description = "Base command for configuration management.",
    syntax = "/config <edit|reload|save>",
    permission = "xg7plugins.command.config",
        pluginClass = XG7Plugins.class
)
public class ConfigCommand implements Command {


    public CommandState root(CommandSender sender) {

        return CommandState.FINE;

    }

}
